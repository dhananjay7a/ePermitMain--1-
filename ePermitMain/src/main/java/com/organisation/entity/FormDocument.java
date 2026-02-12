package com.organisation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity to store generated form documents (Form 4, Form 5) for approved
 * registrations
 */
@Entity
@Table(name = "tbl_form_document", schema = "epermit")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_doc_id")
    private Long formDocId;

    @Column(name = "org_id", length = 50, nullable = false)
    private String orgId;

    @Column(name = "form_type", length = 20)
    private String formType; // FORM_FOUR, FORM_FIVE

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "form_status", length = 20)
    private String formStatus;

    @Column(name = "file_path", columnDefinition = "TEXT")
    private String filePath;

    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @PrePersist
    public void prePersist() {
        this.createdOn = LocalDateTime.now();
    }

}
