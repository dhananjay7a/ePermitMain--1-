package com.organisation.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.organisation.dto.DepositWithdrawalBookDTO;
import com.organisation.model.DepositWithdrawalBook;



public interface FundsOperationService {

    public DepositWithdrawalBook depositFund(DepositWithdrawalBookDTO request , String token );
    public List<DepositWithdrawalBookDTO> getFundDetails(DepositWithdrawalBookDTO request,String token);
    DepositWithdrawalBook approveRejectDeposit(String trnsNo, String decision, String remarks, String token);


}
