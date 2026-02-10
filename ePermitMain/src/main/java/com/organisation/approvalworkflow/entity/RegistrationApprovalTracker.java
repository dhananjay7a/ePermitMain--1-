package com.organisation.approvalworkflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Entity to track all registration approval/rejection actions
 * Audit trail for approval workflow
 */
@Entity
@Table(name = "tbl_registration_approval_tracker", schema = "epermit")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationApprovalTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tracker_id")
    private Long trackerId;

    @Column(name = "org_id", length = 50, nullable = false)
    private String orgId;

    @Column(name = "org_name", length = 255)
    private String orgName;

    @Column(name = "org_category", length = 100)
    private String orgCategory;

    @Column(name = "org_registration_status", length = 50)
    private String orgRegistrationStatus; // Previous status

    @Column(name = "approver_type", length = 50)
    private String approverType; // SCRUTINY, FINAL_APPROVER, DIGITAL_APPROVER

    @Column(name = "approver_action", length = 50)
    private String approverAction; // APPROVE, REJECT

    @Column(name = "approver_user_id", length = 100)
    private String approverUserId;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "new_status", length = 50)
    private String newStatus; // Status after approval/rejection

    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @PrePersist
    public void prePersist() {
        this.createdOn = LocalDateTime.now();
    }

}
