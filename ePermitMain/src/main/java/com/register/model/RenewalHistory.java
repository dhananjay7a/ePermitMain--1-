package com.register.model;



import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_renewal_history")
public class RenewalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "org_id")
    private String orgId;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "requested_from_date")
    private Timestamp requestedFromDate;

    @Column(name = "requested_to_date")
    private Timestamp requestedToDate;

    @Column(name = "approved_from_date")
    private Timestamp approvedFromDate;

    @Column(name = "approved_to_date")
    private Timestamp approvedToDate;

    @Column(name = "status")
    private String status;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approved_on")
    private LocalDateTime approvedOn;
}
