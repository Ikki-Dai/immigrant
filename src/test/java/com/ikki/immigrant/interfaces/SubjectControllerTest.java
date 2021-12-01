package com.ikki.immigrant.interfaces;

import com.ikki.immigrant.application.qrlogin.ScanLoginService;
import com.ikki.immigrant.infrastructure.advice.BizExceptionAdvice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(BizExceptionAdvice.class)
class SubjectControllerTest {
    @MockBean
    ScanLoginService scanLoginService;
    @Autowired
    private MockMvc mvc;

    @Test
    void getTest() throws Exception {
        mvc.perform(get("/subject/helloWorld").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void getTest1() throws Exception {
        mvc.perform(get("/subject/test").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(409))
                .andDo(print());
    }

}
