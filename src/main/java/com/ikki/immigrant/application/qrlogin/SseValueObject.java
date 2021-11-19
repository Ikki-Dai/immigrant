package com.ikki.immigrant.application.qrlogin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


/**
 * @author ikki
 */
@Getter
@Setter
@ToString
public class SseValueObject implements Serializable {

    private String clientId;
    private String id;
    private EventName name;
    private String data;

    enum EventName {
        PING,
        SCAN,
        CONFIRM,
        CANCEL,
        FINISH,
        SECURITY
    }
}
