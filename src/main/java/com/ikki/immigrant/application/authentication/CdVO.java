package com.ikki.immigrant.application.authentication;

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
public class CdVO implements Serializable {
    private int attempts;
    private long lastTime;
}
