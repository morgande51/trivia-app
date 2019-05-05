package com.nge.triviaapp.game;

import com.nge.triviaapp.domain.Contestant;
import com.nge.triviaapp.domain.Question;

import lombok.Getter;

public class ActiveQuestionException extends GameException {

	@Getter
	private Contestant activeContestant;
	
	@Getter
	private Question activeQuestion;
	
	public ActiveQuestionException(Contestant activeContestant) {
		super("Contesant is not active");
		this.activeContestant = activeContestant;
	}

	public ActiveQuestionException(Question activeQuestion) {
		super("Question already answered");
		this.activeQuestion = activeQuestion;
	}

	private static final long serialVersionUID = 1L;
}