package com.nge.triviaapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
@Data
public class ContestantScoreKey implements Serializable {
	
	@Column(name="round_id", nullable=false)
	private Long roundId;
	
	@Column(name="contestant_id", nullable=false)
	private Long contestantId;
	
	public ContestantScoreKey(Round round, Contestant contestant) {
		super();
		this.roundId = round.getId();
		this.contestantId = contestant.getId();
	}

	public ContestantScoreKey() {
		super();
	}

	private static final long serialVersionUID = 1L;
}
