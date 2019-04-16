package com.nge.triviaapp.service;

import javax.ejb.Local;

import com.nge.triviaapp.service.category.domain.Round;

@Local
public interface TriviaGameService {

	public void makeQuestionActive(QuestionSelectionRequest request);

	public void processAnswerRequest(AnswerRequest request);

	public Round makeRoundActive(long roundId);

}
