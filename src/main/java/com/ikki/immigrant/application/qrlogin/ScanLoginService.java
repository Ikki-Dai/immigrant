package com.ikki.immigrant.application.qrlogin;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author ikki
 */
public interface ScanLoginService {
    SseEmitter connect(String clientId);

    SseEmitter reconnect(String clientId, Long lastEventId);

    boolean publish(String clientId, SseValueObject sseValueObject);
}
