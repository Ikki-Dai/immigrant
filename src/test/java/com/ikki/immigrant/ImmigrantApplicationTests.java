package com.ikki.immigrant;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.support.DefaultConversionService;

@SpringBootTest
class ImmigrantApplicationTests {

    @Test
    void contextLoads() {
        boolean b = DefaultConversionService.getSharedInstance()
                .canConvert(Enum.class, Integer.class);
        System.out.println(b);
    }

}
