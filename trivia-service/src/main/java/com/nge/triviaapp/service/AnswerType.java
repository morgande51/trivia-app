package com.nge.triviaapp.service;

public enum AnswerType {

	CORRECT, INCORRECT, NO_ANSWER;
	
	public boolean isCorrect() {
		return this == AnswerType.CORRECT;
	}
}