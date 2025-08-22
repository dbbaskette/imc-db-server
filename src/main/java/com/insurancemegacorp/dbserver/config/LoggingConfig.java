package com.insurancemegacorp.dbserver.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Configuration
public class LoggingConfig {

    @Bean
    public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilter() {
        FilterRegistrationBean<CorrelationIdFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CorrelationIdFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(0); // Highest priority
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<RequestResponseLoggingFilter> requestResponseLoggingFilter() {
        FilterRegistrationBean<RequestResponseLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestResponseLoggingFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(3);
        return registrationBean;
    }

    public static class CorrelationIdFilter extends OncePerRequestFilter {
        
        private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
        private static final String CORRELATION_ID_MDC_KEY = "correlationId";

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                       FilterChain filterChain) throws ServletException, IOException {
            
            String correlationId = request.getHeader(CORRELATION_ID_HEADER);
            if (correlationId == null || correlationId.trim().isEmpty()) {
                correlationId = generateCorrelationId();
            }
            
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
            response.setHeader(CORRELATION_ID_HEADER, correlationId);
            
            try {
                filterChain.doFilter(request, response);
            } finally {
                MDC.remove(CORRELATION_ID_MDC_KEY);
            }
        }

        private String generateCorrelationId() {
            return UUID.randomUUID().toString().substring(0, 8);
        }
    }

    public static class RequestResponseLoggingFilter extends OncePerRequestFilter {
        
        private static final Logger logger = LoggerFactory.getLogger("REQUEST_RESPONSE_LOGGER");

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                       FilterChain filterChain) throws ServletException, IOException {
            
            long startTime = System.currentTimeMillis();
            
            // Log incoming request
            logRequest(request);
            
            try {
                filterChain.doFilter(request, response);
            } finally {
                // Log response
                long duration = System.currentTimeMillis() - startTime;
                logResponse(request, response, duration);
            }
        }

        private void logRequest(HttpServletRequest request) {
            logger.info("Incoming request: {} {} from {} - Query: {}", 
                request.getMethod(),
                request.getRequestURI(), 
                getClientIpAddress(request),
                request.getQueryString() != null ? request.getQueryString() : "none"
            );
        }

        private void logResponse(HttpServletRequest request, HttpServletResponse response, long duration) {
            logger.info("Outgoing response: {} {} - Status: {} - Duration: {}ms", 
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration
            );
            
            // Log slow requests
            if (duration > 5000) { // 5 seconds
                logger.warn("Slow request detected: {} {} took {}ms", 
                    request.getMethod(), request.getRequestURI(), duration);
            }
        }

        private String getClientIpAddress(HttpServletRequest request) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        }
    }
}