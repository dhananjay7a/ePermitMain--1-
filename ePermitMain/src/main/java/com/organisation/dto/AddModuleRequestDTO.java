package com.organisation.dto;

import java.util.List;

import lombok.Data;
@Data
public class AddModuleRequestDTO {
	private String roleId;
    private List<Long> menuIds;
}
