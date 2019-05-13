package com.nge.triviaapp.game;

import java.io.Serializable;

import com.nge.triviaapp.domain.Contestant;
import com.nge.triviaapp.domain.Question;
import com.nge.triviaapp.domain.Round;

import lombok.Getter;

@Getter
public class ActiveGameStateReponse implements Serializable {

	private Round activeRound;
	
	private Question activeQuestion;
	
	private Contestant activeContestant;

	public ActiveGameStateReponse(Round activeRound, Question activeQuestion, Contestant activeContestant) {
		super();
		this.activeRound = activeRound;
		this.activeQuestion = activeQuestion;
		this.activeContestant = activeContestant;
	}
	
	private static final long serialVersionUID = 1L;
}