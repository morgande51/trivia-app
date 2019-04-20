package com.nge.triviaapp.domain;

import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class TriviaDataServiceSessionBean implements TriviaDataService {
	
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
	
	@Override
	public <D> D merge(D domain) {
		return em.merge(domain);
	}
	
	@Override
	public <D> void refresh(D domain) {
		em.refresh(domain);
	}
}