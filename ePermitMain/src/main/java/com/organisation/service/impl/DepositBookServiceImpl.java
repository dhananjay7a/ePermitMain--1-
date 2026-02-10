package com.organisation.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.organisation.dto.DepositWithdrawalBookDTO;
import com.organisation.mapper.DepositMapper;
import com.organisation.model.DepositWithdrawalBook;

import com.organisation.repository.DepositWithdrawalBookRepository;
import com.organisation.service.DepositBookService;

import jakarta.transaction.Transactional;

@Service
public class DepositBookServiceImpl implements DepositBookService {

    private static final DateTimeFormatter FORMATTER =  DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Autowired
    private DepositWithdrawalBookRepository depositBookRepository;
    @Autowired
    private DepositMapper depositMapper;

    @Override
    @Transactional
    public DepositWithdrawalBook createDepositEntry(DepositWithdrawalBookDTO dto, String userId) {
        String trnsNo = this.generateUniqueTransactionId();
        
        DepositWithdrawalBook deposit = depositMapper.toEntity(dto, trnsNo, userId);
        
        DepositWithdrawalBook saved = depositBookRepository.save(deposit);
        
        return saved;
    }
    
    
    private String generateUniqueTransactionId() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "TXN" + timestamp + randomPart; 
    }
    
    
    @Override
    public List<DepositWithdrawalBookDTO> getDepositDetails(DepositWithdrawalBookDTO request, String token) {

        String  status = null;
        if (request.getStatus() != null && request.getStatus().equalsIgnoreCase("Pending")) {
            status = "P";
        }else if (request.getStatus() != null && request.getStatus().equalsIgnoreCase("Approved")) {
            status = "A";
        }else if (request.getStatus() != null && request.getStatus().equalsIgnoreCase("Rejected")) {
            status = "R";   
        }

        // 2. Map CREDIT / DEBIT → DB transaction types
        String trnsType = request.getTrnsType();
        if ("CASH".equalsIgnoreCase(trnsType)) {
            trnsType = "C";
        } else if ("DEPOSIT".equalsIgnoreCase(trnsType)) {
            trnsType = "D";
        }

        // 3. Call repository
        List<DepositWithdrawalBook> depositList =
                depositBookRepository.findDepositWithdrawalBookByCustomQuery(
                        request.getFromDate(),
                        request.getToDate(),
                        request.getPartyCode(),
                        status,
                        trnsType,
                        request.getMarketCode()
                );
        List<DepositWithdrawalBookDTO> responseList = new ArrayList<>();
        depositList.forEach(deposit -> {
            DepositWithdrawalBookDTO dto = DepositWithdrawalBookDTO.builder()
                    .trnsNo(deposit.getTrnsNo())
                    .marketCode(deposit.getMarketCode())
                    .partyCode(deposit.getPartyCode())
                    .partyName(deposit.getPartyName())
                    .amount(deposit.getAmount())
                    .instrumentType(deposit.getInstrumentType())
                    .instrumentNo(deposit.getInstrumentNo())
                    .hostBank(deposit.getHostBank())
                    .bankName(deposit.getBankName())
                    .bankCode(deposit.getBankCode())
                    .bankIfscCode(deposit.getBankIfscCode())
                    .remarks(deposit.getRemarks())
                    .refNo(deposit.getRefNo())
                    .status(deposit.getStatus())
                    .trnsType(deposit.getTrnsType())
                    .build();
            responseList.add(dto);
        });
        return responseList;
                
    }


    @Override
    public Optional<DepositWithdrawalBook> findByTrnsNo(String trnsNo) {
        return depositBookRepository.findById(trnsNo);
    }
    
}
