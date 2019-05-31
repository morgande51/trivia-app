package com.nge.triviaapp.game;

import java.util.List;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import com.nge.triviaapp.ConverterOf;
import com.nge.triviaapp.contestant.BuzzerResetRequest;
import com.nge.triviaapp.domain.Active;
import com.nge.triviaapp.domain.ActiveActionType;
import com.nge.triviaapp.domain.Category;
import com.nge.triviaapp.domain.Contestant;
import com.nge.triviaapp.domain.Question;
import com.nge.triviaapp.domain.Round;

import lombok.extern.slf4j.Slf4j;

@Path("/game")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Slf4j
public class GameEndPoint {
	
	public static final String BUZZER_REGISTRATION_EVENT = "sse.buzzer.registration";
	public static final String BUZZER_ACTIVE_CONTESTANT_EVENT = "sse.buzzer.contestant.active";
	public static final String BUZZER_CLEAR_EVENT = "sse.buzzer.clear";
	public static final String ACTIVE_ROUND_EVENT = "sse.host.round.active";
	public static final String ROUND_END_EVENT = "sse.host.round.end";
	public static final String ACTIVE_QUESTION_EVENT = "sse.contestant.question.active";
	public static final String ACTIVE_QUESTION_CLEAR_EVENT = "sse.contestant.question.clear";
	
	@Inject
	private GameService gameService;
	
	@Inject
	@ConverterOf(BuzzerResetRequest.class)
	private Function<BuzzerResetRequest, String> buzzerRequestConverter;
	
	@Inject
	@ConverterOf(Contestant.class)
	private Function<Contestant, String> contestantConverter;
	
	@Inject
	@ConverterOf(Round.class)
	private Function<Round, String> roundConverter;
	
	@Inject
	@ConverterOf(Question.class)
	private Function<Question, String> questionConverter;
	
	private volatile SseBroadcaster buzzardBroadcaster;
	
	@Context
    private Sse sse;
	
	@PostConstruct
	public void init() {
		buzzardBroadcaster = sse.newBroadcaster();
	}
	
	@GET
	@Path("/notifications")
	@Produces(MediaType.SERVER_SENT_EVENTS)
	@Consumes(MediaType.SERVER_SENT_EVENTS)
	public void buzzerRegisration(@Context SseEventSink sinkEvent) {
		sinkEvent.send(sse.newEvent(BUZZER_REGISTRATION_EVENT));
		buzzardBroadcaster.register(sinkEvent);
	}
	
	public void handleBuzzerNotice(@Observes @Active(Contestant.class) Contestant contestant) {
		log.info("Sending broadcast notification[" + BUZZER_ACTIVE_CONTESTANT_EVENT + "] with:" + contestant);
		buzzardBroadcaster.broadcast(sse.newEvent(BUZZER_ACTIVE_CONTESTANT_EVENT, contestantConverter.apply(contestant)));
	}
	
	public void handleBuzzerReset(@Observes(during=TransactionPhase.AFTER_SUCCESS) BuzzerResetRequest request) {
		log.info("Sending broadcast notification[" + BUZZER_CLEAR_EVENT + "] with:" + request);
		buzzardBroadcaster.broadcast(sse.newEvent(BUZZER_CLEAR_EVENT, buzzerRequestConverter.apply(request)));
	}
	
	public void handleActiveQuestion(@Observes @Active(Question.class) Question question) {
		log.info("Sending broadcase notification[" + ACTIVE_QUESTION_EVENT + "] with: " + question);
		buzzardBroadcaster.broadcast(sse.newEvent(ACTIVE_QUESTION_EVENT, questionConverter.apply(question)));
	}
	
	public void handleActiveQuestionClear(@Observes @Active(value=Question.class, action=ActiveActionType.DELETE) Question question) {
		log.info("Sending broadcase notification[" + ACTIVE_QUESTION_CLEAR_EVENT + "] with: " + question);
		buzzardBroadcaster.broadcast(sse.newEvent(ACTIVE_QUESTION_CLEAR_EVENT, questionConverter.apply(question)));
	}
	
	public void handleActiveRoundEvent(@Observes @Active(Round.class) Round round) {
		log.info("Sending broadcast notification[" + ACTIVE_ROUND_EVENT + "] with:" + round);
		buzzardBroadcaster.broadcast(sse.newEvent(ACTIVE_ROUND_EVENT, roundConverter.apply(round)));
	}
	
	public void handleActiveRoundEndEvent(@Observes @Active(value=Round.class, action=ActiveActionType.DELETE) Round round) {
		log.info("Sending broadcast notification[" + ROUND_END_EVENT + "] with:" + round);
		buzzardBroadcaster.broadcast(sse.newEvent(ROUND_END_EVENT, "NO_DATA"));
	}
	
	@GET
	@Path("/rounds")
	public List<Round> getAllRounds() {
		return gameService.getAllRounds();
	}
	
	@GET
	@Path("/active/round")
	public Round getActiveRound() throws NoActiveGameException {
		Round round = gameService.getActiveRound();
		if (round == null) {
			throw new NoActiveGameException();
		}
		return round;
	}
	
	@DELETE
	@Path("/active/round")
	public void endActiveRound() {
		gameService.endActiveRound();
	}
	
	@POST
	@Path("/active/round")
	public Round selectRound(ActivateRoundRequest request) {
		return gameService.makeRoundActive(request.getRoundId());
	}
	
	@GET
	@Path("/active/round/categories")
	@Consumes(MediaType.WILDCARD)
	public List<Category> getActiveRoundCategories() {
		return gameService.getActiveRoundCategories();
	}
	
	@GET
	@Path("/active/round/categories/{categoryId}/questions")
	public Question[] getActiveRoundCategoryQuestions(@PathParam("categoryId") Long categoryId) {
		return gameService.getActiveRoundCategoryQuestions(categoryId).stream().toArray(i -> new Question[i]);
	}
	
	@POST
	@Path("/active/round/categories/{categoryId}/questions")
	public Question setActiveQuestion(@PathParam("categoryId") Long categoryId, QuestionSelectionRequest request) throws GameException {
		request.setCategoryId(categoryId);
		Question question = gameService.makeQuestionActive(request);
		return question;
	}
	
	@DELETE
	@Path("/active/round/question")
	public void cleaActiveQuestion() {
		gameService.clearActiveQuestion();
	}
	
	@GET
	@Path("/contestants")
	public List<Contestant> getContestants() {
		return gameService.getContestants();
	}
	
	@POST
	@Path("/active/round/contestant")
	public void setActiveContestant(Contestant contestant) {
		gameService.makeContestantActive(contestant);
	}
	
	@DELETE
	@Path("/active/round/contestant")
	public void clearActiveContestant() {
		gameService.clearActiveContestant();
	}
	
	@GET
	@Path("/active")
	public ActiveGameStateReponse getGameStatus() throws NoActiveGameException {
		ActiveGameStateReponse gameState = gameService.getActiveGameState();
		if (gameState == null) {
			throw new NoActiveGameException();
		}
		return gameState;
	}
}