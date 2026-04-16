package com.organisation.repository;

import com.organisation.dto.UserMappingDto;
import com.organisation.model.UserMarketMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMarketMapRepository extends JpaRepository<UserMarketMapping, String> {

        @Query("SELECT new com.organisation.dto.UserMappingDto(" +
                        "um.userId, ms.marketCode, ms.description, um.isActive, " +
                        "um.createdBy, um.createdOn, um.modifiedBy, um.modifiedOn) " +
                        "FROM UserMarketMapping um " +
                        "INNER JOIN MarketMaster ms ON ms.marketCode = um.marketCode " +
                        "WHERE ms.isMockMarket = 'N' " +
                        "AND um.userId = :userId " +
                        "AND um.isActive = :isActive")
        List<UserMappingDto> fetchAllUserMarketMap(@Param("userId") String userId,
                        @Param("isActive") String isActive);

        boolean existsByUserIdAndMarketCode(String userId, String marketCode);
}