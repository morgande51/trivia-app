package com.nge.triviaapp.domain;

import java.util.List;

import javax.ejb.Local;

@Local
public interface TriviaDataService {

	public Round getRound(Long roundId);

	public List<Category> getRoundCategories(Long activeRoundId);

	public List<Question> getRoundCategoryQuestion(Long activeRoundId, Long categoryId);
	
	public <D> D merge(D domain);
	
	public <D> void refresh(D domain);

	public List<Round> getRounds();

	public List<Contestant> getContestants();

	public <D> void persist(D domain);

	public void flush();
}