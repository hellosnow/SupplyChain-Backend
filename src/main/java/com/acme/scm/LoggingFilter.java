package com.acme.scm;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("LoggingFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (isHttpServletRequest(request)) {
            log.info("LoggingFilter: HTTP request detected");
        } else {
            log.info("LoggingFilter: NOT an HTTP request");
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        log.info("LoggingFilter destroyed");
    }

    private static boolean isHttpServletRequest(Object obj) {
        try {
            Class<?> clazz = Class.forName("javax.servlet.http.HttpServletRequest");
            return clazz.isInstance(obj);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}