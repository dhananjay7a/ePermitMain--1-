package com.organisation.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "un_pin_code_mstr")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DistrictPinCodeMstr {

    @EmbeddedId
    private PinCodeMstrId id;

    @Column(name = "state_name")
    private String stateName;

    @Column(name = "district_name")
    private String districtName;

    @Column(name = "block_name")
    private String blockName;

    @Column(name = "sub_division")
    private String subDivision;

    @Column(name = "sub_dist_name")
    private String subDistName;

    @Column(name = "village_name")
    private String villageName;

    @Column(name = "locality_detail_1")
    private String localityDetail1;

    @Column(name = "locality_detail_2")
    private String localityDetail2;

    @Column(name = "locality_detail_3")
    private String localityDetail3;

    @Column(name = "office_name")
    private String officeName;
}
