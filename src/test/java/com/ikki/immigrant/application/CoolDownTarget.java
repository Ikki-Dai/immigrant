package com.ikki.immigrant.application;

import com.ikki.immigrant.application.authentication.CoolDown;
import com.ikki.immigrant.infrastructure.exception.BizException;
import org.springframework.stereotype.Service;

/**
 * @author ikki
 */
@Service
public class CoolDownTarget {

    @CoolDown("sub")
    public String loginSuccess(String sub) {
        return "success";
    }

    @CoolDown("sub")
    public String loginFailed(String sub) {
        throw new BizException("login failed;");
    }


}