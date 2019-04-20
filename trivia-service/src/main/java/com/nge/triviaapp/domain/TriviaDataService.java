package com.nge.triviaapp.domain;

import java.util.Set;

import javax.ejb.Local;

@Local
public interface TriviaDataService {

	public Round getRound(Long roundId);

	public Set<Category> getRoundCategories(Long activeRoundId);

	public Set<Question> getRoundCategoryQuestion(Long activeRoundId, Long categoryId);
	
	public <D> D merge(D domain);
	
	public <D> void refresh(D domain);
}