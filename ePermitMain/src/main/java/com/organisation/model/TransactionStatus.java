package com.organisation.model;



public enum TransactionStatus {
		PENDING("P"),
	    APPROVED("A"),
	    REJECTED("R");

	    private final String code;

	    TransactionStatus(String code) {
	        this.code = code;
	    }

	    public String getCode() {
	        return code;
	    }

	    public static TransactionStatus fromCode(String code) {
	        for (TransactionStatus status : values()) {
	            if (status.code.equals(code)) {
	                return status;
	            }
	        }
	        throw new IllegalArgumentException("Invalid status code: " + code);
	    }

}
