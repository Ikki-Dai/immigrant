package com.ikki.immigrant.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author ikki
 */
@Slf4j
class BizExceptionTest {

    @Test
    void stackTraceWritable() throws Exception {
        Exception e = BizException.of("test");
        System.out.println(e.getStackTrace().length);
        Assertions.assertFalse(e.getStackTrace().length > 1);
    }

    @Test
    void stackTraceUnWritable() throws Exception {
        Exception e = BizException.of("test");
        System.out.println(e.getStackTrace().length);
        Assertions.assertEquals(0, e.getStackTrace().length);
    }

}
