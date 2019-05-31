package com.nge.triviaapp.game;

import static com.nge.triviaapp.security.TriviaSecurity.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.nge.triviaapp.domain.ActiveDomainException;
import com.nge.triviaapp.domain.ActiveDomainManager;
import com.nge.triviaapp.domain.Category;
import com.nge.triviaapp.domain.Contestant;
import com.nge.triviaapp.domain.Question;
import com.nge.triviaapp.domain.Round;
import com.nge.triviaapp.domain.TriviaDataService;
import com.nge.triviaapp.security.PrincipalLocatorService;

import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.Slf4j;

@Stateless
@PermitAll
@Slf4j
public class GameServiceSessionBean implements GameService {
	
	@Inject
	private ActiveDomainManager domainManager;
	
	@Inject
	private TriviaDataService dataService;
	
	@Inject
	private PrincipalLocatorService principalLocatorService;
	
	public void init() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try (InputStream gotStream = cl.getResourceAsStream("got.json")) {
			JsonReader usersReader = Json.createReader(gotStream);
			JsonObject jsonObj = usersReader.readObject();
			Set<Round> rounds = jsonObj.getJsonArray("rounds")
					.stream()
					.map(r -> Round.createFrom(r.asJsonObject()))
					.collect(Collectors.toSet());
			rounds.forEach(r -> {
				Set<Category> categories = new HashSet<>(r.getCategories());
				r.setCategories(null);
				dataService.persist(r);
				dataService.flush();
				categories.forEach(category -> {
					category.setRound(r);
					Set<Question> questions = new HashSet<>(category.getQuestions());
					category.setQuestions(null);
					dataService.persist(category);
					dataService.flush();
					category.setQuestions(questions);
				});
			});
		}
		catch (IOException e) {
			
		}
	}
	
	public List<Category> getActiveRoundCategories() {
		Round activeRound = domainManager.getActiveRound();
		return dataService.getRoundCategories(activeRound.getId());
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Question> getActiveRoundCategoryQuestions(Long categoryId) {
		Round activeRound = domainManager.getActiveRound();
		List<Question> questions = dataService.getRoundCategoryQuestion(activeRound.getId(), categoryId);
		if (questions == null || questions.isEmpty()) {
			throw new ActiveDomainException(Category.class, categoryId);
		}
		return questions;
	}
	
	@RolesAllowed({CONTESTANT_ROLE, ADMIN_ROLE})
	@Override
	public Question makeQuestionActive(QuestionSelectionRequest request) throws NotActiveContestantException, ActiveQuestionException {
		Contestant contestant = principalLocatorService.getPrincipalUser(Contestant.class);
		Contestant activeContestant = domainManager.getActiveContestant();
		Question activeQuestion = domainManager.getActiveQuestion();
		if (!contestant.equals(activeContestant)) {
			log.error("{} != {}", contestant, activeContestant);
			throw new NotActiveContestantException(activeContestant, contestant);
		}
		else if (activeQuestion != null) {
			log.error("Cannot update activeQuestion while its still active: {}", contestant);
			throw new ActiveQuestionException(contestant);
		}
		
		Question question = getActiveRoundCategoryQuestion(request);
		if (question.getAnswerType() != null) {
			throw new ActiveQuestionException(question);
		}
		activeQuestion = question;
		domainManager.setActiveResource(activeQuestion);
		return activeQuestion;
	}
	
	public Round getActiveRound() {
		return domainManager.getActiveRound();
	}
	
	public List<Round> getAllRounds() {
		return dataService.getRounds();
	}
	
	@RolesAllowed({HOST_ROLE, ADMIN_ROLE})
	public Round makeRoundActive(Long roundId) {
		log.debug("making round active...");
		Round round = dataService.getRound(roundId);
		if (round == null) {
			throw new ActiveDomainException(Round.class, roundId);
		}
		log.debug("...making round active completed");
		domainManager.setActiveResource(round);
		return round;
	}
	
	@RolesAllowed({HOST_ROLE, ADMIN_ROLE})
	public void endActiveRound() {
		domainManager.clearActiveResource(Round.class);
		domainManager.clearActiveResource(Question.class, false);
		domainManager.clearActiveResource(Contestant.class, false);
	}
	
	@RolesAllowed(ADMIN_ROLE)
	public void clearActiveQuestion() {
		domainManager.clearActiveResource(Question.class);
	}
	
	@RolesAllowed(ADMIN_ROLE)
	public void clearActiveContestant() {
		domainManager.clearActiveResource(Contestant.class);
	}
	
	public List<Contestant> getContestants() {
		return dataService.getContestants();
	}
	
	@RolesAllowed({HOST_ROLE, ADMIN_ROLE})
	public void makeContestantActive(Contestant contestant) {
		domainManager.setActiveResource(contestant);
	}
	
	public ActiveGameStateReponse getActiveGameState() {
		ActiveGameStateReponse response = null;
		Round activeRound = domainManager.getActiveRound();
		if (activeRound != null) {
			Question activeQuestion = domainManager.getActiveQuestion();
			Contestant activeContestant = domainManager.getActiveContestant();
			response = new ActiveGameStateReponse(activeRound, activeQuestion, activeContestant);
		}
		return response;
	}
	
	protected Question getActiveRoundCategoryQuestion(QuestionSelectionRequest request) {
		List<Question> questions = getActiveRoundCategoryQuestions(request.getCategoryId());
		// TODO: null check questions
		
		return questions.stream().filter(q -> q.getValue().equals(request.getQuestionValue())).findAny().get();
	}
}