package com.insurancemegacorp.dbserver.controller;

import com.insurancemegacorp.dbserver.config.DatabaseInstanceManager;
import com.insurancemegacorp.dbserver.exception.DatabaseInstanceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatabaseInstanceManager databaseInstanceManager;

    @Test
    void testHealthEndpoint_ValidInstance() throws Exception {
        // Given
        String instance = "db01";
        when(databaseInstanceManager.isInstanceAvailable(instance)).thenReturn(true);
        when(databaseInstanceManager.testConnection(instance)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/{instance}/health", instance))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.instance").value(instance))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.database_connected").value(true))
                .andExpect(jsonPath("$.executionTimeMs").exists());
    }

    @Test
    void testHealthEndpoint_InvalidInstance() throws Exception {
        // Given
        String instance = "nonexistent";
        when(databaseInstanceManager.isInstanceAvailable(instance)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/{instance}/health", instance))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Database instance not found: " + instance));
    }

    @Test
    void testHealthEndpoint_DatabaseConnectionFailed() throws Exception {
        // Given
        String instance = "db01";
        when(databaseInstanceManager.isInstanceAvailable(instance)).thenReturn(true);
        when(databaseInstanceManager.testConnection(instance)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/{instance}/health", instance))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.database_connected").value(false));
    }

    @Test
    void testDatabaseInfoEndpoint_ValidInstance() throws Exception {
        // Given
        String instance = "db01";
        when(databaseInstanceManager.isInstanceAvailable(instance)).thenReturn(true);
        when(databaseInstanceManager.getAvailableInstances()).thenReturn(Set.of("db01", "db02"));
        when(databaseInstanceManager.testConnection(instance)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/{instance}/database/info", instance))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.instance").value(instance))
                .andExpect(jsonPath("$.data.available_instances").isArray())
                .andExpect(jsonPath("$.data.connection_test").value(true));
    }

    @Test
    void testDatabaseInfoEndpoint_InvalidInstance() throws Exception {
        // Given
        String instance = "invalid";
        when(databaseInstanceManager.isInstanceAvailable(instance)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/{instance}/database/info", instance))
                .andExpect(status().isNotFound());
    }
}