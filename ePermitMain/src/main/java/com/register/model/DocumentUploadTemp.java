package com.register.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name="tbl_registration_documents_temp")
public class DocumentUploadTemp {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

    private String orgId;
    private String docType;
    private String docName;
    private String uploadPath;
    private String createdBy;
    private String modifiedBy;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;

}
