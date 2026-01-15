package com.insurancemegacorp.dbserver.model;

// POJO for MADlib model data - not a JPA entity
// MADlib output tables don't have traditional primary keys, so we use JdbcTemplate directly
public class DriverAccidentModel {

    private double[] coef;
    private Double logLikelihood;
    private double[] stdErr;
    private double[] zStats;
    private double[] pValues;
    private double[] oddsRatios;
    private Double conditionNo;
    private Long numRowsProcessed;
    private Long numMissingRowsSkipped;
    private double[] varianceCovariance;
    private Integer numIterations;

    public DriverAccidentModel() {}

    public Integer getNumIterations() {
        return numIterations;
    }

    public void setNumIterations(Integer numIterations) {
        this.numIterations = numIterations;
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



    public double[] getVarianceCovariance() {
        return varianceCovariance;
    }

    public void setVarianceCovariance(double[] varianceCovariance) {
        this.varianceCovariance = varianceCovariance;
    }
}