package com.insurancemegacorp.dbserver.integration;

import com.insurancemegacorp.dbserver.controller.FleetController;
import com.insurancemegacorp.dbserver.config.DatabaseInstanceManager;
import com.insurancemegacorp.dbserver.dto.FleetSummaryDto;
import com.insurancemegacorp.dbserver.dto.DriverPerformanceDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.insurancemegacorp.dbserver.service.FleetService;
import java.math.BigDecimal;
import java.util.Arrays;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(FleetController.class)
@AutoConfigureMockMvc
class FleetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FleetService fleetService;

    @MockBean
    private DatabaseInstanceManager databaseInstanceManager;

    @Test
    void testGetFleetSummary() throws Exception {
        // Mock the database instance manager to return true for db01
        when(databaseInstanceManager.isInstanceAvailable("db01")).thenReturn(true);
        
        // Mock the fleet service to return test data
        FleetSummaryDto mockSummary = new FleetSummaryDto();
        mockSummary.setTotalDrivers(3L);
        mockSummary.setAverageSafetyScore(new BigDecimal("85.0"));
        when(fleetService.getFleetSummary()).thenReturn(mockSummary);
        
        mockMvc.perform(get("/api/db01/fleet/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalDrivers").exists())
                .andExpect(jsonPath("$.data.averageSafetyScore").exists())
                .andExpect(jsonPath("$.executionTimeMs").exists());
    }

    @Test
    void testGetActiveDriversCount() throws Exception {
        when(databaseInstanceManager.isInstanceAvailable("db01")).thenReturn(true);
        
        // Mock the fleet service to return test data
        when(fleetService.getActiveDriversCount()).thenReturn(3L);
        
        mockMvc.perform(get("/api/db01/drivers/active-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.active_drivers").value(3));
    }

    @Test
    void testGetHighRiskDriversCount() throws Exception {
        when(databaseInstanceManager.isInstanceAvailable("db01")).thenReturn(true);
        
        // Mock the fleet service to return test data
        when(fleetService.getHighRiskDriversCount()).thenReturn(1L);
        
        mockMvc.perform(get("/api/db01/drivers/high-risk-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.high_risk_drivers").value(1));
    }

    @Test
    void testGetTopPerformers() throws Exception {
        when(databaseInstanceManager.isInstanceAvailable("db01")).thenReturn(true);
        
        // Mock the fleet service to return test data
        DriverPerformanceDto mockDriver = new DriverPerformanceDto();
        mockDriver.setDriverId(1L);
        mockDriver.setSafetyScore(new BigDecimal("95.0"));
        mockDriver.setRiskCategory("LOW");
        when(fleetService.getTopPerformers(5)).thenReturn(Arrays.asList(mockDriver));
        
        mockMvc.perform(get("/api/db01/drivers/top-performers?limit=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].driverId").exists());
    }

            @Test
    void testGetTopPerformersWithInvalidInstance() throws Exception {
        when(databaseInstanceManager.isInstanceAvailable("nonexistent")).thenReturn(false);
        
        mockMvc.perform(get("/api/nonexistent/drivers/top-performers"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Database instance not found: nonexistent"));
    }
}