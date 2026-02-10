package com.organisation.service;

import org.springframework.stereotype.Service;

import com.organisation.dto.DepositWithdrawalBookDTO;
import com.organisation.model.CashBook;
import com.organisation.model.DepositWithdrawalBook;
import com.organisation.model.TransactionStatus;


public interface CashBookService {
	
public void updateStatus(String bookNo, TransactionStatus status, String remarks,String userId);
	
public void markAsPosted(String bookNo);

public CashBook createCashBookEntry(DepositWithdrawalBookDTO dto, String userId, String purpose);
	

}
