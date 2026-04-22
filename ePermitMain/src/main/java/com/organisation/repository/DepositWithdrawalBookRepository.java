package com.organisation.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.organisation.model.DepositWithdrawalBook;
import com.organisation.model.TransactionStatus;

@Repository
public interface DepositWithdrawalBookRepository extends JpaRepository<DepositWithdrawalBook, String> {
	
	Optional<DepositWithdrawalBook> findByTrnsNo(String trnsNo);

    List<DepositWithdrawalBook> findByPartyCodeAndStatus(String partyCode, TransactionStatus status);

    @Modifying
    @Query("UPDATE DepositWithdrawalBook d SET d.status = :status, " +
           "d.approverRemarks = :remarks, d.approvalDate = CURRENT_TIMESTAMP, " +
           "d.modifiedBy = :modifiedBy, d.modifiedOn = CURRENT_TIMESTAMP " +
           "WHERE d.trnsNo = :trnsNo")
    int updateStatus(@Param("trnsNo") String trnsNo,
                     @Param("status") String status,
                     @Param("remarks") String remarks,
                     @Param("modifiedBy") String modifiedBy);

    @Query("SELECT COUNT(d) > 0 FROM DepositWithdrawalBook d " +
           "WHERE d.partyCode = :partyCode AND d.status = 'P'")
    boolean hasPendingDeposits(@Param("partyCode") String partyCode);

		@Query(
		value = """
			SELECT *
			FROM un_deposit_withdrawal_book d
			WHERE d.created_on >= CAST(:fromDate AS timestamp)
			AND d.created_on <= CAST(:toDate AS timestamp)
			AND UPPER(d.party_code) = UPPER(:partyCode)
			AND d.status = UPPER(:status)
			AND UPPER(d.trns_type) = UPPER(:trnsType)
			AND UPPER(d.market_code) = UPPER(:marketCode)
			""",
		nativeQuery = true)
	List<DepositWithdrawalBook> findDepositWithdrawalBookByCustomQuery(
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate,
			@Param("partyCode") String partyCode,
			@Param("status") String status,
			@Param("trnsType") String trnsType,
			@Param("marketCode") String marketCode
	);



}
