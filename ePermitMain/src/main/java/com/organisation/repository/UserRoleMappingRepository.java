package com.organisation.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.organisation.model.UserRoleMapping;
import com.organisation.model.UserRoleMappingId;

@Repository
public interface UserRoleMappingRepository extends JpaRepository<UserRoleMapping, UserRoleMappingId> {

    boolean existsByUserIdAndRoleId(String userId, String roleId);

}
