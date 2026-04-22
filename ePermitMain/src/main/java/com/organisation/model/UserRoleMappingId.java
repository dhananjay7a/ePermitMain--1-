package com.organisation.model;


import java.io.Serializable;
import lombok.Data;

@Data
public class UserRoleMappingId implements Serializable {

    private String userId;
    private String roleId;

}