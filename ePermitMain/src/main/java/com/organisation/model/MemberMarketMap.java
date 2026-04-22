package com.organisation.model;

public class MemberMarketMap 
{
	private String traderOrgId;
	private String traderName;
	private String market;
	private String marketDescription; 
	private String isActive;
	private String returnMsg;
	private String createdBy;
	private boolean restrictDenotifiedMkt;
	
	public boolean isRestrictDenotifiedMkt() {
		return restrictDenotifiedMkt;
	}
	public void setRestrictDenotifiedMkt(boolean restrictDenotifiedMkt) {
		this.restrictDenotifiedMkt = restrictDenotifiedMkt;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getTraderOrgId() {
		return traderOrgId;
	}
	public void setTraderOrgId(String traderOrgId) {
		this.traderOrgId = traderOrgId;
	}
	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	public String getTraderName() {
		return traderName;
	}
	public void setTraderName(String traderName) {
		this.traderName = traderName;
	}
	public String getMarketDescription() {
		return marketDescription;
	}
	public void setMarketDescription(String marketDescription) {
		this.marketDescription = marketDescription;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public String getReturnMsg() {
		return returnMsg;
	}
	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}
	
	
}


