package com.organisation.util;



import java.time.LocalDate;

import com.register.model.FinancialYear;

public class FinancialYearUtil {
	
	// Creting for getting current financial year - start and end date (31-03-2025 & 01-04-2026)
	public static FinancialYear getCurrentFinancialYear() {

        LocalDate today = LocalDate.now();

        int startYear;

        if (today.getMonthValue() >= 4) {
            startYear = today.getYear();
        } else {
            startYear = today.getYear() - 1;
        }

        LocalDate start = LocalDate.of(startYear, 4, 1);
        LocalDate end = LocalDate.of(startYear + 1, 3, 31);

        return new FinancialYear(start, end);
    }
	
	// Creating this function for getting label of current FY
	public static String getCurrentFinancialYearLabel() {

	    FinancialYear fy = getCurrentFinancialYear();

	    int startYear = fy.getStartDate().getYear();
	    int endYear = fy.getEndDate().getYear();

	    return String.valueOf(startYear).substring(2) + "-" +
	           String.valueOf(endYear).substring(2);
	}


	// Creting for getting next financial year - start and end date (31-03-2025 & 01-04-2026)
    public static FinancialYear getNextFinancialYear() {

        FinancialYear current = getCurrentFinancialYear();

        LocalDate start = current.getStartDate().plusYears(1);
        LocalDate end = current.getEndDate().plusYears(1);

        return new FinancialYear(start, end);
    }
    
    // Getting end date of current financial year like 31-03-2026
    public static LocalDate getCurrentFinancialYearEnd() {   

        LocalDate today = LocalDate.now();

        int year;

        if (today.getMonthValue() >= 4) {
            year = today.getYear();
        } else {
            year = today.getYear() - 1;
        }

        return LocalDate.of(year + 1, 3, 31);
    }
    
	// Creting for getting previous financial year - start and end date (31-03-2025 & 01-04-2026)
    public static FinancialYear getPreviousFinancialYear() {

        FinancialYear current = getCurrentFinancialYear();

        LocalDate start = current.getStartDate().minusYears(1);
        LocalDate end = current.getEndDate().minusYears(1);

        return new FinancialYear(start, end);
    }
    
    public static String getPreviousFinancialYearLabel() {   // returns like : 25-26

       int year = LocalDate.now().getYear() %100;
       
       String prevFY = (year -1) + "-" + year;
       
       return prevFY;
       
    }
    
    public static FinancialYear[] getPrevCurrentAndNextFinancialYear() {
    	/*
    	Getting prev , current and next financial year within an array of FinancialYear just like 
    	Previous FY
		Start : 2024-04-01
		End   : 2025-03-31
		
		Current FY
		Start : 2025-04-01
		End   : 2026-03-31
		
		Next FY
		Start : 2026-04-01
		End   : 2027-03-31
    	*/
    	
        FinancialYear current = getCurrentFinancialYear();

        FinancialYear previous = new FinancialYear(
                current.getStartDate().minusYears(1),
                current.getEndDate().minusYears(1)
        );

        FinancialYear next = new FinancialYear(
                current.getStartDate().plusYears(1),
                current.getEndDate().plusYears(1)
        );

        return new FinancialYear[]{previous, current, next};
    }

    public static FinancialYear[] getCurrentAndNextFinancialYear() {

        FinancialYear current = getCurrentFinancialYear();

        FinancialYear next = new FinancialYear(
                current.getStartDate().plusYears(1),
                current.getEndDate().plusYears(1)
        );

        return new FinancialYear[]{current, next};
    }

    public static String getNextFinancialYearLabel() {

        FinancialYear current = getCurrentFinancialYear();

        int startYear = current.getStartDate().getYear() + 1;
        int endYear = startYear + 1;

        return String.valueOf(startYear).substring(2) + "-" +
               String.valueOf(endYear).substring(2);
    }

    public static String getLaterFinancialYearLabel() {

        FinancialYear current = getCurrentFinancialYear();

        int startYear = current.getStartDate().getYear() + 2;
        int endYear = startYear + 1;

        return String.valueOf(startYear).substring(2) + "-" +
               String.valueOf(endYear).substring(2);
    }

    

}
