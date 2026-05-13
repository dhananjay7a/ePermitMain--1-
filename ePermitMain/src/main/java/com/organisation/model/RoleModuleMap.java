package com.organisation.model;


import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "tbl_role_module_map")
@IdClass(RoleModuleMapId.class)
public class RoleModuleMap {

    @Id
    private String marketCode;

    @Id
   private String roleId;

    @Id
   private Long moduleId;

    
    private String createdBy;

   
    private LocalDateTime createdOn;

   
    private String modifiedBy;

    
    private LocalDateTime modifiedOn;

}
