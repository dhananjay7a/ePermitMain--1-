package com.organisation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.organisation.model.RoleModuleMap;
import com.organisation.model.RoleModuleMapId;

import jakarta.transaction.Transactional;

public interface RoleModuleMapRepository extends JpaRepository<RoleModuleMap, RoleModuleMapId> {
	 @Transactional
	    @Modifying
	    @Query("""
	            DELETE FROM RoleModuleMap r
	            WHERE r.roleId = :roleId
	            AND r.moduleId IN :moduleIds
	           """)
	    void deleteModules(String roleId, List<Long> moduleIds);
}
