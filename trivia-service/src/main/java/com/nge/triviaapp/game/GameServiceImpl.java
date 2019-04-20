package com.nge.triviaapp.game;

import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.nge.triviaapp.domain.ActiveLocator;
import com.nge.triviaapp.domain.ActiveUpdate;
import com.nge.triviaapp.domain.Category;
import com.nge.triviaapp.domain.Contestant;
import com.nge.triviaapp.domain.Question;
import com.nge.triviaapp.domain.Round;
import com.nge.triviaapp.domain.TriviaDataService;
import com.nge.triviaapp.domain.TriviaSecurity;
import com.nge.triviaapp.security.PrincipalLocatorService;

import lombok.Getter;
import lombok.extern.java.Log;

@ApplicationScoped
@PermitAll
@Log
public class GameServiceImpl implements GameService, ActiveLocator {

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
	
	@Override
	public Set<Category> getActiveRoundCategories() {
		return dataService.getRoundCategories(activeRound.getId());
	}
	
	@Override
	public Set<Question> getActiveRoundCategoryQuestions(long categoryId) {
		return dataService.getRoundCategoryQuestion(activeRound.getId(), categoryId);
	}
	
	@RolesAllowed(TriviaSecurity.CONTESTANT_ROLE)
	@Override
	public void makeQuestionActive(QuestionSelectionRequest request) {
		Contestant contestant = principalLocatorService.getPrincipalUser(Contestant.class);
		if (!contestant.equals(activeContestant)) {
			// TODO: handle this
		}	
		activeQuestion = getActiveRoundCategoryQuestion(request.getCategoryId(), request.getQuestionId());
	}
	
	protected Question getActiveRoundCategoryQuestion(Long categoryId, Long questionId) {
		Set<Question> questions = getActiveRoundCategoryQuestions(categoryId);
		// TODO: null check questions
		
		return questions.stream().filter(q -> q.getId().equals(questionId)).findAny().get();
	}
	
	public void updateActiveRound(@Observes @ActiveUpdate(Round.class) Round round) {
		this.activeRound = round;
	}
	
	public void updateActiveContestant(@Observes @ActiveUpdate(Contestant.class) Contestant contestant) {
		this.activeContestant = contestant;
	}
	
	public void updateActiveQuestion(@Observes @ActiveUpdate(Question.class) Question question) {
		this.activeQuestion = question;
	}
}