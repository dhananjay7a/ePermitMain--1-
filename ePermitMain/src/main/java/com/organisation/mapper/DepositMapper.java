package com.organisation.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.organisation.dto.DepositWithdrawalBookDTO;
import com.organisation.model.CashBook;
import com.organisation.model.DepositWithdrawalBook;
import com.organisation.model.RegistrationValidityTracker;
import com.organisation.model.TransactionStatus;

@Component
public class DepositMapper {

    public DepositWithdrawalBook toEntity(DepositWithdrawalBookDTO dto, String trnsNo, String userId) {
        return DepositWithdrawalBook.builder()
                .trnsNo(trnsNo)
                .trnsType(dto.isCash() ? "C" : "D")
                .marketCode(dto.getMarketCode())
                .partyCode(dto.getPartyCode())
                .partyName(dto.getPartyName())
                .amount(dto.getAmount())
                .instrumentType(dto.getInstrumentType())
                .instrumentNo(dto.getInstrumentNo())
                .hostBank(dto.getHostBank())
                .bankName(dto.getBankName())
                .bankCode(dto.getBankCode())
                .bankIfscCode(dto.getBankIfscCode())
                .remarks(dto.getRemarks())
                .refNo(dto.getRefNo())
                .status(TransactionStatus.PENDING.getCode())
                .createdBy(userId)
                .requestDate(LocalDate.now())
                .processingDate(LocalDate.now())
                .purpose(dto.getPurpose())
                .build();
    }

    public CashBook toCashBookEntity(DepositWithdrawalBookDTO dto, String bookNo, String userId, String purpose) {
        return CashBook.builder()
                .bookNo(bookNo)
                .marketCode(dto.getMarketCode())
                .partyCode(dto.getPartyCode())
                .partyName(dto.getPartyName())
                .counterPartyCode("CASH_" + userId)
                .counterPartyName("CASH_" + userId)
                .txnRefNo(dto.getPartyCode())
                .creditAmt(dto.getAmount())
                .amount(dto.getAmount())
                .status(TransactionStatus.PENDING.getCode())
                .remark(getPurposeRemark(purpose))
                .createdBy(userId)
                .modifiedBy(userId)
                .isPosted("F") // Not posted yet
                .build();
    }

    public RegistrationValidityTracker toTrackerEntity(
            DepositWithdrawalBookDTO dto, 
            String receiptNo, 
            String bookType, 
            String userId) {
        
        return RegistrationValidityTracker.builder()
                .orgId(dto.getPartyCode())
                .regType(dto.getPurpose())
                .regFeeAmount(dto.getAmount())
                .regFeeReceiptNo(receiptNo)
                .regFeeBookType(bookType)
                .createdBy(userId)
                .modifiedBy(userId)
                .build();
    }

    private String getPurposeRemark(String purpose) {
        switch (purpose) {
            case "REGISTRATION":
                return "Registration Fee Payment";
            case "RENEWAL":
                return "Renewal Fee Payment";
            case "PERMIT_GENERATION":
                return "Permit Generation Fee";
            default:
                return purpose;
        }
    }
}
