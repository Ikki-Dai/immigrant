package com.ikki.immigrant.infrastructure.i18n;

import com.ikki.immigrant.application.qrlogin.ScanLoginService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * @author ikki
 */
@WebMvcTest
class I18nTest {

    @MockBean
    ScanLoginService scanLoginService;

    @Autowired
    MessageSource messageSource;

    @BeforeEach
    void prepare() {
        Assertions.assertNotNull(messageSource);
    }

    @Test
    void messageSourceTest() {
        String s = messageSource.getMessage("test_key", null, Locale.SIMPLIFIED_CHINESE);
        System.out.println(s);
        Assertions.assertEquals("测试消息", s);
    }

    @Test
    void messageSourceWithArgsTest() {
        String s = messageSource.getMessage("test_args", new Object[]{"客户"}, Locale.SIMPLIFIED_CHINESE);
        System.out.println(s);
        Assertions.assertEquals("亲爱的 客户", s);
    }

}
