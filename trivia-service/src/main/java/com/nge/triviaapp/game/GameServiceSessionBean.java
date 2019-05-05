package com.nge.triviaapp.game;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import static com.nge.triviaapp.security.TriviaSecurity.*;

import com.nge.triviaapp.domain.ActiveDomain;
import com.nge.triviaapp.contestant.BuzzerAcknowledgmentResponse;
import com.nge.triviaapp.domain.Active;
import com.nge.triviaapp.domain.ActiveActionType;
import com.nge.triviaapp.domain.Category;
import com.nge.triviaapp.domain.Contestant;
import com.nge.triviaapp.domain.Question;
import com.nge.triviaapp.domain.QuestionAnswerType;
import com.nge.triviaapp.domain.Round;
import com.nge.triviaapp.domain.TriviaDataService;
import com.nge.triviaapp.host.AnswerRequest;
import com.nge.triviaapp.security.PrincipalLocatorService;

import lombok.Getter;
import lombok.extern.java.Log;

@Singleton
@PermitAll
@Log
public class GameServiceSessionBean implements GameService {
	
	@Getter
	private Round activeRound;
	
	@Getter
	private Question activeQuestion;
	
	@Getter
	private Contestant activeContestant;
	
	@Inject
	private TriviaDataService dataService;
	
	@Inject
	private PrincipalLocatorService principalLocatorService;
	
	@Inject
	private Event<ActiveDomain> activeEvent;
	
	public List<Category> getActiveRoundCategories() {
		return dataService.getRoundCategories(activeRound.getId());
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Question> getActiveRoundCategoryQuestions(long categoryId) {
		return dataService.getRoundCategoryQuestion(activeRound.getId(), categoryId);
	}
	
	@RolesAllowed({CONTESTANT_ROLE, ADMIN_ROLE})
	@Override
	public Question makeQuestionActive(QuestionSelectionRequest request) throws NotActiveContestantException, ActiveQuestionException {
		Contestant contestant = principalLocatorService.getPrincipalUser(Contestant.class);
		if (!contestant.equals(activeContestant)) {
			log.severe(contestant + " != " + activeContestant);
			throw new NotActiveContestantException(activeContestant, contestant);
		}
		else if (activeQuestion != null) {
			log.severe("Cannot update activeQuestion while its still active: " + contestant);
			throw new ActiveQuestionException(contestant);
		}
		activeQuestion = getActiveRoundCategoryQuestion(request);
		if (activeQuestion.getAnswerType() != null) {
			throw new ActiveQuestionException(activeQuestion);
		}
		activeEvent.select(activeQuestion.getLiteral()).fire(activeQuestion);
		return activeQuestion;
	}
	
	@Override
	public List<Round> getAllRounds() {
		return dataService.getRounds();
	}
	
	@RolesAllowed({HOST_ROLE, ADMIN_ROLE})
	@Lock
	public Round makeRoundActive(long roundId) {
		Round round = dataService.getRound(roundId);
		// TODO: null check round
		activeRound = round;
		activeEvent.select(round.getLiteral()).fire(round);
		return round;
	}
	
	@RolesAllowed({HOST_ROLE, ADMIN_ROLE})
	@Lock
	public void endActiveRound() {
		activeEvent.select(activeRound.getLiteral(ActiveActionType.DELETE)).fire(activeRound);
		activeRound = null;
		activeQuestion = null;
	}
	
	public List<Contestant> getContestants() {
		return dataService.getContestants();
	}
	
	@RolesAllowed({HOST_ROLE, ADMIN_ROLE})
	@Lock
	public void setActiveContestant(Contestant contestant) {
		AnnotationLiteral<Active> literal = null;
		if (contestant == null && activeContestant == null) {
			log.warning("Somehow the active contestant and target contestant are both null");
		}
		else if (contestant != null) {
			literal = contestant.getLiteral();
		}
		else {
			literal = activeContestant.getLiteral();
		}
		
		if (literal != null) {
			activeEvent.select(literal).fire(contestant);
		}
		activeContestant = contestant;
	}
	
	@Lock
	public void handleActiveBuzzerEvent(@Observes BuzzerAcknowledgmentResponse response ) {
		setActiveContestant(response.getContestant());
	}
	
	@Lock
	public void handleHostAnswerEvent(@Observes AnswerRequest request) {
		switch (request.getAnswerType()) {
		case CORRECT:
			this.activeQuestion = null;
			break;
			
		case INCORRECT:
			this.activeContestant = null;
			break;
			
		default:
			this.activeContestant = null;
			this.activeQuestion = null;
		}
	}
	
	protected Question getActiveRoundCategoryQuestion(QuestionSelectionRequest request) {
		List<Question> questions = getActiveRoundCategoryQuestions(request.getCategoryId());
		// TODO: null check questions
		
		return questions.stream().filter(q -> q.getValue().equals(request.getQuestionValue())).findAny().get();
	}
}