package com.ikki.immigrant.infrastructure.filter;

import com.ikki.immigrant.infrastructure.exception.BizException;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ikki
 */
@WebFilter
@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final String BASIC_AUTH = "Basic ";
    private static final String BEARER = "Bearer ";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasLength(auth)) {
            if (StringUtils.startsWithIgnoreCase(auth, BASIC_AUTH)) {
                // todo analysis basic auth
                CurrentUtil.addTenant("");
                filterChain.doFilter(request, response);
                CurrentUtil.removeTenant();
            } else if (StringUtils.startsWithIgnoreCase(auth, BEARER)) {
                // todo analysis bearer auth
                CurrentUtil.addJwtClaimsSet(new JWTClaimsSet.Builder().build());
                filterChain.doFilter(request, response);
                CurrentUtil.removeClaimsSet();
            } else {
                throw BizException.of(HttpStatus.UNAUTHORIZED, "UnSupport auth token type");
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
