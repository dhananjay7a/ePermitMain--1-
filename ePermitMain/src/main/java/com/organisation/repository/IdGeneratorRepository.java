package com.organisation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.organisation.model.IdGenerator;

import jakarta.persistence.LockModeType;

@Repository
public interface IdGeneratorRepository extends JpaRepository<IdGenerator, String> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT i FROM IdGenerator i WHERE i.txnType = :txnType")
	IdGenerator findByTxnTypeForUpdate(@Param("txnType") String txnType);
}
