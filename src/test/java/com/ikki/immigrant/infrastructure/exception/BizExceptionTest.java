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
        Exception e = new BizException("test", null, false, true);
        System.out.println(e.getStackTrace().length);
        Assertions.assertTrue(e.getStackTrace().length > 1);
    }

    @Test
    void stackTraceUnWritable() throws Exception {
        Exception e = new BizException("test", null, false, false);
        System.out.println(e.getStackTrace().length);
        Assertions.assertEquals(0, e.getStackTrace().length);
    }

}
