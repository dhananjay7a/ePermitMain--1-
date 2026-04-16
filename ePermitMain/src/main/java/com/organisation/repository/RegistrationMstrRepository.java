package com.organisation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.organisation.model.RegistrationMstr;

@Repository
public interface RegistrationMstrRepository extends JpaRepository<RegistrationMstr, String> {
	Optional<RegistrationMstr> findByOrgId(String orgId);

}
