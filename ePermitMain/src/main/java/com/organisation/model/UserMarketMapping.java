package com.organisation.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "un_user_market_mapping")
public class UserMarketMapping {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "market_code")
    private String marketCode;

    @Column(name = "is_active")
    private String isActive;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;

    // Constructors, getters, setters

    public UserMarketMapping() {
    }

    public UserMarketMapping(String userId, String marketCode, String isActive, String createdBy,
            LocalDateTime createdOn) {
        this.userId = userId;
        this.marketCode = marketCode;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
    }

    // Getters and setters
    public String getId() {
        return userId;
    }

    public void setId(String id) {
        this.userId = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMarketCode() {
        return marketCode;
    }

    public void setMarketCode(String marketCode) {
        this.marketCode = marketCode;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(LocalDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }
}