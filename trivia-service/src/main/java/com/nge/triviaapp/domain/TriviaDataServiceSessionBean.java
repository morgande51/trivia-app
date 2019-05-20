package com.nge.triviaapp.domain;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class TriviaDataServiceSessionBean implements TriviaDataService {
	
	private static final String ROUND_CATEGORY_QUESTIONS_QRY = "Question.findByRoundCategory";
	private static final String FIND_ALL_ROUNDS_QRY = "Rounds.findAll";
	private static final String ROUND_CATEGORY_QRY = "Category.findByRound";
	private static final String FIND_ALL_CONTESTANTS_QRY = "Contestant.findAll";
	private static final String FIND_CONTESTANT_BY_EMAIL_QRY = "Contestant.findByEmail";
	private static final String ROUND_PARAM = "roundId";
	private static final String CATEGORY_PARAM = "categoryId";
	private static final String EMAIL_PARAM = "email";
	
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public Round getRound(Long roundId) {
		return em.find(Round.class, roundId);
	}
	
	@Override
	public List<Round> getRounds() {
		return em.createNamedQuery(FIND_ALL_ROUNDS_QRY, Round.class).getResultList();
	}
	
	@Override
	public List<Category> getRoundCategories(Long roundId) {
		return em.createNamedQuery(ROUND_CATEGORY_QRY, Category.class).setParameter(ROUND_PARAM, roundId).getResultList();
	}

	@Override
	public List<Question> getRoundCategoryQuestion(Long roundId, Long categoryId) {
		return em.createNamedQuery(ROUND_CATEGORY_QUESTIONS_QRY, Question.class)
				.setParameter(ROUND_PARAM, roundId)
				.setParameter(CATEGORY_PARAM, categoryId)
				.getResultList();
	}
	
	@Override
	public List<Contestant> getContestants() {
		return em.createNamedQuery(FIND_ALL_CONTESTANTS_QRY, Contestant.class).getResultList();
	}
	
	@Override
	public Contestant getContestantByEmail(String email) {
		return em.createNamedQuery(FIND_CONTESTANT_BY_EMAIL_QRY, Contestant.class)
				.setParameter(EMAIL_PARAM, email.toUpperCase())
				.getResultStream()
				.findAny()
				.orElse(null);
	}
	
	@Override
	public <D> D merge(D domain) {
		return em.merge(domain);
	}
	
	@Override
	public <D> void refresh(D domain) {
		em.refresh(domain);
	}
	
	@Override
	public <D> void persist(D domain) {
		em.persist(domain);
	}
	
	@Override
	public void flush() {
		em.flush();
	}
}