package com.nge.triviaapp.game;

import java.util.List;

import javax.ejb.Local;

import com.nge.triviaapp.domain.Category;
import com.nge.triviaapp.domain.Contestant;
import com.nge.triviaapp.domain.Question;
import com.nge.triviaapp.domain.Round;

@Local
public interface GameService {

	public List<Category> getActiveRoundCategories();

	public List<Question> getActiveRoundCategoryQuestions(Long categoryId);

	public Question makeQuestionActive(QuestionSelectionRequest request) throws GameException;

	public List<Round> getAllRounds();
	
	public Round makeRoundActive(Long roundId);

	public Round getActiveRound();

	public void endActiveRound();
	
	public List<Contestant> getContestants();
	
	public void makeContestantActive(Contestant contestant);

	public ActiveGameStateReponse getActiveGameState();

	public void clearActiveQuestion();

	public void clearActiveContestant();
}