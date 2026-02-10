package com.organisation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.organisation.model.UserDigiSignEntity;

@Repository
public interface UserDigiSignRepository extends JpaRepository<UserDigiSignEntity, Long> {

    boolean existsByUserIdAndIsActive(String userId, String isActive);
    
    Optional<UserDigiSignEntity> 
    findByUserIdAndIsActive(String userId, String isActive);
}
