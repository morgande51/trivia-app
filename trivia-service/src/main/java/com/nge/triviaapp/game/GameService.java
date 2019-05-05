package com.nge.triviaapp.game;

import java.util.List;

import javax.ejb.Local;

import com.nge.triviaapp.contestant.BuzzerAcknowledgmentResponse;
import com.nge.triviaapp.domain.Category;
import com.nge.triviaapp.domain.Contestant;
import com.nge.triviaapp.domain.Question;
import com.nge.triviaapp.domain.Round;
import com.nge.triviaapp.host.AnswerRequest;

@Local
public interface GameService {

	public List<Category> getActiveRoundCategories();

	public List<Question> getActiveRoundCategoryQuestions(long categoryId);

	public Question makeQuestionActive(QuestionSelectionRequest request) throws GameException;

	public List<Round> getAllRounds();
	
	public Round makeRoundActive(long roundId);

	public Round getActiveRound();

	public void endActiveRound();
	
	public List<Contestant> getContestants();
	
	public void setActiveContestant(Contestant contestant);

	public void handleActiveBuzzerEvent(BuzzerAcknowledgmentResponse response);

	public void handleHostAnswerEvent(AnswerRequest request);
}