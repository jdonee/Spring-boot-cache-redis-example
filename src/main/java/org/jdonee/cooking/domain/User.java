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
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	public User(Long id, String firstName, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	private Long id;
	private String firstName;
	private String lastName;
	private String nickName = "漫步金星";
	private String job = null;
	private Boolean married = false;

}
