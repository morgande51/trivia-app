package com.nge.triviaapp.service;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class BuzzerResetRequest implements Serializable {

	private AnswerType answerType;
	
	/**
	 * Constructor
	 * @param answerType The answerType to set
	 */
	public BuzzerResetRequest(AnswerType answerType) {
		super();
		this.answerType = answerType;
	}

	private static final long serialVersionUID = 1L;
}
