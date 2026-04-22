package com.organisation.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "un_registration_validity_tracker", schema = "epermit")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationValidityTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "org_id", length = 50, nullable = false)
    private String orgId;

    @Column(name = "reg_type", length = 20)
    private String regType; // REGISTRATION, RENEWAL

    @Column(name = "reg_valid_upto")
    private LocalDate regValidUpto;

    @Column(name = "reg_fee_book_type", length = 30)
    private String regFeeBookType; // CASH_BOOK, DEPOSIT_WITHDRAWAL_BOOK, UNIFIED

    @Column(name = "reg_fee_receipt_no", length = 50)
    private String regFeeReceiptNo; // Reference to book_no or trns_no

    @Column(name = "reg_fee_amount", precision = 15, scale = 2)
    private BigDecimal regFeeAmount;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;

    // Audit fields
    @PrePersist
    protected void onCreate() {
        createdOn = LocalDateTime.now();
        modifiedOn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedOn = LocalDateTime.now();
    }
}
