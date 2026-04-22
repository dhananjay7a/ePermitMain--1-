package com.organisation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.register.model.RegisterBasicInfoAddress;

@Repository
public interface RegisterBasicInfoRepository extends JpaRepository<RegisterBasicInfoAddress, String> {
    Optional<RegisterBasicInfoAddress> findByOrgId(String orgId);

}
