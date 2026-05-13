package com.organisation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.register.model.RenewalHistory;

@Repository
public interface RenewalHistoryRepository extends JpaRepository<RenewalHistory, Long> {
}
