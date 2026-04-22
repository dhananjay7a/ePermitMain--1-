package com.organisation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_mst_terms_and_conditions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TermsAndConditions {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "form_type", nullable = false, length = 100)
    private String formType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_mandatory", nullable = false)
    private boolean isMandatory;
}
