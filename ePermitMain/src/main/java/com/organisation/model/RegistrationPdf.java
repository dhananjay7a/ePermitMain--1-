package com.organisation.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "tbl_registration_pdf")
@Data
public class RegistrationPdf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orgId;

    private String pdfPath;

    private String status;   // SIGNED

    private LocalDateTime createdOn;
}
