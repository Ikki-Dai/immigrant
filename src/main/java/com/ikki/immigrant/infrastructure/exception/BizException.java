package com.ikki.immigrant.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author ikki
 */
@Getter
public class BizException extends RuntimeException {

    private HttpStatus httpStatus;
    private Object[] formatParameters;

    public BizException(String message) {
        this(message, null, false, false);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BizException(HttpStatus httpStatus, String message) {
        this(message, null, false, false);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public BizException(HttpStatus httpStatus, String message, Object[] formatParameters) {
        this(message, null, false, false);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.formatParameters = formatParameters;
    }

    protected BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static BizException of(HttpStatus httpStatus, String message) {
        return new BizException(httpStatus, message);
    }

    public static BizException of(HttpStatus httpStatus, String code, Object[] args) {
        return new BizException(httpStatus, code, args);
    }


}
