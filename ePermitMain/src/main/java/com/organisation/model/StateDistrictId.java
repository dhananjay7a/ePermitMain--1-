package com.organisation.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class StateDistrictId implements Serializable {
	@Column(name = "state_code")
    private String stateCode;

    @Column(name = "district_code")
    private String districtCode;
}
