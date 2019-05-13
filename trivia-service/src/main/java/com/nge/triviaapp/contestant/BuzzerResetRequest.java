package com.nge.triviaapp.contestant;

import java.io.Serializable;

import com.nge.triviaapp.domain.QuestionAnswerType;

import lombok.Getter;

@Getter
public class BuzzerResetRequest implements Serializable {

	private QuestionAnswerType answerType;
	
	/**
	 * Constructor
	 * @param answerType The answerType to set
	 */
	public BuzzerResetRequest(QuestionAnswerType answerType) {
		super();
		this.answerType = answerType;
	}

	private static final long serialVersionUID = 1L;
}
