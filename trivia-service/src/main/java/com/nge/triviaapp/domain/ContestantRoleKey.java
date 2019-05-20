package com.nge.triviaapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
@Data
public class ContestantRoleKey implements Serializable {
	
	@Column(name="contestant_id")
	private Long contestantId;
	
	@Column(name="role_name")
	private String roleName;
	
	private static final long serialVersionUID = 1L;
}
