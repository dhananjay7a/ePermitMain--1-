package com.organisation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.organisation.model.StateDistrictId;
import com.organisation.model.StateDistrictMaster;

@Repository
public interface StateDistrictRepository extends JpaRepository<StateDistrictMaster, StateDistrictId>{
	List<StateDistrictMaster> findByIdStateCode(String stateCode);
}

 