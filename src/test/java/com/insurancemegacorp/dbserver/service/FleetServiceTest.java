package com.insurancemegacorp.dbserver.service;

import com.insurancemegacorp.dbserver.dto.DriverPerformanceDto;
import com.insurancemegacorp.dbserver.dto.FleetSummaryDto;
import com.insurancemegacorp.dbserver.repository.SafeDriverScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FleetServiceTest {

    @Mock
    private SafeDriverScoreRepository safeDriverScoreRepository;

    @InjectMocks
    private FleetService fleetService;

    @BeforeEach
    void setUp() {
        // Common setup if needed
    }

    @Test
    void testGetFleetSummary() {
        // Given
        FleetSummaryDto expectedSummary = new FleetSummaryDto(
            1250L, 
            new BigDecimal("78.5"), 
            125L, 
            8L, 
            new BigDecimal("2.3")
        );
        when(safeDriverScoreRepository.getFleetSummary()).thenReturn(expectedSummary);

        // When
        FleetSummaryDto result = fleetService.getFleetSummary();

        // Then
        assertNotNull(result);
        assertEquals(1250L, result.getTotalDrivers());
        assertEquals(new BigDecimal("78.5"), result.getAverageSafetyScore());
        assertEquals(125L, result.getHighRiskCount());
        verify(safeDriverScoreRepository).getFleetSummary();
    }

    @Test
    void testGetActiveDriversCount() {
        // Given
        long expectedCount = 1180L;
        when(safeDriverScoreRepository.countActiveDrivers()).thenReturn(expectedCount);

        // When
        long result = fleetService.getActiveDriversCount();

        // Then
        assertEquals(expectedCount, result);
        verify(safeDriverScoreRepository).countActiveDrivers();
    }

    @Test
    void testGetHighRiskDriversCount() {
        // Given
        long expectedCount = 125L;
        when(safeDriverScoreRepository.countHighRiskDrivers()).thenReturn(expectedCount);

        // When
        long result = fleetService.getHighRiskDriversCount();

        // Then
        assertEquals(expectedCount, result);
        verify(safeDriverScoreRepository).countHighRiskDrivers();
    }

    @Test
    void testGetTopPerformers() {
        // Given
        int limit = 10;
        List<DriverPerformanceDto> expectedPerformers = List.of(
            createMockDriverPerformance(100001L, new BigDecimal("94.7"), "EXCELLENT"),
            createMockDriverPerformance(100002L, new BigDecimal("92.3"), "EXCELLENT")
        );
        when(safeDriverScoreRepository.findTopPerformers(any(Pageable.class)))
            .thenReturn(expectedPerformers);

        // When
        List<DriverPerformanceDto> result = fleetService.getTopPerformers(limit);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(100001L, result.get(0).getDriverId());
        assertEquals(new BigDecimal("94.7"), result.get(0).getSafetyScore());
        
        verify(safeDriverScoreRepository).findTopPerformers(PageRequest.of(0, limit));
    }

    @Test
    void testGetTopPerformersWithLimitExceedingMax() {
        // Given
        int limit = 2000; // Exceeds max of 1000
        when(safeDriverScoreRepository.findTopPerformers(any(Pageable.class)))
            .thenReturn(List.of());

        // When
        fleetService.getTopPerformers(limit);

        // Then
        verify(safeDriverScoreRepository).findTopPerformers(PageRequest.of(0, 1000));
    }

    @Test
    void testGetHighRiskDrivers() {
        // Given
        int limit = 5;
        List<DriverPerformanceDto> expectedDrivers = List.of(
            createMockDriverPerformance(100003L, new BigDecimal("25.1"), "HIGH_RISK")
        );
        when(safeDriverScoreRepository.findHighRiskDrivers(any(Pageable.class)))
            .thenReturn(expectedDrivers);

        // When
        List<DriverPerformanceDto> result = fleetService.getHighRiskDrivers(limit);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("HIGH_RISK", result.get(0).getRiskCategory());
        
        verify(safeDriverScoreRepository).findHighRiskDrivers(PageRequest.of(0, limit));
    }

    private DriverPerformanceDto createMockDriverPerformance(Long driverId, BigDecimal score, String riskCategory) {
        DriverPerformanceDto dto = new DriverPerformanceDto();
        dto.setDriverId(driverId);
        dto.setSafetyScore(score);
        dto.setRiskCategory(riskCategory);
        dto.setSpeedCompliance(new BigDecimal("98.5"));
        dto.setHarshEvents(0);
        dto.setPhoneUsage(new BigDecimal("2.1"));
        dto.setAccidents(0);
        dto.setTotalEvents(1250);
        return dto;
    }
}