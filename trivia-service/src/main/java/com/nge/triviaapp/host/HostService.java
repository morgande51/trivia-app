package com.nge.triviaapp.host;

import java.util.concurrent.Future;

import javax.ejb.Local;

import com.nge.triviaapp.domain.Round;

@Local
public interface HostService {

	public void processAnswerRequest(AnswerRequest request);

	public Round makeRoundActive(long roundId);

	public Future<AcknowlegedAnswerRequest> getHostAnswer();

	public String getActiveQuestionAnswer();
}
