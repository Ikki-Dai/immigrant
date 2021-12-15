package com.ikki.immigrant.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author ikki
 */
@Getter
public class BizException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final Object[] formatParameters;

    private BizException(String message) {
        this(HttpStatus.BAD_REQUEST, null, message, null, false, false);
    }

    private BizException(HttpStatus httpStatus, String message) {
        this(httpStatus, null, message, null, false, false);
    }

    private BizException(HttpStatus httpStatus, String message, Object[] formatParameters) {
        this(httpStatus,formatParameters, message, null, false, false);
    }

    protected BizException(HttpStatus httpStatus, Object[] formatParameters, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.httpStatus = httpStatus;
        this.formatParameters = formatParameters;
    }

    public static BizException of(String message) {
        return new BizException(message);
    }

    public static BizException of(HttpStatus httpStatus, String message) {
        return new BizException(httpStatus, message);
    }

    public static BizException of(HttpStatus httpStatus, String message, Object[] args) {
        return new BizException(httpStatus, message, args);
    }


}
