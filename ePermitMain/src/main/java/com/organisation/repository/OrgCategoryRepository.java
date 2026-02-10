package com.organisation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.organisation.model.OrgCategoryMaster;

@Repository
public interface OrgCategoryRepository extends JpaRepository<OrgCategoryMaster, String>{
	List<OrgCategoryMaster> findByOrgCategoryNotIn(List<String> excludedCategories);
}
