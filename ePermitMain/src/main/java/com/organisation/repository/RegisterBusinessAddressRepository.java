package com.organisation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.register.model.RegisterBusinessAddress;
import com.register.model.RegisterBusinessAddressId;

@Repository
public interface RegisterBusinessAddressRepository extends JpaRepository<RegisterBusinessAddress, RegisterBusinessAddressId>{
	List<RegisterBusinessAddress> findByIdOrgId(String orgId);

}
