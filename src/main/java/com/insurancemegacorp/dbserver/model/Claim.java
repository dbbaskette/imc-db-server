package com.insurancemegacorp.dbserver.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
public class Claim {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "claim_id")
    private Integer claimId;
    
    @Column(name = "accident_id", nullable = false)
    private Integer accidentId;
    
    @Column(name = "claim_date", nullable = false)
    private LocalDate claimDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ClaimStatus status = ClaimStatus.PENDING;
    
    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "description", columnDefinition = "text")
    private String description;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Claim() {}

    public Claim(Integer accidentId, LocalDate claimDate) {
        this.accidentId = accidentId;
        this.claimDate = claimDate;
    }

    public Claim(Integer accidentId, LocalDate claimDate, BigDecimal amount, String description) {
        this.accidentId = accidentId;
        this.claimDate = claimDate;
        this.amount = amount;
        this.description = description;
    }

    // Getters and Setters
    public Integer getClaimId() {
        return claimId;
    }

    public void setClaimId(Integer claimId) {
        this.claimId = claimId;
    }

    public Integer getAccidentId() {
        return accidentId;
    }

    public void setAccidentId(Integer accidentId) {
        this.accidentId = accidentId;
    }

    public LocalDate getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDate claimDate) {
        this.claimDate = claimDate;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    public boolean isPending() {
        return status == ClaimStatus.PENDING;
    }

    public boolean isApproved() {
        return status == ClaimStatus.APPROVED;
    }

    public boolean isDenied() {
        return status == ClaimStatus.DENIED;
    }

    public boolean hasAmount() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public String getFormattedAmount() {
        if (hasAmount()) {
            return "$" + amount.toString();
        }
        return "Amount not specified";
    }

    // Enum for Claim Status
    public enum ClaimStatus {
        PENDING, APPROVED, DENIED
    }
}
