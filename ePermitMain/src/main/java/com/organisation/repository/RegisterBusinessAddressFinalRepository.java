package com.organisation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.organisation.model.RegisterBusinessAddressDetails;
import com.register.model.RegisterBusinessAddressId;

@Repository
public interface RegisterBusinessAddressFinalRepository 
									extends JpaRepository<RegisterBusinessAddressDetails, RegisterBusinessAddressId>{

}
