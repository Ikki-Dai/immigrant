package com.ikki.immigrant.infrastructure.filter;

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
@Component
@WebFilter
public class XffFilter extends OncePerRequestFilter {

    private static final String X_FORWARD_FOR = "X-Forwarded-For";

    private static final String X_REAL_IP = "X-Real-IP";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = tryGetIP(request);
        CurrentUtil.addIpAddress(ip);
        filterChain.doFilter(request, response);
        CurrentUtil.removeIpAddress();
    }

    private String tryGetIP(HttpServletRequest request) {
        // x-forwarded-for
        String xff = request.getHeader(X_FORWARD_FOR);
        if (StringUtils.hasLength(xff)) {
            String[] ips = xff.split(",", 2);
            if (ips.length > 1) {
                return ips[0].trim();
            }
        }
        // x-real-ip
        xff = request.getHeader(X_REAL_IP);
        if (StringUtils.hasLength(xff)) {
            return xff;
        }
        // remote address
        xff = request.getRemoteAddr();
        if (StringUtils.hasLength(xff) && 4 == xff.split("\\.").length) {
            return xff;
        }
        return "127.0.0.1";
    }

}
