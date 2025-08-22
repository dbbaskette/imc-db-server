package com.insurancemegacorp.dbserver.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "safe_driver_scores")
public class SafeDriverScore {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id")
    private Integer scoreId;
    
    @Column(name = "driver_id", nullable = false)
    private Integer driverId;
    
    @Column(name = "score", precision = 5, scale = 2, nullable = false)
    private BigDecimal score;
    
    @Column(name = "calculation_date", nullable = false)
    private ZonedDateTime calculationDate;
    
    @Column(name = "notes")
    private String notes;

    public SafeDriverScore() {}

    public SafeDriverScore(Integer driverId, BigDecimal score, ZonedDateTime calculationDate, String notes) {
        this.driverId = driverId;
        this.score = score;
        this.calculationDate = calculationDate;
        this.notes = notes;
    }

    public Integer getScoreId() {
        return scoreId;
    }

    public void setScoreId(Integer scoreId) {
        this.scoreId = scoreId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public ZonedDateTime getCalculationDate() {
        return calculationDate;
    }

    public void setCalculationDate(ZonedDateTime calculationDate) {
        this.calculationDate = calculationDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}