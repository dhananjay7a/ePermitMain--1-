package com.organisation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.register.model.RegistrationStatus;

@Repository
public interface RegistrationStatusRepository extends JpaRepository<RegistrationStatus, Long>{
	Optional<RegistrationStatus> findByOrgId(String orgId);
	
	Optional<RegistrationStatus>
    findTopByOrgIdOrderByActionDateTimeDesc(String orgId);
	
	@Query(value = """
		    SELECT *
		    FROM tbl_registration_status
		    WHERE org_id = :orgId
		    ORDER BY action_date_time DESC
		    LIMIT 1
		    """, nativeQuery = true)
		Optional<RegistrationStatus> findLatestStatus(@Param("orgId") String orgId);


}
