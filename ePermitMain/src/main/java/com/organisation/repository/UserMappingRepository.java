// package com.organisation.repository;

// import com.organisation.model.UserMapping;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// import java.util.Optional;
// import java.util.List;

// /**
// * Repository for UserMapping entity
// * Manages user to market/organization mappings
// */
// public interface UserMappingRepository extends JpaRepository<UserMapping,
// String> {

// /**
// * Find user mapping by userId
// */
// UserMapping findByUserId(String userId);

// /**
// * Find all mappings for a specific organization
// */
// List<UserMapping> findByOrgId(String orgId);

// /**
// * Find all mappings for a specific market
// */
// List<UserMapping> findByMarketCode(String marketCode);

// /**
// * Find mapping by userId and OrgId
// */
// UserMapping findByUserIdAndOrgId(String userId, String orgId);

// }
