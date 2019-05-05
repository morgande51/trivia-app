package com.nge.triviaapp.game;

import com.nge.triviaapp.domain.Contestant;

import lombok.Getter;

public class NotActiveContestantException extends GameException {
	
	public static final String ERROR_MSG = "Contestant must be active to execute this function";

	@Getter
	private Contestant activeContestant;
	
	@Getter
	private Contestant targetContestant;
	
	public NotActiveContestantException(Contestant activeContestant, Contestant contestant) {
		super(ERROR_MSG);
		this.activeContestant = activeContestant;
		this.targetContestant = contestant;
	}

	private static final long serialVersionUID = 1L;
}