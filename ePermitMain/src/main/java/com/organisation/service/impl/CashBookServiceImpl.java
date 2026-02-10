package com.organisation.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.organisation.dto.DepositWithdrawalBookDTO;
import com.organisation.mapper.DepositMapper;
import com.organisation.model.CashBook;
import com.organisation.model.TransactionStatus;
import com.organisation.repository.CashBookRepository;
import com.organisation.service.CashBookService;

import jakarta.transaction.Transactional;

@Service
public class CashBookServiceImpl implements CashBookService {
	@Autowired
    private  CashBookRepository cashBookRepository;
	
	@Autowired
    private DataSource dataSource; 
	
	
	 @Transactional
	 @Override
	 public CashBook createCashBookEntry(DepositWithdrawalBookDTO dto, 
	                                        String userId, 
	                                        String purpose) {
	        
	        String bookNo = this.generateBookNo();
	        
			CashBook cashBook = CashBook.builder()
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
	        
	        CashBook saved = cashBookRepository.save(cashBook);
	        
	        return saved;
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
	 
	private String generateBookNo() {	 
		String prefix = "BK";
		String sql = "SELECT MAX(book_no) FROM un_cash_book WHERE book_no LIKE 'BK%'";
	
		    try (Connection conn = dataSource.getConnection();
		         PreparedStatement pstmt = conn.prepareStatement(sql);
		         ResultSet rs = pstmt.executeQuery()) {
	
		        int nextNumber = 10001; // default starting number
	
		        if (rs.next() && rs.getString(1) != null) {
		            String lastBookNo = rs.getString(1); // e.g. BK10005
		            int lastNumber = Integer.parseInt(lastBookNo.substring(2));
		            nextNumber = lastNumber + 1;
		        }
	
		        return prefix + nextNumber;
	
		    } catch (SQLException e) {
		        throw new RuntimeException("Failed to generate book number", e);
		    }
	}

	 @Override
	 @Transactional
	 public void updateStatus(String bookNo, TransactionStatus status, String remarks, String userId) {
		 int updated = cashBookRepository.updateStatus(bookNo, status, remarks, userId);
	        
	        if (updated == 0) {
	            throw new RuntimeException("Failed to update cash book status for: " + bookNo);
	        }
		
	 }

	 @Override
	 @Transactional
	 public void markAsPosted(String bookNo) {
		 cashBookRepository.markAsPosted(bookNo);
	 }

	
    
    


}
