package com.organisation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.organisation.dto.UserRoleResponseDto;
import com.organisation.model.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, String> {
	
	@Query("""
			SELECT DISTINCT new com.organisation.dto.UserRoleResponseDto(
			    rm.userId,
			    rm.roleId,
			    ro.roleName,
			    rm.isActive
			   
			)
			FROM UserRole ro
			JOIN UserRoleMapping rm ON ro.roleId = rm.roleId
			JOIN UserMstr um ON um.userId = rm.userId
			JOIN RegistrationMstr rg ON um.orgId = rg.orgId
			WHERE rm.userId = :userId
			AND (
			    rg.orgBaseMarket IN (
			        SELECT m.marketCode FROM MarketMstr m WHERE m.isMockMarket='N'
			    )
			    OR rg.orgBaseMarket = 'ALL'
			    OR rg.orgBaseMarket IS NULL
			)
			""")
			List<UserRoleResponseDto> getUserRoleMapping(String userId);
    
    @Query("SELECT DISTINCT r FROM UserRole r ORDER BY r.roleName")
    List<UserRole> fetchDistinctRole();


}
