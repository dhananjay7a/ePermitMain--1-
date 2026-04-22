package com.organisation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="tbl_dropdown_mstr")
public class OrgConstitution {

    @Id
    @Column(name="dropdown_id")
    private String dropdownId;

    @Column(name="dropdown_value")
    private String dropdownValue;

    @Column(name="is_active")
    private String isActive;        
}


