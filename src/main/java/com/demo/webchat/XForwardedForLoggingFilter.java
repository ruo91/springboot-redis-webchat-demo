package com.demo.webchat;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class XForwardedForLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(XForwardedForLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String xForwardedFor = httpRequest.getHeader("X-Forwarded-For");
        String remoteAddr = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");
        String referer = httpRequest.getHeader("Referer");

        //logger.info("Request from IP: {}, X-Forwarded-For: {}", remoteAddr, xForwardedFor);
        logger.info("IP: {}, X-Forwarded-For: {}, Referer: {}, User-Agent: {}", remoteAddr, xForwardedFor, referer, userAgent);

        chain.doFilter(request, response);
    }
}
