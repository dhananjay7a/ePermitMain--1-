package com.organisation.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.organisation.model.ModulesMstr;



public interface ModuleMstrRepository extends JpaRepository<ModulesMstr, Long> {

    @Query("""
        SELECT m
        FROM ModulesMstr m
        JOIN RoleModuleMap r ON m.moduleId = r.moduleId
        WHERE m.parentModuleId <> 0
        AND m.moduleOwner IN ('MIS','PERMIT')
        AND r.roleId = :roleId
    """)
    List<ModulesMstr> fetchMappedList(String roleId);
    
    
    @Query("""
            SELECT m
            FROM ModulesMstr m
            LEFT JOIN RoleModuleMap r 
            ON m.moduleId = r.moduleId AND r.roleId = :roleId
            WHERE m.parentModuleId <> 0
            AND m.moduleOwner IN ('MIS','PERMIT')
            AND r.moduleId IS NULL
        """)
    List<ModulesMstr> fetchNotMappedList(String roleId);
}
