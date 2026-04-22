package com.organisation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.organisation.model.LicneseMstr;

@Repository
public interface LicenseMstrRepository extends JpaRepository<LicneseMstr, String> {

}
