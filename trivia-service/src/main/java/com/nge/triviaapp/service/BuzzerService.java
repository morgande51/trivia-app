package com.nge.triviaapp.service;

public interface BuzzerService {

	public void handleAnswerRequest(AnswerRequest request);
	
	public void clearBuzzer();

	public BuzzerAcknowledgmentResponse processContestantBuzzard();
}
