package com.organisation.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "un_deposit_withdrawal_book", schema = "epermit")

public class DepositWithdrawalBook {

    @Id
    @Column(name = "trns_no")
    private String trnsNo;

    @Column(name = "trns_type")
    private String trnsType; // DEBIT, WITHDRAWAL, CREDIT

    @Column(name = "market_code")
    private String marketCode;

    @Column(name = "request_date")
    private LocalDate requestDate;

    @Column(name = "processing_date")
    private LocalDate processingDate;

    @Column(name = "party_code")
    private String partyCode;

    @Column(name = "party_name")
    private String partyName;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "status")
    private String status;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "instrument_no")
    private String instrumentNo;

    @Column(name = "instrument_type")
    private String instrumentType; // CASH, CHEQUE, DD, ONLINE

    @Column(name = "host_bank_ref")
    private String hostBankRef;

    @Column(name = "host_bank")
    private String hostBank;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "manual_receipt_no")
    private String manualReceiptNo;

    @Column(name = "manual_receipt_date")
    private LocalDate manualReceiptDate;

    @Column(name = "approver_remarks")
    private String approverRemarks;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "ref_no")
    private String refNo;

    @Column(name = "bank_ifsc_code")
    private String bankIfscCode;

    @Column(name = "purpose")
    private String purpose;

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

	@Override
	public String toString() {
		return "DepositWithdrawalBook [trnsNo=" + trnsNo + ", trnsType=" + trnsType + ", marketCode=" + marketCode
				+ ", requestDate=" + requestDate + ", processingDate=" + processingDate + ", partyCode=" + partyCode
				+ ", partyName=" + partyName + ", amount=" + amount + ", status=" + status + ", bankName=" + bankName
				+ ", instrumentNo=" + instrumentNo + ", instrumentType=" + instrumentType + ", hostBankRef="
				+ hostBankRef + ", hostBank=" + hostBank + ", createdOn=" + createdOn + ", createdBy=" + createdBy
				+ ", modifiedOn=" + modifiedOn + ", modifiedBy=" + modifiedBy + ", remarks=" + remarks
				+ ", manualReceiptNo=" + manualReceiptNo + ", manualReceiptDate=" + manualReceiptDate
				+ ", approverRemarks=" + approverRemarks + ", approvalDate=" + approvalDate + ", bankCode=" + bankCode
				+ ", refNo=" + refNo + ", bankIfscCode=" + bankIfscCode + "]";
	}
}