package com.organisation.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.epermit.Exception.EntityNotFoundException;
import com.epermit.Exception.UnauthorizedException;
import com.organisation.dto.DepositWithdrawalBookDTO;
import com.organisation.model.CashBook;
import com.organisation.model.DepositWithdrawalBook;
import com.organisation.model.RegistrationMaster;
import com.organisation.model.RegistrationMstr;
import com.organisation.repository.RegistrationMstrRepository;
import com.organisation.security.TokenService;
import com.organisation.service.CashBookService;
import com.organisation.service.DepositBookService;
import com.organisation.service.FundsOperationService;
import com.organisation.service.RegistrationService;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;

@Service
public class FundOperationImpl implements FundsOperationService {

    @Autowired
    private TokenService ts;

    @Autowired
    private DepositBookService depositBookService;
    @Autowired
    private CashBookService cashBookService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private RegistrationMstrRepository registrationRepository;

    @Override
    @Transactional
    public DepositWithdrawalBook depositFund(DepositWithdrawalBookDTO request, String token) {
        // ---------------- TOKEN ----------------
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = ts.validateToken(token);
        if (claims == null) {
            throw new UnauthorizedException("Invalid Token");
        }

        String userId = ts.extractUserId(token);
        String orgId = userId.startsWith("U") ? userId.substring(1) : userId;

        request.normalizePurpose();

        if (request.isCash()) {
            return processCashDeposit(request, userId);
        } else {
            return processNonCashDeposit(request, userId);
        }
    }

    private DepositWithdrawalBook processCashDeposit(DepositWithdrawalBookDTO request, String userId) {
        CashBook cashBook = cashBookService.createCashBookEntry(
                request, userId, request.getPurpose());

        // Create deposit book entry
        DepositWithdrawalBook deposit = depositBookService.createDepositEntry(
                request, userId);

        deposit.setTrnsType("C");
        deposit.setStatus("A");

        saveRegistrationAfterApproval(deposit, cashBook.getBookNo(), userId);

        return deposit;
    }

    @Transactional
    private void saveRegistrationAfterApproval(DepositWithdrawalBook deposit, String receiptNo, String userId) {

        if (!"REGISTRATION".equals(deposit.getPurpose())) {
            return;
        }

        RegistrationMstr regMstr = new RegistrationMstr();
        regMstr.setOrgId(deposit.getPartyCode());
        regMstr.setRegFeeAmount(deposit.getAmount());
        regMstr.setCreatedBy(userId);
        regMstr.setOrgBaseMarket(deposit.getMarketCode());
        regMstr.setCreatedOn(LocalDateTime.now());

        if ("CASH".equalsIgnoreCase(deposit.getInstrumentType())) {
            regMstr.setRegBookType("CASH_BOOK");
            regMstr.setRegReceiptNo(receiptNo);
            regMstr.setIsRegistraionFeePaid(true);

        } else if ("DEPOSIT".equalsIgnoreCase(deposit.getInstrumentType())) {
            regMstr.setRegBookType("DEPOSIT_WITHDRAWAL_BOOK");
            regMstr.setRegReceiptNo(deposit.getTrnsNo());
            regMstr.setIsRegistraionFeePaid(true);

        } else {
            return;
        }

        registrationRepository.save(regMstr);

    }

    private DepositWithdrawalBook processNonCashDeposit(DepositWithdrawalBookDTO request, String userId) {
        // Create deposit book entry
        DepositWithdrawalBook deposit = depositBookService.createDepositEntry(request, userId);

        return deposit;

    }

    @Override
    @Transactional
    public DepositWithdrawalBook approveRejectDeposit(String trnsNo, String decision, String remarks, String token) {

        // -------- TOKEN VALIDATION --------
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = ts.validateToken(token);
        if (claims == null) {
            throw new UnauthorizedException("Invalid Token");
        }

        String userId = ts.extractUserId(token);

        // -------- FETCH DEPOSIT --------
        Optional<DepositWithdrawalBook> depositOpt = depositBookService.findByTrnsNo(trnsNo);
        if (depositOpt.isEmpty()) {
            throw new EntityNotFoundException("Deposit transaction not found for trnsNo: " + trnsNo);
        }
        DepositWithdrawalBook deposit = depositOpt.get();
        // -------- REJECT FLOW --------
        if ("R".equals(decision)) {
            deposit.setStatus("R");
            deposit.setRemarks(remarks);
            deposit.setCreatedBy(userId);
            return deposit;
        }

        // -------- APPROVE FLOW --------
        deposit.setStatus("A");
        deposit.setModifiedBy(userId);
        deposit.setApproverRemarks(remarks);
        deposit.setApprovalDate(java.time.LocalDateTime.now());

        // -------- REGISTRATION HANDLING --------
        if ("REGISTRATION".equals(deposit.getPurpose())) {
            saveRegistrationAfterApproval(
                    deposit,
                    deposit.getTrnsNo(),
                    userId);
        }

        return deposit;
    }

    @Override
    public List<DepositWithdrawalBookDTO> getFundDetails(DepositWithdrawalBookDTO request, String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = ts.validateToken(token);
        if (claims == null) {
            throw new UnauthorizedException("Invalid Token");
        }

        String userId = ts.extractUserId(token);
        String orgId = userId.startsWith("U") ? userId.substring(1) : userId;

        // Call the repository method with all parameters
        List<DepositWithdrawalBookDTO> depositList = depositBookService.getDepositDetails(request, token);
        return depositList;

    }

}
