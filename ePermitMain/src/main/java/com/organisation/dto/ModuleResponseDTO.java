package com.organisation.dto;

import lombok.Data;

@Data
public class ModuleResponseDTO {
	 private Long moduleId;
	 private String moduleName;
	 public ModuleResponseDTO(Long moduleId, String moduleName) {
		super();
		this.moduleId = moduleId;
		this.moduleName = moduleName;
	 }
	 
	 
}
