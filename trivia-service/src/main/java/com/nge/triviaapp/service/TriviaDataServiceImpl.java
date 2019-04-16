package com.nge.triviaapp.service;

import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.nge.triviaapp.service.category.domain.Category;
import com.nge.triviaapp.service.category.domain.Question;
import com.nge.triviaapp.service.category.domain.Round;

@RequestScoped
public class TriviaDataServiceImpl implements TriviaDataService {
	
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public Round getRound(Long roundId) {
		return em.find(Round.class, roundId);
	}

	@Override
	public Set<Category> getRoundCategories(Long activeRoundId) {
		return getRound(activeRoundId).getCategories();
	}

	@Override
	public Set<Question> getRoundCategoryQuestion(Long activeRoundId, Long categoryId) {
		return getRound(activeRoundId).getCategories().stream()
				.filter(c -> c.getId().equals(categoryId))
				.findAny()
				.get()
				.getQuestions();
	}
}