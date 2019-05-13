package com.nge.triviaapp.domain;

import java.io.Serializable;

import javax.json.bind.annotation.JsonbVisibility;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="contestant_score")
@Data
@JsonbVisibility(FieldVisibilityStrategy.class)
public class ContestantScore implements Serializable {
	
	@EmbeddedId
	private ContestantScoreKey id;
	
	@ManyToOne
	@JoinColumn(name="round_id", insertable=false, updatable=false)
	private Round round;
	
	@ManyToOne
	@JoinColumn(name="contestant_id", insertable=false, updatable=false)
	private Contestant contestant;
	
	@Column(name="score")
	private Integer score;
	
	public static ContestantScore newInstance(Round round, Contestant contestant) {
		ContestantScoreKey key = new ContestantScoreKey(round, contestant);
		ContestantScore score = new ContestantScore();
		score.setId(key);
		score.setScore(0);
		return score;
	}
	
	private static final long serialVersionUID = 1L;
}