package com.organisation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.organisation.model.MarketMstr;

@Repository
public interface MarketMstrRepository extends JpaRepository<MarketMstr, String>{
	List<MarketMstr> findAllByIsMockMarket(String isMockMarket);
	
	List<MarketMstr> findAllByIsMockMarketAndIsDenotifiedMarket(String isMockMarket, String isDenotifiedMarket);
	
	Optional<MarketMstr> findByMarketCode(String marketCode);
}
