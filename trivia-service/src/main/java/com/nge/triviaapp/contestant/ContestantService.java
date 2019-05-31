package com.nge.triviaapp.contestant;

import com.nge.triviaapp.domain.Question;
import com.nge.triviaapp.domain.Round;
import com.nge.triviaapp.host.AnswerRequest;

public interface ContestantService {

	public void handleAnswerRequest(AnswerRequest request);
	
	public void clearBuzzer();

	public BuzzerAcknowledgmentResponse processContestantBuzzard() throws ContestantException;

	public void onActiveRoundChange(Round round);

	public void onActiveQuestionChange(Question question);
}