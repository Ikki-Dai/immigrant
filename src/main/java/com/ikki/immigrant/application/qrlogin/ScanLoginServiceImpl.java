package com.ikki.immigrant.application.qrlogin;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RReliableTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author ikki
 */
@Slf4j
@Component
public class ScanLoginServiceImpl implements ScanLoginService {

    private static final String BROKER_REGISTRY = "BROKER-REGISTRY";
    private static final String SSE_REDIRECT_REGISTRY = "SSE-REDIRECT-REGISTRY";
    private static final String SSE_BACKUP = "SSE-BACKUP:";

    private final String brokerId;
    private final long sseTimeout;
    private final long topicDelayThreshold;
    private final RedissonClient redisson;
    private final String channel;
    private final RMap<String, BrokerStats> brokerRegistry;
    /**
     * to known which broker the client redirect to
     * then broker will publish message to the correct broker;
     */
    private final RMap<String, String> clientRedirectAddress;
    /**
     * local SseEmitter handler
     */
    private final Map<String, SseEmitter> sseClientHandler;

    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public ScanLoginServiceImpl(@Value("${qr-login.timeout:90000}") long timeout,
                                @Value("${qr-login.timeout:90000}") long topicDelayThreshold,
                                ThreadPoolTaskExecutor threadPoolTaskExecutor,
                                RedissonClient redisson) {
        this.sseTimeout = timeout;
        this.topicDelayThreshold = topicDelayThreshold;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.redisson = redisson;
        // init container
        sseClientHandler = new ConcurrentHashMap<>();
        brokerRegistry = redisson.getMap(BROKER_REGISTRY);
        clientRedirectAddress = redisson.getMap(SSE_REDIRECT_REGISTRY, StringCodec.INSTANCE);
        /**
         * registry current server instance as a broker
         */
        brokerId = checkAndRegistryBroker();
        channel = "SSE-" + brokerId;
    }

    private String checkAndRegistryBroker() {
        String bId = null;
        for (int i = 0; i < 3; i++) {
            bId = RandomString.make(10);
            boolean b = brokerRegistry.fastPutIfAbsent(bId, BrokerStats.REGISTER);
            if (b) {
                break;
            }
        }
        if (!StringUtils.hasText(bId)) {
            throw new IllegalArgumentException("tried 3 times for register sse channel failed pls alter large range for channel id");
        }
        return bId;
    }

    @PostConstruct
    void init() {
        RReliableTopic reliableTopic = redisson.getReliableTopic(channel);

        reliableTopic.addListener(SseValueObject.class, new MessageListener<SseValueObject>() {
            @Override
            public void onMessage(CharSequence channel, SseValueObject msg) {
                SseEmitter sseEmitter = sseClientHandler.get(msg.getClientId());
                if (null != sseEmitter) {
                    // check message delay
                    long delay = System.currentTimeMillis() - Long.parseLong(msg.getId());
                    if (delay > topicDelayThreshold) {
                        log.warn("redis topic: {} message delayed arrive at {}", channel, delay);
                    }
                    send(sseEmitter, msg);
                } else {
                    // backup and keep 5 minutes
                    RList<SseValueObject> list = redisson.getList(SSE_BACKUP + msg.getClientId());
                    list.expire(5, TimeUnit.MINUTES);
                    list.add(msg);
                }
            }
        });
        // listen setup success
        brokerRegistry.putIfExists(brokerId, BrokerStats.SERVING);
    }

    void send(SseEmitter sseEmitter, SseValueObject sseValueObject) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .name(sseValueObject.getName().toString())
                    .id(sseValueObject.getId())
                    .build()
            );
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    @Override
    public SseEmitter connect(String clientId) {
        return openSseEmitter(clientId);
    }

    @Override
    public SseEmitter reconnect(String clientId, Long lastEventId) {
        SseEmitter sseEmitter = openSseEmitter(clientId);
        threadPoolTaskExecutor.submit(() -> {
            // find backup message and send out
            RList<SseValueObject> list = redisson.getList(SSE_BACKUP + clientId);
            for (SseValueObject svo : list) {
                if (Long.parseLong(svo.getId()) > lastEventId) {
                    send(sseEmitter, svo);
                }
            }
        });
        return sseEmitter;
    }

    @Override
    public boolean publish(String clientId, SseValueObject sseValueObject) {
        String bId = clientRedirectAddress.get(clientId);
        BrokerStats stats = brokerRegistry.get(bId);
        if (stats != BrokerStats.SERVING) {
            return false;
        }
        // try to publish
        RReliableTopic topic = redisson.getReliableTopic(channel);
        topic.publish(sseValueObject);
        return true;
    }


    SseEmitter openSseEmitter(String clientId) {
        SseEmitter sseEmitter = new SseEmitter(sseTimeout);

        sseEmitter.onCompletion(() -> {
            sseClientHandler.remove(clientId);
            log.info("ServerSentEvent: {} completed", clientId);
        });

        sseEmitter.onTimeout(() -> {
            sseClientHandler.remove(clientId);
            log.info("ServerSentEvent {} timeout", clientId);
        });

        /**
         * WARN: !!!!!!!!!!!!!!!
         * while client registry to topic failed
         * in case of the another client connect to server wait for your token
         */
        boolean b = clientRedirectAddress.fastPutIfAbsent(clientId, brokerId);
        // already register into broker
        if (!b) {
            threadPoolTaskExecutor.submit(() -> {
                try {
                    sseEmitter.send(SseEmitter.event()
                            .name(SseValueObject.EventName.SECURITY.name())
                            .id(String.valueOf(System.currentTimeMillis()))
                            .build()
                    );
                } catch (IOException e) {
                    log.warn(e.getMessage());
                }
                sseEmitter.complete();
            });
        } else {
            // note current broker's client
            sseClientHandler.put(clientId, sseEmitter);
        }

        return sseEmitter;
    }

    @PreDestroy
    void destroy() throws InterruptedException {
        brokerRegistry.putIfExists(brokerId, BrokerStats.TERMINATING);
        log.info("wait all client finished");
        while (sseClientHandler.size() > 0) {
            Thread.sleep(1000L);
        }
        brokerRegistry.remove(brokerId);
    }


    enum BrokerStats {
        REGISTER, SERVING, TERMINATING,
    }


}
