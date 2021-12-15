package com.ikki.immigrant.infrastructure.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua_parser.CachingParser;
import ua_parser.Client;
import ua_parser.Parser;

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
public class UserAgentFilter extends OncePerRequestFilter {

    private final Parser parser = new CachingParser();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String ua = request.getHeader(HttpHeaders.USER_AGENT);
            Client client = parser.parse(ua);
            CurrentUtil.addUserAgent(client);
            filterChain.doFilter(request, response);
        } finally {
            CurrentUtil.removeUserAgent();
        }


    }
}
