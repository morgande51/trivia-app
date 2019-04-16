package com.nge.triviaapp.service;

import java.io.Serializable;

import lombok.Data;

@Data
public class AnswerRequest implements Serializable {

	private Long categoryId;
	
	private Long questionId;
	
	private AnswerType answerType;
	
	private static final long serialVersionUID = 1L;
}
