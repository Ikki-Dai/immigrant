package com.ikki.immigrant.infrastructure.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikki.immigrant.application.qrlogin.ScanLoginService;
import com.ikki.immigrant.infrastructure.advice.BizExceptionAdvice;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@WebMvcTest
@Import(BizExceptionAdvice.class)
class SensitiveSerializerTest {

    @MockBean
    ScanLoginService scanLoginService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void test() throws JsonProcessingException {
        User user = new User();
        user.setEmail("undewtow@redhat.com");
        user.setPhone("13056781234");
        String s = objectMapper.writeValueAsString(user);
        System.out.println(s);
        Assertions.assertTrue(s.contains("*"));
    }

    @Getter
    @Setter
    public static class User {
        @SensitiveMask(prefixLength = 3, suffixLength = 4)
        private String phone;

        @SensitiveMask(prefixLength = 2, suffixLength = -6)
        private String email;
    }

}
