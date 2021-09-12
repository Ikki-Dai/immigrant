package com.ikki.immigrant.interfaces;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class SubjectControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void getTest() throws Exception {
        mvc.perform(get("/subject/helloWorld").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andDo(print());
    }


}
