package com.organisation.model;



import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_user_digisign_mstr")
@Data
public class UserDigiSignEntity {

	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "thumbprint")
    private String thumbPrint;

    @Column(name = "alias")
    private String alias;

    @Column(name = "issuer")
    private String issuer;

    @Column(name = "is_active")
    private String isActive;   

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_on")
    private LocalDateTime createdOn;
}

