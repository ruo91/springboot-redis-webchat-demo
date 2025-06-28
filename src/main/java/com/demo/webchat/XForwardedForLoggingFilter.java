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

        // Original remoteAddr
        String remoteAddr = httpRequest.getRemoteAddr();

        // X-Forwarded-For: "client, proxy1, proxy2"
        String xForwardedFor = httpRequest.getHeader("X-Forwarded-For");
        String realClientIp;

        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            realClientIp = xForwardedFor.split(",")[0].trim();
        } else {
            realClientIp = remoteAddr;
        }

        String protocol = httpRequest.getHeader("X-Forwarded-Proto");
        String host = httpRequest.getHeader("X-Forwarded-Host");
        String port = httpRequest.getHeader("X-Forwarded-Port");
        String userAgent = httpRequest.getHeader("User-Agent");
        String referer = httpRequest.getHeader("Referer");

        // Logging
        logger.info("==== INCOMING REQUEST INFO ====");
        logger.info("Real Client IP     : {}", realClientIp);
        logger.info("RemoteAddr         : {}", remoteAddr);
        logger.info("Referer            : {}", referer);
        logger.info("User-Agent         : {}", userAgent);
        logger.info("===============================");

        chain.doFilter(request, response);
    }
}
