package com.organisation.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.organisation.util.OrgUtil;

public class Pagination {

	private BigDecimal noOfRowsToShowInGrid;
	private String action;	
	private BigDecimal startRnum;
	private BigDecimal endRnum;
	private String totalNoOfRows;
	private String totalNoOfPages;
	private String goToPageNo;
	private String userId;
	private String orgId;
	private BigDecimal currentPageNo;
	private int startRow;
	private int endRow;
	private String orderBy;
	private String eventType;
	
	private int totalRows;
	private int rowNumber;

	
	
	
	
	public BigDecimal getNoOfRowsToShowInGrid() {
		return noOfRowsToShowInGrid;
	}
	public void setNoOfRowsToShowInGrid(BigDecimal noOfRowsToShowInGrid) {
		this.noOfRowsToShowInGrid = noOfRowsToShowInGrid;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public BigDecimal getStartRnum() {
		return startRnum;
	}
	public void setStartRnum(BigDecimal startRnum) {
		this.startRnum = startRnum;
	}
	public BigDecimal getEndRnum() {
		return endRnum;
	}
	public void setEndRnum(BigDecimal endRnum) {
		this.endRnum = endRnum;
	}
	
	public String getTotalNoOfRows() {
		return totalNoOfRows;
	}
	public void setTotalNoOfRows(String totalNoOfRows) {
		this.totalNoOfRows = totalNoOfRows;
	}
	public String getTotalNoOfPages() {
		return totalNoOfPages;
	}
	public void setTotalNoOfPages(String totalNoOfPages) {
		this.totalNoOfPages = totalNoOfPages;
	}
	public String getGoToPageNo() {
		return goToPageNo;
	}
	public void setGoToPageNo(String goToPageNo) {
		this.goToPageNo = goToPageNo;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public BigDecimal getCurrentPageNo() {
		return currentPageNo;
	}
	public void setCurrentPageNo(BigDecimal currentPageNo) {
		this.currentPageNo = currentPageNo;
	}
	
	
	
	
	public int getStartRow() {
		return startRow;
	}
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
	public int getEndRow() {
		return endRow;
	}
	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
	
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public Pagination getPaginationData(Pagination pagination, int totalRecords,String actionName)
	{
		pagination.setTotalNoOfRows(String.valueOf(totalRecords));
		  pagination.setTotalNoOfPages((new BigDecimal(pagination.getTotalNoOfRows()).divide(pagination.getNoOfRowsToShowInGrid(),0,RoundingMode.CEILING)).toString());
		  
		  //etenderObj = webLotService.fetchLots(lotKeyObj).subList(0,48);
		  /*LotSyncMain.marketWiseMap_0_9.get*/
		  if(OrgUtil.isNeitherNullNorEmpty(pagination.getNoOfRowsToShowInGrid()) && OrgUtil.isNeitherNullNorEmpty(pagination.getGoToPageNo()) && (!OrgUtil.isNeitherNullNorEmpty(pagination.getAction()) ||  (!OrgUtil.isNeitherNullNorEmpty(actionName)) || (!OrgUtil.isNeitherNullNorEmpty(pagination.getAction())))){
			  
			  if(new BigDecimal(pagination.getGoToPageNo()).compareTo(new BigDecimal(pagination.getTotalNoOfPages()))<0)
			  {
			  if((BigDecimal.ONE).equals(pagination.getGoToPageNo()))			  	  
				  pagination.setStartRnum(pagination.getNoOfRowsToShowInGrid().multiply(BigDecimal.ZERO));			  
			  else if(OrgUtil.isNeitherNullNorEmpty(pagination.getGoToPageNo())){
				  pagination.setStartRnum((pagination.getNoOfRowsToShowInGrid()).multiply(new BigDecimal(pagination.getGoToPageNo())).subtract(pagination.getNoOfRowsToShowInGrid()));				 
			  }
			
			/*  if((pagination.getEndRnum().add(pagination.getNoOfRowsToShowInGrid())).compareTo(new BigDecimal(totalRecords))>=0)
				  pagination.setEndRnum(new BigDecimal(totalRecords));
			  else*/
				  pagination.setEndRnum((pagination.getStartRnum().add(pagination.getNoOfRowsToShowInGrid())));
			  }
			  else
			  {
				  if(new BigDecimal(pagination.getGoToPageNo()).compareTo(new BigDecimal(pagination.getTotalNoOfPages()))>=0)
				  {
					  pagination.setGoToPageNo(pagination.getTotalNoOfPages());
					  pagination.setStartRnum((pagination.getNoOfRowsToShowInGrid()).multiply(new BigDecimal(pagination.getGoToPageNo()).subtract(BigDecimal.ONE)));
					  
					  if((pagination.getStartRnum().add(pagination.getNoOfRowsToShowInGrid())).compareTo(new BigDecimal(totalRecords))>0)
						  pagination.setEndRnum(new BigDecimal(totalRecords));
					  else
						  pagination.setEndRnum((pagination.getStartRnum().add(pagination.getNoOfRowsToShowInGrid()))); 
					  
				  }
			  } 
			 
		  }
		  else if(OrgUtil.isNeitherNullNorEmpty(pagination.getAction()))
		  {
			  if("NEXT".equals(pagination.getAction()))
			  {
				  pagination.setStartRnum(pagination.getStartRnum().add(pagination.getNoOfRowsToShowInGrid()));
				  if((pagination.getEndRnum().add(pagination.getNoOfRowsToShowInGrid())).compareTo(new BigDecimal(totalRecords))>=0)
					  pagination.setEndRnum(new BigDecimal(totalRecords));
				  else  
					  pagination.setEndRnum(pagination.getEndRnum().add(pagination.getNoOfRowsToShowInGrid()));
			  }
			  else if("PREV".equals(pagination.getAction()))
			  {
				  if((pagination.getStartRnum().subtract(pagination.getNoOfRowsToShowInGrid()).compareTo(BigDecimal.ZERO))<0)
				  {
					  pagination.setStartRnum(BigDecimal.ZERO);
					  pagination.setEndRnum(pagination.getNoOfRowsToShowInGrid());
				  }
				  else {
					  
					  if(pagination.getEndRnum().compareTo(new BigDecimal(totalRecords))==0)
					  {				
						  BigDecimal temp = pagination.getEndRnum().subtract(pagination.getStartRnum());
						  pagination.setEndRnum(pagination.getEndRnum().subtract(temp));
						  pagination.setStartRnum(pagination.getEndRnum().subtract(pagination.getNoOfRowsToShowInGrid()));
					  }
					  else{
					  pagination.setStartRnum(pagination.getStartRnum().subtract(pagination.getNoOfRowsToShowInGrid()));
				  	  pagination.setEndRnum(pagination.getEndRnum().subtract(pagination.getNoOfRowsToShowInGrid()));
					  }
				  }
			  }
		  }
		  else if(!OrgUtil.isNeitherNullNorEmpty(pagination.getGoToPageNo()))
		  {
			if(!OrgUtil.isNeitherNullNorEmpty(pagination.getStartRnum()) && !OrgUtil.isNeitherNullNorEmpty(pagination.getEndRnum())) 
			{
				pagination.setStartRnum(pagination.getNoOfRowsToShowInGrid().multiply(new BigDecimal(0)));	
				pagination.setEndRnum((pagination.getNoOfRowsToShowInGrid().multiply(BigDecimal.ONE)));
			}
			else
			{
				pagination.setEndRnum(pagination.getStartRnum().add(pagination.getNoOfRowsToShowInGrid()));
				pagination.setStartRnum(pagination.getStartRnum());
			}
		  }
		  if(new BigDecimal(totalRecords).compareTo(pagination.getStartRnum())<0)
		  {
			  pagination.setStartRnum(new BigDecimal(pagination.getTotalNoOfPages()).subtract(BigDecimal.ONE).multiply(pagination.getNoOfRowsToShowInGrid()));
		  }
		  if(pagination.getNoOfRowsToShowInGrid().compareTo(new BigDecimal(pagination.getTotalNoOfRows()))>0)
		  {
			  pagination.setEndRnum(new BigDecimal(pagination.getTotalNoOfRows()));
		  }
		  
		  if(OrgUtil.isNeitherNullNorEmpty(pagination.getGoToPageNo()))
			  pagination.setCurrentPageNo(new BigDecimal(pagination.getGoToPageNo()));
		  else
			  pagination.setCurrentPageNo(pagination.getEndRnum().divide(pagination.getNoOfRowsToShowInGrid(),0,RoundingMode.CEILING));
		
		return pagination;
	}
	public int getTotalRows() {
		return totalRows;
	}
	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}
	public int getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}
	
	

}
