package com.register.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "un_reg_additional_info_archive")
public class RegisterAdditionalDetailsArchive {

    @Id
    private String orgId;

    private String archiveBy;
    private LocalDateTime archiveOn;
    private String middleName;
    private String isRegistered;
    private String registeredWith;
    private String registrationDetails;
    private String isBgReady;
    private String isGuilty;
    private String commodityCode;
    private String createdBy;
    private LocalDateTime createdDt;
    private String modifiedBy;
    private LocalDateTime modifiedDt;
    private String employeeDetails;
    private String marketYearApplied;
    private String importerExporter;
    private String official1;
    private String official1Father;
    private String official2;
    private String official2Father;
    private String official3;
    private String official3Father;
    private String impExpCommodity;
    private String hadMarketLicense;
    private String hadLicense;
    private String hasPreviousExp;
    private String prevExpRemarks;
    private String prevLicenseRemarks;
    private String prevMarketLicenseRemarks;
    private String guiltyRemarks;
    private String godownDetailsRemarks;
    private String licenseTerms;
    private String breachTerms;
    private String landDetails;
    private String amenitiesDetails;
    private String transactionDetails;
    private String stylePmyard;
    private String situationPmyard;
    private String natureOfInterest;
    private String privateMarketYardArea;
}