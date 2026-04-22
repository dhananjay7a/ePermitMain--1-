package com.organisation.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "un_cash_book", schema = "epermit")
public class CashBook {

    @Id
    @Column(name = "book_no")
    private String bookNo;

    @Column(name = "market_code")
    private String marketCode;

    @Column(name = "party_code")
    private String partyCode;

    @Column(name = "party_name")
    private String partyName;

    @Column(name = "counter_party_code")
    private String counterPartyCode;

    @Column(name = "counter_party_name")
    private String counterPartyName;

    @Column(name = "txn_ref_no")
    private String txnRefNo;

    @Column(name = "status")
    private String status;

    @Column(name = "debit_amt")
    private BigDecimal debitAmt;

    @Column(name = "credit_amt")
    private BigDecimal creditAmt;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "remark")
    private String remark;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;

    @Column(name = "posting_amt")
    private BigDecimal postingAmt;

    @Column(name = "is_posted")
    private String isPosted;

    @Column(name = "book_ref_no")
    private String bookRefNo;

    @Column(name = "mode_type")
    private String modeType;

    @Column(name = "cash_book_date")
    private LocalDateTime cashBookDate;

    @Column(name = "org_book_ref_no")
    private String orgBookRefNo;

    @Column(name = "permit_ref_no")
    private String permitRefNo;

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

