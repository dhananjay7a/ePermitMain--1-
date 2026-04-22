package com.organisation.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.organisation.dto.UserListResponseDto;
import com.organisation.model.UserMstr;

import jakarta.transaction.Transactional;

@Repository
public interface UserMstrRepository extends JpaRepository<UserMstr, String> {	
	UserMstr findByUserId(String userId);
	
	UserMstr findByOrgId(String orgId);
	
	@Query(value = "SELECT * FROM epermit.tbl_mst_user_details WHERE user_id = :uid", nativeQuery = true)
    Map<String, Object> findUserById(@Param("uid") String uid);
	
	@Modifying
	@Transactional
	@Query("UPDATE UserMstr u SET u.currentToken = :token WHERE u.userId = :userId")
	void updateToken(String userId, String token);
	
	@Modifying
	@Transactional
	@Query("UPDATE UserMstr u SET u.isLock = 'N' WHERE u.userId = :userId")
	void unlockUser(String userId);



//	boolean existsByUserId(String userId);

	@Modifying
	@Transactional
	@Query("UPDATE UserMstr u SET u.isLock = 'Y', u.lockTimestamp = CURRENT_TIMESTAMP WHERE u.userId = :userId AND u.isLock = 'N'")
	int logoutUser(String userId);
	
	
	@Query("""
			SELECT new com.organisation.dto.UserListResponseDto(
			u.userId,
			u.userName,
			u.orgId,
			u.userEmail,
			u.userMobile,
			r.orgAddress,
			u.userIsActive
			)
			FROM UserMstr u
			LEFT JOIN RegistrationMstr r
			ON u.orgId = r.orgId
			""")
			List<UserListResponseDto> getAllUsers();



	
}	
	
	