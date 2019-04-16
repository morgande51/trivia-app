package com.nge.triviaapp.service;

import java.util.Set;

import com.nge.triviaapp.service.category.domain.Category;
import com.nge.triviaapp.service.category.domain.Question;
import com.nge.triviaapp.service.category.domain.Round;

public interface TriviaDataService {

	public Round getRound(Long roundId);

	public Set<Category> getRoundCategories(Long activeRoundId);

	public Set<Question> getRoundCategoryQuestion(Long activeRoundId, Long categoryId);
}