package com.insurancemegacorp.dbserver.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class QueryFilterBuilder {
    
    private static final int DEFAULT_PAGE_SIZE = 100;
    private static final int MAX_PAGE_SIZE = 1000;

    public Pageable buildPageable(Integer limit, Integer offset, String orderBy) {
        // Handle pagination
        int pageSize = (limit != null && limit > 0) ? Math.min(limit, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
        int pageNumber = (offset != null && offset >= 0) ? offset / pageSize : 0;

        // Handle sorting
        Sort sort = buildSort(orderBy);
        
        return PageRequest.of(pageNumber, pageSize, sort);
    }

    public Sort buildSort(String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "eventDate");
        }

        List<Sort.Order> orders = new ArrayList<>();
        String[] sortFields = orderBy.split(",");
        
        for (String field : sortFields) {
            field = field.trim();
            Sort.Direction direction = Sort.Direction.ASC;
            
            if (field.startsWith("-")) {
                direction = Sort.Direction.DESC;
                field = field.substring(1);
            } else if (field.startsWith("+")) {
                field = field.substring(1);
            }
            
            // Validate and map field names
            String mappedField = mapSortField(field);
            if (mappedField != null) {
                orders.add(new Sort.Order(direction, mappedField));
            }
        }
        
        if (orders.isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "eventDate");
        }
        
        return Sort.by(orders);
    }

    private String mapSortField(String field) {
        return switch (field.toLowerCase()) {
            case "date", "event_date", "eventdate" -> "eventDate";
            case "driver", "driver_id", "driverid" -> "driverId";
            case "vehicle", "vehicle_id", "vehicleid" -> "vehicleId";
            case "type", "event_type", "eventtype" -> "eventType";
            case "speed" -> "speedMph";
            case "gforce", "g_force" -> "gforce";
            case "severity" -> "severity";
            case "lat", "latitude" -> "latitude";
            case "lng", "lon", "longitude" -> "longitude";
            default -> null; // Invalid field, ignore
        };
    }

    public LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                if (dateStr.length() == 10) { // Date only
                    return LocalDateTime.parse(dateStr + " 00:00:00", 
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } else {
                    return LocalDateTime.parse(dateStr, formatter);
                }
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }

        throw new IllegalArgumentException("Invalid date format: " + dateStr + 
            ". Expected formats: yyyy-MM-dd, yyyy-MM-dd HH:mm:ss, yyyy-MM-ddTHH:mm:ss");
    }

    public String sanitizeStringFilter(String value) {
        if (value == null) return null;
        
        // Remove potentially dangerous characters
        String sanitized = value.replaceAll("[;'\"\\\\]", "");
        return sanitized.trim().isEmpty() ? null : sanitized;
    }

    public Long parseDriverId(String driverIdStr) {
        if (driverIdStr == null || driverIdStr.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(driverIdStr.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid driver_id format: " + driverIdStr);
        }
    }

    public boolean isValidEventType(String eventType) {
        if (eventType == null) return true;
        
        List<String> validTypes = List.of(
            "ACCELERATION", "DECELERATION", "TURN", "CRASH", "SPEEDING", 
            "PHONE_USAGE", "IDLE", "START", "STOP"
        );
        
        return validTypes.contains(eventType.toUpperCase());
    }

    public boolean isValidSeverity(String severity) {
        if (severity == null) return true;
        
        List<String> validSeverities = List.of("LOW", "MEDIUM", "HIGH", "CRITICAL");
        return validSeverities.contains(severity.toUpperCase());
    }

    public String parseWhereClause(String whereClause) {
        if (whereClause == null || whereClause.trim().isEmpty()) {
            return null;
        }
        
        // Simple sanitization - remove dangerous SQL keywords
        String[] dangerousKeywords = {
            "drop", "delete", "insert", "update", "alter", "create", 
            "exec", "execute", "union", "script", "javascript"
        };
        
        String sanitized = whereClause.toLowerCase();
        for (String keyword : dangerousKeywords) {
            if (sanitized.contains(keyword)) {
                throw new IllegalArgumentException("Potentially dangerous SQL keyword detected: " + keyword);
            }
        }
        
        return whereClause.trim();
    }
}