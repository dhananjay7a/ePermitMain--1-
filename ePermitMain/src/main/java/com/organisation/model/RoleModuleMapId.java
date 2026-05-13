package com.organisation.model;


import java.io.Serializable;
import lombok.Data;

@Data
public class RoleModuleMapId implements Serializable {

    private String marketCode;
    private String roleId;
    private Long moduleId;

}
