package com.epermit.register.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegDetailsResponseDTO {

    private String orgId;
    private String orgName;
    private Boolean unifiedLicense;
    private String orgCategory;
    private String orgBaseMarket; // conditionally populated
}
