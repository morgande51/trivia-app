package com.nge.triviaapp.domain;

public enum QuestionAnswerType {

	CORRECT, INCORRECT, NO_ANSWER;
	
	public boolean isCorrect() {
		return this == QuestionAnswerType.CORRECT;
	}
}