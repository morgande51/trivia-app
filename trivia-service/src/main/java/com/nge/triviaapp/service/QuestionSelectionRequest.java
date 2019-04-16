package com.nge.triviaapp.service;

import java.io.Serializable;

import lombok.Data;

@Data
public class QuestionSelectionRequest implements Serializable {

	private Long categoryId;
	
	private Long questionId;
	
	private static final long serialVersionUID = 1L;
}
