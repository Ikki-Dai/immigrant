package com.ikki.immigrant.infrastructure.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SensitiveSerializerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void test() throws JsonProcessingException {
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
        @JsonSerialize(using = SensitiveSerializer.class)
        @SensitiveMask(start = 3, end = -4)
        private String phone;

        @JsonSerialize(using = SensitiveSerializer.class)
        @SensitiveMask(start = 2, end = -6)
        private String email;
    }

}
