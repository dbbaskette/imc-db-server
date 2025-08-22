package com.insurancemegacorp.dbserver.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Configuration
public class SecurityConfig {

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilter() {
        FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitingFilter());
        registrationBean.addUrlPatterns("/api/*/ml/recalculate", "/api/*/vehicle-events/batch");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<InputSanitizationFilter> inputSanitizationFilter() {
        FilterRegistrationBean<InputSanitizationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new InputSanitizationFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    public static class RateLimitingFilter extends OncePerRequestFilter {
        
        private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
        private static final int MAX_REQUESTS_PER_MINUTE = 10;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                       FilterChain filterChain) throws ServletException, IOException {
            
            String clientIp = getClientIpAddress(request);
            String key = clientIp + ":" + request.getRequestURI();
            
            RateLimitInfo info = rateLimitMap.computeIfAbsent(key, k -> new RateLimitInfo());
            
            if (info.isLimitExceeded()) {
                response.setStatus(429); // HTTP 429 Too Many Requests
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Rate limit exceeded. Too many requests.\", \"success\": false}");
                return;
            }
            
            info.incrementCount();
            filterChain.doFilter(request, response);
        }

        private String getClientIpAddress(HttpServletRequest request) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        }

        private static class RateLimitInfo {
            private final AtomicInteger count = new AtomicInteger(0);
            private LocalDateTime windowStart = LocalDateTime.now();

            boolean isLimitExceeded() {
                LocalDateTime now = LocalDateTime.now();
                if (ChronoUnit.MINUTES.between(windowStart, now) >= 1) {
                    // Reset window
                    windowStart = now;
                    count.set(0);
                    return false;
                }
                return count.get() >= MAX_REQUESTS_PER_MINUTE;
            }

            void incrementCount() {
                count.incrementAndGet();
            }
        }
    }

    public static class InputSanitizationFilter extends OncePerRequestFilter {
        
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                       FilterChain filterChain) throws ServletException, IOException {
            
            // Check for suspicious patterns in parameters
            for (String paramName : request.getParameterMap().keySet()) {
                String[] values = request.getParameterValues(paramName);
                for (String value : values) {
                    if (containsSuspiciousContent(value)) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Invalid request parameters detected\", \"success\": false}");
                        return;
                    }
                }
            }
            
            filterChain.doFilter(request, response);
        }

        private boolean containsSuspiciousContent(String value) {
            if (value == null) return false;
            
            String lowerValue = value.toLowerCase();
            String[] suspiciousPatterns = {
                "<script", "javascript:", "vbscript:", "onload=", "onerror=",
                "drop table", "delete from", "insert into", "update set",
                "../", "..\\", "file://", "http://", "https://",
                "exec(", "eval(", "system(", "cmd.exe", "/bin/sh"
            };
            
            for (String pattern : suspiciousPatterns) {
                if (lowerValue.contains(pattern)) {
                    return true;
                }
            }
            
            return false;
        }
    }
}