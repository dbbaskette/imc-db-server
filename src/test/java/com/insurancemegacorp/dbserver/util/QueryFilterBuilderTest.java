package com.insurancemegacorp.dbserver.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class QueryFilterBuilderTest {

    private QueryFilterBuilder queryFilterBuilder;

    @BeforeEach
    void setUp() {
        queryFilterBuilder = new QueryFilterBuilder();
    }

    @Test
    void testBuildPageable_DefaultValues() {
        // When
        Pageable pageable = queryFilterBuilder.buildPageable(null, null, null);

        // Then
        assertEquals(100, pageable.getPageSize());
        assertEquals(0, pageable.getPageNumber());
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor("eventDate").getDirection());
    }

    @Test
    void testBuildPageable_CustomValues() {
        // When
        Pageable pageable = queryFilterBuilder.buildPageable(50, 200, "driverId");

        // Then
        assertEquals(50, pageable.getPageSize());
        assertEquals(4, pageable.getPageNumber()); // 200/50 = 4
        assertEquals(Sort.Direction.ASC, pageable.getSort().getOrderFor("driverId").getDirection());
    }

    @Test
    void testBuildPageable_ExceedsMaxLimit() {
        // When
        Pageable pageable = queryFilterBuilder.buildPageable(2000, null, null);

        // Then
        assertEquals(1000, pageable.getPageSize()); // Should be capped at max
    }

    @Test
    void testBuildSort_DescendingPrefix() {
        // When
        Sort sort = queryFilterBuilder.buildSort("-eventDate,+driverId");

        // Then
        assertEquals(2, sort.stream().count());
        assertEquals(Sort.Direction.DESC, sort.getOrderFor("eventDate").getDirection());
        assertEquals(Sort.Direction.ASC, sort.getOrderFor("driverId").getDirection());
    }

    @Test
    void testParseDateTime_ValidFormats() {
        // Test various date formats
        LocalDateTime result1 = queryFilterBuilder.parseDateTime("2024-08-22");
        LocalDateTime result2 = queryFilterBuilder.parseDateTime("2024-08-22 14:30:00");
        LocalDateTime result3 = queryFilterBuilder.parseDateTime("2024-08-22T14:30:00");

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertEquals(0, result1.getHour()); // Date only should default to 00:00:00
        assertEquals(14, result2.getHour());
        assertEquals(30, result2.getMinute());
    }

    @Test
    void testParseDateTime_InvalidFormat() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            queryFilterBuilder.parseDateTime("invalid-date");
        });
    }

    @Test
    void testParseDateTime_NullOrEmpty() {
        // When & Then
        assertNull(queryFilterBuilder.parseDateTime(null));
        assertNull(queryFilterBuilder.parseDateTime(""));
        assertNull(queryFilterBuilder.parseDateTime("   "));
    }

    @Test
    void testSanitizeStringFilter() {
        // When
        String result1 = queryFilterBuilder.sanitizeStringFilter("valid_value");
        String result2 = queryFilterBuilder.sanitizeStringFilter("invalid';DROP TABLE--");
        String result3 = queryFilterBuilder.sanitizeStringFilter("   ");
        String result4 = queryFilterBuilder.sanitizeStringFilter(null);

        // Then
        assertEquals("valid_value", result1);
        assertEquals("invalidDROP TABLE--", result2); // Dangerous chars removed
        assertNull(result3); // Empty after trim
        assertNull(result4); // Null input
    }

    @Test
    void testParseDriverId_Valid() {
        // When
        Long result = queryFilterBuilder.parseDriverId("12345");

        // Then
        assertEquals(12345L, result);
    }

    @Test
    void testParseDriverId_Invalid() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            queryFilterBuilder.parseDriverId("not-a-number");
        });
    }

    @Test
    void testParseDriverId_NullOrEmpty() {
        // When & Then
        assertNull(queryFilterBuilder.parseDriverId(null));
        assertNull(queryFilterBuilder.parseDriverId(""));
        assertNull(queryFilterBuilder.parseDriverId("   "));
    }

    @Test
    void testIsValidEventType() {
        // When & Then
        assertTrue(queryFilterBuilder.isValidEventType("CRASH"));
        assertTrue(queryFilterBuilder.isValidEventType("ACCELERATION"));
        assertTrue(queryFilterBuilder.isValidEventType("crash")); // Case insensitive
        assertFalse(queryFilterBuilder.isValidEventType("INVALID_TYPE"));
        assertTrue(queryFilterBuilder.isValidEventType(null)); // Null is valid (no filter)
    }

    @Test
    void testIsValidSeverity() {
        // When & Then
        assertTrue(queryFilterBuilder.isValidSeverity("HIGH"));
        assertTrue(queryFilterBuilder.isValidSeverity("low")); // Case insensitive
        assertTrue(queryFilterBuilder.isValidSeverity("MEDIUM"));
        assertFalse(queryFilterBuilder.isValidSeverity("INVALID"));
        assertTrue(queryFilterBuilder.isValidSeverity(null)); // Null is valid
    }

    @Test
    void testParseWhereClause_Safe() {
        // When
        String result = queryFilterBuilder.parseWhereClause("driver_id = 123 AND event_type = 'CRASH'");

        // Then
        assertEquals("driver_id = 123 AND event_type = 'CRASH'", result);
    }

    @Test
    void testParseWhereClause_Dangerous() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            queryFilterBuilder.parseWhereClause("driver_id = 123; DROP TABLE users;");
        });
    }

    @Test
    void testParseWhereClause_NullOrEmpty() {
        // When & Then
        assertNull(queryFilterBuilder.parseWhereClause(null));
        assertNull(queryFilterBuilder.parseWhereClause(""));
        assertNull(queryFilterBuilder.parseWhereClause("   "));
    }
}