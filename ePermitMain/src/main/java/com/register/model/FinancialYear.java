package com.register.model;

import java.time.LocalDate;

public class FinancialYear {
	 private LocalDate startDate;
	    private LocalDate endDate;
	   
	    
	    public FinancialYear(LocalDate startDate, LocalDate endDate) {
	        this.startDate = startDate;
	        this.endDate = endDate;
	    }

	    public LocalDate getStartDate() {
	        return startDate;
	    }

	    public LocalDate getEndDate() {
	        return endDate;
	    }
}
