package com.organisation.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
@Data
public class DepositWithdrawalBookDTO {
    private String trnsNo;
    
    private String marketCode;


    private String partyCode;

    private String partyName;


 
    private BigDecimal amount;


    private String instrumentType; // CASH, CHEQUE, DD, ONLINE

    private String instrumentNo;

    private String hostBank;

    private String bankName;

    private String bankCode;

    private String bankIfscCode;


    private String remarks;

    private String purpose; // REGISTRATION, RENEWAL, PERMIT_GENERATION

    private String refNo;

    private String orgCategory;

    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String status;
    private String credit;
    private String debit;
    private String trnsType;

    // Helper method
    public boolean isCash() {
        return "CASH".equalsIgnoreCase(instrumentType);
    }

    public void normalizePurpose() {
        if ("REGISTRATION_CASH".equals(purpose)) {
            purpose = "REGISTRATION";
        } else if ("RENEWAL_CASH".equals(purpose)) {
            purpose = "RENEWAL";
        }
    }
    public String getStatus() {
        return this.status;
    }

}