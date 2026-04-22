package com.organisation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.organisation.model.BankGstMstr;

@Repository
public interface BankGstMstrRepository extends JpaRepository<BankGstMstr, String> {

}
