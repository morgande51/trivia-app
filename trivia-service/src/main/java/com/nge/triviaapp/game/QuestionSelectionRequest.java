package com.nge.triviaapp.game;

import java.io.Serializable;

import lombok.Data;

@Data
public class QuestionSelectionRequest implements Serializable {

	private Long categoryId;
	
	private Integer questionValue;
	
	private static final long serialVersionUID = 1L;
}
