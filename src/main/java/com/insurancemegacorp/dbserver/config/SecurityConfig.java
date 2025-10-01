package com.insurancemegacorp.dbserver.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private SecurityProperties securityProperties;

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilter() {
        FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitingFilter(securityProperties));
        registrationBean.addUrlPatterns(securityProperties.getFilters().getUrlPattern());
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<InputSanitizationFilter> inputSanitizationFilter() {
        FilterRegistrationBean<InputSanitizationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new InputSanitizationFilter());
        registrationBean.addUrlPatterns(securityProperties.getFilters().getUrlPattern());
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CorsFilter(securityProperties));
        registrationBean.addUrlPatterns(securityProperties.getFilters().getUrlPattern());
        registrationBean.setOrder(0); // CORS should be first
        return registrationBean;
    }

    public static class RateLimitingFilter extends OncePerRequestFilter {

        private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
        private final SecurityProperties securityProperties;

        public RateLimitingFilter(SecurityProperties securityProperties) {
            this.securityProperties = securityProperties;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                       FilterChain filterChain) throws ServletException, IOException {
            
            String requestURI = request.getRequestURI();

            // Only apply rate limiting to configured endpoints
            boolean shouldApplyRateLimit = securityProperties.getRateLimiting().getEnabledEndpoints()
                .stream()
                .anyMatch(requestURI::contains);

            if (!shouldApplyRateLimit) {
                filterChain.doFilter(request, response);
                return;
            }
            
            String clientIp = getClientIpAddress(request);
            String key = clientIp + ":" + requestURI;
            
            RateLimitInfo info = rateLimitMap.computeIfAbsent(key, k -> new RateLimitInfo(securityProperties.getRateLimiting().getMaxRequestsPerMinute()));
            
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
            private final int maxRequestsPerMinute;
            private LocalDateTime windowStart = LocalDateTime.now();

            public RateLimitInfo(int maxRequestsPerMinute) {
                this.maxRequestsPerMinute = maxRequestsPerMinute;
            }

            boolean isLimitExceeded() {
                LocalDateTime now = LocalDateTime.now();
                long minutesBetween = ChronoUnit.MINUTES.between(windowStart, now);

                if (minutesBetween >= 1) {
                    // Reset window
                    windowStart = now;
                    count.set(0);
                    return false;
                }

                return count.get() >= maxRequestsPerMinute;
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

    public static class CorsFilter extends OncePerRequestFilter {

        private final SecurityProperties securityProperties;

        public CorsFilter(SecurityProperties securityProperties) {
            this.securityProperties = securityProperties;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                       FilterChain filterChain) throws ServletException, IOException {

            // Set CORS headers based on configuration
            String origin = request.getHeader("Origin");
            if (origin != null && securityProperties.getCors().getAllowedOrigins().contains(origin)) {
                response.setHeader("Access-Control-Allow-Origin", origin);
            }
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, Origin, Authorization, X-Requested-With");
            if (securityProperties.getCors().isAllowCredentials()) {
                response.setHeader("Access-Control-Allow-Credentials", "true");
            }
            response.setHeader("Access-Control-Max-Age", String.valueOf(securityProperties.getCors().getMaxAge()));
            
            // Handle preflight OPTIONS request
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            
            filterChain.doFilter(request, response);
        }
    }
}