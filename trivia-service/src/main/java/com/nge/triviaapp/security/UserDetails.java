package com.nge.triviaapp.security;

import java.io.Serializable;

import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.bind.annotation.JsonbTransient;

import com.nge.triviaapp.domain.Contestant;

import lombok.Data;

@Data
public class UserDetails implements Serializable {
	
	private String[] userRoles;
	
	@JsonbTransient
	private Contestant contestant;
	
	public UserDetails(JsonObject userData) {
		super();
		contestant = new Contestant();
		contestant.setEmail(userData.getString("userEmail"));
		contestant.setPasswordHash(userData.getString("userPwd"));
		contestant.setFirstName(userData.getString("firstName", "John"));
		contestant.setLastName(userData.getString("lastName", "Doe"));
		userRoles = userData.getJsonArray("userRoles").stream()
				.map(r -> ((JsonString) r).getString())
				.toArray(i ->  new String[i]);;
	}
	
	public String getUserEmail() {
		return contestant.getEmail();
	}
	
	@JsonbTransient
	public String getUserPwd() {
		return contestant.getPasswordHash();
	}
	private static final long serialVersionUID = 1L;
}