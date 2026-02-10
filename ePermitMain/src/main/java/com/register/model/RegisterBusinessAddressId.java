// RegisterBusinessAddressId.java
package com.register.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class RegisterBusinessAddressId {

	private String orgId;
	private String orgBaseMarket;
}
