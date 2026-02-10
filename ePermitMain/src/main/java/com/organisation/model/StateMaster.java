package com.organisation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "un_state_mstr")
public class StateMaster {
	@Id
    private String stateCode;
    private String stateName;

}
