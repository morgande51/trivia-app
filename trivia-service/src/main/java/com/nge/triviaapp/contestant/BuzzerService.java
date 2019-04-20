package com.nge.triviaapp.contestant;

import com.nge.triviaapp.host.AnswerRequest;

public interface BuzzerService {

	public void handleAnswerRequest(AnswerRequest request);
	
	public void clearBuzzer();

	public BuzzerAcknowledgmentResponse processContestantBuzzard();
}
