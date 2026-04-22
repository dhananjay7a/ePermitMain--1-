package com.organisation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.organisation.model.RegistrationMstr;

@Repository
public interface RegistrationMstrRepository extends JpaRepository<RegistrationMstr, String>{
	Optional<RegistrationMstr> findByOrgId(String orgId);
	
	@Query(value = """
	        SELECT 
	            r.org_id as orgId,
	            r.org_name as orgName,
	            r.org_category as orgCategory,
	            s.application_status as applicationStatus,
	            s.remarks as remarks
	          
	        FROM tbl_mst_registration r
	        JOIN tbl_registration_status s 
	            ON r.org_id = s.org_id
	        WHERE s.application_status = 'SV'
	        ORDER BY s.action_date_time DESC
	        """, nativeQuery = true)
	    List<Object[]> getScrutinyList();
	    
	    
	    @Query(value = """
		        SELECT 
		            r.org_id as orgId,
		            r.org_name as orgName,
		            r.org_category as orgCategory,
		            s.application_status as applicationStatus,
		            s.remarks as remarks
		          
		        FROM tbl_mst_registration r
		        JOIN tbl_registration_status s 
		            ON r.org_id = s.org_id
		        WHERE s.application_status = 'FA'
		        ORDER BY s.action_date_time DESC
		        """, nativeQuery = true)
		    List<Object[]> getFinalApproverList();
		    
		    boolean existsByOrgId(String orgId);
		  
}
