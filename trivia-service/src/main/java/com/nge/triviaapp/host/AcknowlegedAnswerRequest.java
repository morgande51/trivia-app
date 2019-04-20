package com.nge.triviaapp.host;

import com.nge.triviaapp.domain.Contestant;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AcknowlegedAnswerRequest extends AnswerRequest {
	
	private String answeringContestantEmail;
	
	public AcknowlegedAnswerRequest(AnswerRequest request, Contestant answeringContestant) {
		super();
		setAnswerType(request.getAnswerType());
		setCategoryId(request.getCategoryId());
		setQuestionId(request.getQuestionId());
		if (request.getAnswerType() != AnswerType.NO_ANSWER) {
			answeringContestantEmail = answeringContestant.getEmail();
		}
	}

	private static final long serialVersionUID = 1L;
}