package com.ikki.immigrant.infrastructure.filter;

import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua_parser.Client;
import ua_parser.Parser;

/**
 * @author ikki
 */
class CurrentUtilTest {


    @Test
    void uaTest() {
        String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.55 Safari/537.36 Edg/96.0.1054.43";
        Parser parser = new Parser();
        Client client = parser.parse(ua);

        CurrentUtil.addUserAgent(client);

        Client c = CurrentUtil.getUserAgent();
        Assertions.assertNotNull(c);

        CurrentUtil.removeUserAgent();
        Assertions.assertNull(CurrentUtil.getUserAgent());
    }

    @Test
    void tenantTest() {
        CurrentUtil.addTenant(RandomString.make(10));
        Assertions.assertNotNull(CurrentUtil.getTenant());
        CurrentUtil.removeTenant();
        Assertions.assertNull(CurrentUtil.getTenant());

    }

}
