package com.insurancemegacorp.dbserver.dto;

import java.math.BigDecimal;

public class FleetSummaryDto {
    
    private long totalDrivers;
    private BigDecimal averageSafetyScore;
    private long highRiskCount;
    private long accidentsThisMonth;
    private BigDecimal improvementTrend;

    public FleetSummaryDto() {}

    public FleetSummaryDto(long totalDrivers, BigDecimal averageSafetyScore, 
                          long highRiskCount, long accidentsThisMonth, 
                          BigDecimal improvementTrend) {
        this.totalDrivers = totalDrivers;
        this.averageSafetyScore = averageSafetyScore;
        this.highRiskCount = highRiskCount;
        this.accidentsThisMonth = accidentsThisMonth;
        this.improvementTrend = improvementTrend;
    }

    public long getTotalDrivers() {
        return totalDrivers;
    }

    public void setTotalDrivers(long totalDrivers) {
        this.totalDrivers = totalDrivers;
    }

    public BigDecimal getAverageSafetyScore() {
        return averageSafetyScore;
    }

    public void setAverageSafetyScore(BigDecimal averageSafetyScore) {
        this.averageSafetyScore = averageSafetyScore;
    }

    public long getHighRiskCount() {
        return highRiskCount;
    }

    public void setHighRiskCount(long highRiskCount) {
        this.highRiskCount = highRiskCount;
    }

    public long getAccidentsThisMonth() {
        return accidentsThisMonth;
    }

    public void setAccidentsThisMonth(long accidentsThisMonth) {
        this.accidentsThisMonth = accidentsThisMonth;
    }

    public BigDecimal getImprovementTrend() {
        return improvementTrend;
    }

    public void setImprovementTrend(BigDecimal improvementTrend) {
        this.improvementTrend = improvementTrend;
    }
}