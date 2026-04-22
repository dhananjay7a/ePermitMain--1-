package com.organisation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.organisation.model.AccountMstr;

public interface AccountMstrRepository extends JpaRepository<AccountMstr, Long> {

}
