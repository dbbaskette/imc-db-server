package com.insurancemegacorp.dbserver.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "driver_accident_model")
public class DriverAccidentModel {
    
    @Id
    @Column(name = "num_iterations") // Use num_iterations as a simple ID since there's likely only one model
    private Integer id;
    
    @Column(name = "coef")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private double[] coef;
    
    @Column(name = "log_likelihood")
    private Double logLikelihood;
    
    @Column(name = "std_err")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private double[] stdErr;
    
    @Column(name = "z_stats")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private double[] zStats;
    
    @Column(name = "p_values")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private double[] pValues;
    
    @Column(name = "odds_ratios")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private double[] oddsRatios;
    
    @Column(name = "condition_no")
    private Double conditionNo;
    
    @Column(name = "num_rows_processed")
    private Long numRowsProcessed;
    
    @Column(name = "num_missing_rows_skipped")
    private Long numMissingRowsSkipped;
    
    @Column(name = "variance_covariance")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private double[] varianceCovariance;

    public DriverAccidentModel() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double[] getCoef() {
        return coef;
    }

    public void setCoef(double[] coef) {
        this.coef = coef;
    }

    public Double getLogLikelihood() {
        return logLikelihood;
    }

    public void setLogLikelihood(Double logLikelihood) {
        this.logLikelihood = logLikelihood;
    }

    public double[] getStdErr() {
        return stdErr;
    }

    public void setStdErr(double[] stdErr) {
        this.stdErr = stdErr;
    }

    public double[] getZStats() {
        return zStats;
    }

    public void setZStats(double[] zStats) {
        this.zStats = zStats;
    }

    public double[] getPValues() {
        return pValues;
    }

    public void setPValues(double[] pValues) {
        this.pValues = pValues;
    }

    public double[] getOddsRatios() {
        return oddsRatios;
    }

    public void setOddsRatios(double[] oddsRatios) {
        this.oddsRatios = oddsRatios;
    }

    public Double getConditionNo() {
        return conditionNo;
    }

    public void setConditionNo(Double conditionNo) {
        this.conditionNo = conditionNo;
    }

    public Long getNumRowsProcessed() {
        return numRowsProcessed;
    }

    public void setNumRowsProcessed(Long numRowsProcessed) {
        this.numRowsProcessed = numRowsProcessed;
    }

    public Long getNumMissingRowsSkipped() {
        return numMissingRowsSkipped;
    }

    public void setNumMissingRowsSkipped(Long numMissingRowsSkipped) {
        this.numMissingRowsSkipped = numMissingRowsSkipped;
    }

    public Integer getNumIterations() {
        return id; // num_iterations is now the ID field
    }

    public void setNumIterations(Integer numIterations) {
        this.id = numIterations; // num_iterations is now the ID field
    }

    public double[] getVarianceCovariance() {
        return varianceCovariance;
    }

    public void setVarianceCovariance(double[] varianceCovariance) {
        this.varianceCovariance = varianceCovariance;
    }
}