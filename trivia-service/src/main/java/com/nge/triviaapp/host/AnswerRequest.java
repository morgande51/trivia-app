package com.nge.triviaapp.host;

import java.io.Serializable;

import javax.json.bind.annotation.JsonbTransient;

import com.nge.triviaapp.domain.QuestionAnswerType;

import lombok.Data;

@Data
public class AnswerRequest implements Serializable {
	
	@JsonbTransient
	private Long categoryId;
	
	@JsonbTransient
	private Long questionId;
	
	private QuestionAnswerType answerType;
	
	private static final long serialVersionUID = 1L;
}
