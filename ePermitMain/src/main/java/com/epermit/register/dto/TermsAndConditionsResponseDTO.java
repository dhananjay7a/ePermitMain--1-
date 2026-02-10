package com.epermit.register.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TermsAndConditionsResponseDTO {
	private Long id;
	private String description;
	private boolean mandatory;
}
