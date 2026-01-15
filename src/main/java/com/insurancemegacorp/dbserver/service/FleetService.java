package com.insurancemegacorp.dbserver.service;

import com.insurancemegacorp.dbserver.dto.DriverPerformanceDto;
import com.insurancemegacorp.dbserver.dto.FleetSummaryDto;
import com.insurancemegacorp.dbserver.repository.SafeDriverScoreRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class FleetService {

    private final SafeDriverScoreRepository safeDriverScoreRepository;

    public FleetService(SafeDriverScoreRepository safeDriverScoreRepository) {
        this.safeDriverScoreRepository = safeDriverScoreRepository;
    }

    public FleetSummaryDto getFleetSummary() {
        return safeDriverScoreRepository.getFleetSummary();
    }

    public long getActiveDriversCount() {
        return safeDriverScoreRepository.countActiveDrivers();
    }

    public long getHighRiskDriversCount() {
        return safeDriverScoreRepository.countHighRiskDrivers();
    }

    public List<DriverPerformanceDto> getTopPerformers(int limit) {
        Pageable pageable = PageRequest.of(0, Math.min(limit, 1000));
        return safeDriverScoreRepository.findTopPerformers(pageable);
    }

    public List<DriverPerformanceDto> getHighRiskDrivers(int limit) {
        Pageable pageable = PageRequest.of(0, Math.min(limit, 1000));
        return safeDriverScoreRepository.findHighRiskDrivers(pageable);
    }

    public Map<String, Long> getScoreDistribution() {
        return safeDriverScoreRepository.getScoreDistribution();
    }
}