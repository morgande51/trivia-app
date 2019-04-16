package com.nge.triviaapp.service.category.domain;

import java.io.Serializable;
import java.security.Principal;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Entity
@Table(name="contestant")
@NamedQueries(
	@NamedQuery(name="Contestant.findFromEmail", query="select c from Contestant c where c.email = :email")
)
@Data
@EqualsAndHashCode(exclude="totalScore")
public class Contestant implements Principal, Serializable {
	
	@Id
	@SequenceGenerator(name="contestant_seq_gen", sequenceName="contestant_seq")
	@GeneratedValue(generator="contestant_seq_gen", strategy=GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="email")
	private String email;
	
	@Column(name="password")
	@JsonbTransient
	private String passwordHash;
	
	@Transient
	@Setter(AccessLevel.NONE)
	@JsonbProperty
	private int totalScore;
	
	public void updateScore(Question question, boolean correctAnswer) {
		int value = question.getValue();
		if (correctAnswer) {
			totalScore += value;
		}
		else {
			totalScore -= value;
		}
	}
	@Override
	public String getName() {
		return email;
	}
	
	public static <T extends Principal> Contestant as(T user) {
		return (Contestant) user;
	}
	
	private static final long serialVersionUID = 1L;
}