package com.organisation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_mst_org_category")
public class OrgCategoryMaster {

    @Id
    private String orgCategory;

    private String orgCategoryName;

    @Column(name = "org_is_host")
    private boolean isHost;

    private String orgUserScope;
    private String orgCategoryScope;
    private String orgSelfRegAllowed;
    private String orgDfltUserRole;

    @Column(name = "org_is_member")
    private boolean isMember;

    private String categoryType;
    private String isAutoApprove;
    private String createdOn;
    private String createdBy;
    private String modifiedOn;
    private String modifiedBy;
}
