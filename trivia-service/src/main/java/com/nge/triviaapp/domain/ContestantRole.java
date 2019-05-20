package com.nge.triviaapp.domain;

import java.io.Serializable;

import javax.json.JsonString;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="contestant_role")
@Data
public class ContestantRole implements Serializable {
	
	@EmbeddedId
	private ContestantRoleKey id;
	
	@ManyToOne
	@JoinColumn(name="contestant_id", insertable=false, updatable=false)
	private Contestant contestant;
	
	public String getRoleName() {
		return id.getRoleName();
	}
	
	public static ContestantRole createFrom(JsonString roleStr, Contestant contestant) {
		ContestantRoleKey id = new ContestantRoleKey();
		id.setContestantId(contestant.getId());
		id.setRoleName(roleStr.getString());
		
		ContestantRole role = new ContestantRole();
		role.setId(id);
		return role;
	}
	
	private static final long serialVersionUID = 1L;
}