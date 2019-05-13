package com.nge.triviaapp.host;

import com.nge.triviaapp.domain.Contestant;
import com.nge.triviaapp.domain.QuestionAnswerType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AcknowlegedAnswerRequest extends AnswerRequest {
	
	private Contestant contestant;
	
	public AcknowlegedAnswerRequest(AnswerRequest request, Contestant answeringContestant) {
		super();
		setAnswerType(request.getAnswerType());
		setCategoryId(request.getCategoryId());
		setQuestionId(request.getQuestionId());
		if (request.getAnswerType() != QuestionAnswerType.NO_ANSWER) {
			contestant = answeringContestant;
		}
	}

	private static final long serialVersionUID = 1L;
}