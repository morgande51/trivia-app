package com.nge.triviaapp.contestant;

import com.nge.triviaapp.domain.Contestant;

import lombok.Getter;

public class ContestantException extends Exception {
	
	public enum Reason { DUPLICATE, NO_ACTIVE_QUESTION }
	
	@Getter
	private Contestant contestant;
	
	@Getter
	private Reason reason;

	public ContestantException(Contestant contestant, Reason reason) {
		this.contestant = contestant;
		this.reason = reason;
	}

	private static final long serialVersionUID = 1L;
}