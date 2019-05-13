package com.nge.triviaapp.host;

import java.util.concurrent.Future;

import javax.ejb.Local;

import com.nge.triviaapp.domain.Contestant;
import com.nge.triviaapp.domain.Question;
import com.nge.triviaapp.domain.Round;

@Local
public interface HostService {

	public void processAnswerRequest(AnswerRequest request);

	public Future<AcknowlegedAnswerRequest> getHostAnswer();

	public String getActiveQuestionAnswer();
	
	public void onActiveQuestionChange(Question question);

	public void onActiveContestantChange(Contestant contestant);

	public void onActiveRoundChange(Round round);

	public void onActiveRoundEnd(Round round);

	public void onActiveHostAnswer(AnswerRequest request);
}