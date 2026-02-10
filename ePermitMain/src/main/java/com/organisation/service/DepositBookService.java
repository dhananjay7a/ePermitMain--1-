package com.organisation.service;



import java.util.List;
import java.util.Optional;

import com.organisation.dto.DepositWithdrawalBookDTO;
import com.organisation.model.DepositWithdrawalBook;

public interface DepositBookService {
    public DepositWithdrawalBook createDepositEntry(DepositWithdrawalBookDTO dto, String userId);
    public List<DepositWithdrawalBookDTO> getDepositDetails(DepositWithdrawalBookDTO request,String token);
    public Optional<DepositWithdrawalBook> findByTrnsNo(String trnsNo);
}
