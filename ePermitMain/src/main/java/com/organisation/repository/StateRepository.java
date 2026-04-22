package com.organisation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.organisation.model.StateMaster;

@Repository
public interface StateRepository extends JpaRepository<StateMaster, String>{

}
