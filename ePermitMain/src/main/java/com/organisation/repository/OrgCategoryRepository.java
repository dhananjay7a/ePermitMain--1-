package com.organisation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.organisation.model.OrgCategoryMaster;
import com.organisation.model.OrgOfficeDetails;

@Repository
public interface OrgCategoryRepository extends JpaRepository<OrgCategoryMaster, String>{
	List<OrgCategoryMaster> findByOrgCategoryNotIn(List<String> excludedCategories);
	Optional<OrgCategoryMaster> findByOrgCategory(String orgCategory);
}
