package org.jdonee.cooking.domain;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Address implements Serializable {

	private static final long serialVersionUID = 1L;

	public Address(Long id, String province, String city) {
		super();
		this.id = id;
		this.province = province;
		this.city = city;
	}

	private Long id;
	private String province;
	private String city;
	private String zipcode = "000000";
}
