package com.register.model;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_bank_gst_temp")
public class BankGstDetails {

    @Id
    private String orgId;
    private String orgGstNo;
    private String orgGstState;
    private String orgBankName;
    private String orgBankAccNo;
    private String orgIfscCode;
    private String orgBankBranch;
    private LocalDateTime orgCreatedOn;
    private String orgCreatedBy;
    private LocalDateTime orgModifiedOn;
    private String orgModifiedBy;
    
   }
