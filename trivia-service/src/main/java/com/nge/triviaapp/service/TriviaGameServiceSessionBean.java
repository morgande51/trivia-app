package com.nge.triviaapp.service;

import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.nge.triviaapp.security.TriviaSecurity;
import com.nge.triviaapp.service.category.domain.Category;
import com.nge.triviaapp.service.category.domain.Contestant;
import com.nge.triviaapp.service.category.domain.Question;
import com.nge.triviaapp.service.category.domain.Round;

@Singleton
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@PermitAll
public class TriviaGameServiceSessionBean implements TriviaGameService {

	private Long activeRoundId;
	
	private Question activeQuestion;
	
	private Contestant activeContestant;
	
	@Inject
	private TriviaDataService dataService;
	
	@Inject
	private PrincipalLocatorService principalLocatorService;
	
	@Inject
	private Event<AnswerRequest> questionAnsweredEvent;
	
	@RolesAllowed({TriviaSecurity.HOST_ROLE, TriviaSecurity.ADMIN_ROLE})
	public Round makeRoundActive(long roundId) {
		Round round = dataService.getRound(roundId);
		// TODO: null check round
		activeRoundId = roundId;
		return round;
	}
	
	public Set<Category> getActiveRoundCategories() {
		return dataService.getRoundCategories(activeRoundId);
	}
	
	public Set<Question> getActiveRoundCategoryQuestions(long categoryId) {
		return dataService.getRoundCategoryQuestion(activeRoundId, categoryId);
	}
	
	@RolesAllowed(TriviaSecurity.CONTESTANT_ROLE)
	public void makeQuestionActive(QuestionSelectionRequest request) {
		Contestant contestant = principalLocatorService.getPrincipalUser(Contestant.class);
		if (!contestant.equals(activeContestant)) {
			// TODO: handle this
		}	
		activeQuestion = getActiveRoundCategoryQuestion(request.getCategoryId(), request.getQuestionId());
	}
	
	@RolesAllowed(TriviaSecurity.HOST_ROLE)
	public void processAnswerRequest(AnswerRequest request) {	
		Question question = getActiveRoundCategoryQuestion(request.getCategoryId(), request.getQuestionId());
		switch (request.getAnswerType()) {
			case CORRECT:
				activeContestant.updateScore(question, true);
				question.setAnsweredBy(activeContestant);
				resetTimer();
				break;
				
			case INCORRECT:
				activeContestant.updateScore(question, false);
				clearActiveContestant();
				break;
				
			default:
				clearActiveContestant();
		}
		
		questionAnsweredEvent.fire(request);
	}
	
	public String getActiveQuestionText() {
		return activeQuestion.getText();
	}
	
	public String getActiveQuestionAnswer() {
		return activeQuestion.getAnswer();
	}
	
	protected Question getActiveRoundCategoryQuestion(Long categoryId, Long questionId) {
		Set<Question> questions = getActiveRoundCategoryQuestions(categoryId);
		// TODO: null check questions
		
		return questions.stream().filter(q -> q.getId().equals(questionId)).findAny().get();
	}
	
	protected void clearActiveContestant() {
		activeContestant = null;
		resetTimer();
	}
	
	protected void resetTimer() {
		// TODO: write timer for activeQuestion
	}
}