package com.organisation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.organisation.model.CashBook;
import com.organisation.model.TransactionStatus;

@Repository
public interface CashBookRepository extends JpaRepository<CashBook, String>  {
	Optional<CashBook> findByBookNo(String bookNo);
       

    @Modifying
    @Query
    ("UPDATE CashBook c SET c.status = :status, c.remark = :remark, " +
           "c.modifiedBy = :modifiedBy, c.modifiedOn = CURRENT_TIMESTAMP " +
           "WHERE c.bookNo = :bookNo")
    int updateStatus(@Param("bookNo") String bookNo, 
                     @Param("status") TransactionStatus status,
                     @Param("remark") String remark,
                     @Param("modifiedBy") String modifiedBy);

    @Modifying
    @Query
    ("UPDATE CashBook c SET c.isPosted = 'T', c.postingAmt = c.creditAmt " +
           "WHERE c.bookNo = :bookNo")
    int markAsPosted(@Param("bookNo") String bookNo);

}
