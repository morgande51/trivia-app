package com.nge.triviaapp.domain;

import java.io.Serializable;

import javax.json.JsonString;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="contestant_role")
@IdClass(ContestantRole.class)
@Data
@EqualsAndHashCode(exclude="contestant")
public class ContestantRole implements Serializable {
	
	@Id
	@ManyToOne
	@JoinColumn(name="contestant_id")
	private Contestant contestant;
	
	@Id
	@Column(name="roleName")
	private String roleName;
	
	public static ContestantRole createFrom(JsonString roleStr, Contestant contestant) {
		ContestantRole role = new ContestantRole();
		role.setRoleName(roleStr.getString());
		role.setContestant(contestant);
		return role;
	}
	
	private static final long serialVersionUID = 1L;
}