package com.organisation.dto;

public class ApproveRejectDepositRequestDTO {
    private String trnsNo;
    private String decision;
    private String remarks;

    // Getters and Setters
    public String getTrnsNo() {
        return trnsNo;
    }

    public void setTrnsNo(String trnsNo) {
        this.trnsNo = trnsNo;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}