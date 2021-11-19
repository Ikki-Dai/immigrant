package com.ikki.immigrant.interfaces;

import com.ikki.immigrant.application.qrlogin.ScanLoginService;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author ikki
 */
@WebMvcTest
class AuthenticationControllerTest {

    @MockBean
    ScanLoginService scanLoginService;

    @Autowired
    private MockMvc mvc;


    @Test
    void getTest() throws Exception {
        String clientId = RandomString.make(8);
        mvc.perform(get("/authentication/qr/" + clientId).accept(MediaType.ALL))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/authentication/qr/listen/*@*"))
                .andDo(print());
    }

}
