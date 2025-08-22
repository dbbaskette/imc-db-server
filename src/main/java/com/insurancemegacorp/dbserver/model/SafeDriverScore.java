package com.insurancemegacorp.dbserver.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "safe_driver_scores")
public class SafeDriverScore {
    
    @Id
    @Column(name = "driver_id")
    private Long driverId;
    
    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;
    
    @Column(name = "risk_category", length = 20)
    private String riskCategory;
    
    @Column(name = "calculation_date")
    private LocalDateTime calculationDate;

    public SafeDriverScore() {}

    public SafeDriverScore(Long driverId, BigDecimal score, String riskCategory, LocalDateTime calculationDate) {
        this.driverId = driverId;
        this.score = score;
        this.riskCategory = riskCategory;
        this.calculationDate = calculationDate;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getRiskCategory() {
        return riskCategory;
    }

    public void setRiskCategory(String riskCategory) {
        this.riskCategory = riskCategory;
    }

    public LocalDateTime getCalculationDate() {
        return calculationDate;
    }

    public void setCalculationDate(LocalDateTime calculationDate) {
        this.calculationDate = calculationDate;
    }
}