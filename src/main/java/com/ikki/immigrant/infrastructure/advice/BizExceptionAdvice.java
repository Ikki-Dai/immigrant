package com.ikki.immigrant.infrastructure.advice;

import com.ikki.immigrant.infrastructure.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author ikki
 */
@Slf4j
@RestControllerAdvice
public class BizExceptionAdvice extends BasicErrorController {

    private final MessageSource messageSource;

    public BizExceptionAdvice(MessageSource messageSource, ErrorAttributes errorAttributes, ServerProperties serverProperties) {
        super(errorAttributes, serverProperties.getError());
        this.messageSource = messageSource;
    }

    /**
     * status, http code will be over-ride by spring framework
     *
     * @param bizException
     * @param request
     * @return
     */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<Map<String, Object>> handleBizExcetion(BizException bizException, HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request, this.getErrorAttributeOptions(request, MediaType.ALL));
        String s;
        try {
            s = messageSource.getMessage(bizException.getMessage(), bizException.getFormatParameters(), request.getLocale());
        } catch (NoSuchMessageException e) {
            log.warn(e.getMessage());
            s = bizException.getMessage();
        }
        body.put("status", bizException.getHttpStatus().value());
        body.put("tips", s);
        return new ResponseEntity<>(body, bizException.getHttpStatus());
    }


}
