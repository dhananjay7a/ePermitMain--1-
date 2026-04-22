package com.organisation.model;



import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "tbl_modules_mstr")
public class ModulesMstr {

    @Id
    private Long moduleId;

   
    private String moduleName;

  
    private String moduleUrl;

    
    private String lowLevelSecure;

    
    private String mediumLevelSecure;

  
    private String highLevelSecure;

   
    private Integer parentModuleId;

   
    private Integer srNo;

   
    private String moduleOwner;

   
    private String moduleNm1;

   
    private String moduleNm2;

   
    private String createdBy;

  
    private Timestamp createdOn;

   
    private String modifiedBy;

   
    private Timestamp modifiedOn;

}