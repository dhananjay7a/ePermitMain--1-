package com.organisation.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "un_state_district_mstr")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateDistrictMaster {

    @EmbeddedId
    private StateDistrictId id;

    private String districtName;
}
