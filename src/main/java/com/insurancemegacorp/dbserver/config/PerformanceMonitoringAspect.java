package com.insurancemegacorp.dbserver.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceMonitoringAspect {
    
    private static final Logger logger = LoggerFactory.getLogger("PERFORMANCE_LOGGER");

    @Around("execution(* com.insurancemegacorp.dbserver.service.*.*(..))")
    public Object monitorServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorExecution(joinPoint, "SERVICE");
    }

    @Around("execution(* com.insurancemegacorp.dbserver.repository.*.*(..))")
    public Object monitorRepositoryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorExecution(joinPoint, "REPOSITORY");
    }

    @Around("execution(* com.insurancemegacorp.dbserver.controller.*.*(..))")
    public Object monitorControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorExecution(joinPoint, "CONTROLLER");
    }

    private Object monitorExecution(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Log all method calls for debugging
            logger.debug("{} - {}.{} executed in {}ms", 
                layer, className, methodName, executionTime);
            
            // Log slow methods as warnings
            if (executionTime > 1000) { // 1 second
                logger.warn("Slow {} method: {}.{} took {}ms", 
                    layer, className, methodName, executionTime);
            }
            
            return result;
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("{} - {}.{} failed after {}ms: {}", 
                layer, className, methodName, executionTime, e.getMessage());
            throw e;
        }
    }
}