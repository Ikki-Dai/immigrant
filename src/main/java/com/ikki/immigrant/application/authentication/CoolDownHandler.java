package com.ikki.immigrant.application.authentication;

import com.ikki.immigrant.infrastructure.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author ikki
 */
@Component
@Aspect
@Slf4j
public class CoolDownHandler {

    private static final String PREFIX = "COOLDOWN:";
    private final ConcurrentHashMap<String, Integer> cache = new ConcurrentHashMap<>();
    @Resource
    RedisTemplate<String, CdVO> redisTemplate;

    private String key;

    private CoolDown coolDown;

    @Pointcut("@annotation(com.ikki.immigrant.application.authentication.CoolDown)")
    public void coolDown() {
    }

    private String getKey(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature methodSignature = ((MethodSignature) joinPoint.getSignature());
        String methodName = methodSignature.getName();
        // get from cache;
        Integer index = cache.get(methodName);
        if (null != index) {
            return String.valueOf(args[index]);
        }

        String[] argNames = methodSignature.getParameterNames();
        if (args.length == 0 || argNames.length == 0 || args.length != argNames.length) {
            throw new IllegalArgumentException(String.format("[CoolDown] Method: [%s] have no parameters or have illegal length", methodSignature.getName()));
        }
        coolDown = methodSignature.getMethod().getDeclaredAnnotation(CoolDown.class);
        if (!StringUtils.hasText(coolDown.value())) {
            throw new IllegalArgumentException(String.format("[CoolDown] Method: [%s] annotation field value can't be empty", methodSignature.getName()));
        }

        if (coolDown.interval().length != (coolDown.maxAttempts() - 1)) {
            throw new IllegalArgumentException((String.format("[CoolDown] interval length should be %d", coolDown.maxAttempts() - 1)));
        }

        index = 0;
        Object key = null;
        for (; index < args.length; index++) {
            if (coolDown.value().equals(argNames[index])) {
                key = args[index];
            }
        }
        return String.valueOf(key);
    }

    @Around("coolDown()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        key = getKey(joinPoint);
        CdVO cdVO = redisTemplate.opsForValue().get(PREFIX + key);
        // process key;
        if (null != cdVO) {
            // blocked
            if (cdVO.getAttempts() >= coolDown.maxAttempts()) {
                // wait
                long wait = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - cdVO.getLastTime());
                if (wait > coolDown.clearAfter()) {
                    redisTemplate.delete(PREFIX + key);
                } else {
                    throw new BizException("arrival max attempts, account have been locked");
                }
            } else {
                // free times has cost
                long[] interval = coolDown.interval();
                long wait = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - cdVO.getLastTime());
                int attempts = cdVO.getAttempts();
                if (wait < interval[attempts - 1]) {
                    throw new BizException(String.format("pls retry after %d seconds", wait));
                }
            }
        }
        return joinPoint.proceed();
    }

    @AfterReturning(value = "coolDown()", returning = "r")
    public void afterReturn(Object r) {
        redisTemplate.delete(PREFIX + key);
        log.info("[CoolDown] authen success and remove {}", PREFIX + key);
    }

    @AfterThrowing(value = "coolDown()", throwing = "ex")
    public void afterThrow(Exception ex) {
        CdVO cdVO = redisTemplate.opsForValue().get(PREFIX + key);
        if (null == cdVO) { // first failed
            log.debug("[CoolDown] first authen failed");
            cdVO = new CdVO();
            cdVO.setAttempts(1);
        } else { // the second time and more...
            cdVO.setAttempts(cdVO.getAttempts() + 1);
            log.debug("[CoolDown] authen failed, attempts ++1");
        }
        cdVO.setLastTime(System.currentTimeMillis());
        redisTemplate.opsForValue().set(PREFIX + key, cdVO);
    }

}
