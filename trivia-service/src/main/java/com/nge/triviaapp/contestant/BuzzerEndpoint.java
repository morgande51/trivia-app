package com.nge.triviaapp.contestant;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import com.nge.triviaapp.ConverterOf;
import com.nge.triviaapp.domain.Contestant;
import com.nge.triviaapp.game.GameService;
import com.nge.triviaapp.game.QuestionSelectionRequest;
import com.nge.triviaapp.host.AcknowlegedAnswerRequest;
import com.nge.triviaapp.host.AnswerRequest;
import com.nge.triviaapp.host.HostService;

import lombok.extern.java.Log;

@Log
@Path("/buzzer")
@ApplicationScoped
public class BuzzerEndpoint {
	
	public static final String BUZZER_REGISTRATION_EVENT = "sse.buzzer.registration";
	public static final String BUZZER_ACTIVE_CONTESTANT_EVENT = "sse.buzzer.contestant.active";
	public static final String BUZZER_CLEAR_EVENT = "sse.buzzer.clear";
	private static final long CONFIRM_ANSWER_WAIT_TIME = 60;
	
	@Inject
	private GameService gameService;
	
	@Inject
	private HostService hostService;
	
	@Inject
	private BuzzerService buzzardService;
	
	@Inject
	@ConverterOf(BuzzerResetRequest.class)
	private Function<BuzzerResetRequest, String> buzzerRequestConverter;
	
	@Inject
	@ConverterOf(Contestant.class)
	private Function<Contestant, String> contestantConverter;
	
	@Context
    private Sse sse;
	
	private volatile SseBroadcaster buzzardBroadcaster;
	
	@PostConstruct
	public void init() {
		buzzardBroadcaster = sse.newBroadcaster();
	}
	
	@POST
	@Path("/register")
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public void buzzerRegisration(@Context SseEventSink sinkEvent) {
		sinkEvent.send(sse.newEvent(BUZZER_REGISTRATION_EVENT));
		buzzardBroadcaster.register(sinkEvent);
	}
	
	// TODO: needs qualifier
	public void sendActiveBuzzerNotice(@Observes Contestant contestant) {
		buzzardBroadcaster.broadcast(sse.newEvent(BUZZER_ACTIVE_CONTESTANT_EVENT, contestantConverter.apply(contestant)));
	}
	
	public void handleBuzzerReset(@Observes BuzzerResetRequest request) {
		buzzardBroadcaster.broadcast(sse.newEvent(BUZZER_CLEAR_EVENT, buzzerRequestConverter.apply(request)));
	}
	
	@POST
	@Path("/selection")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.WILDCARD)
	public void selectQuestion(QuestionSelectionRequest request) {
		gameService.makeQuestionActive(request);
	}
	
	@GET
	@Path("/confirm")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public void confirmAnswer(@Suspended final AsyncResponse response) {
		Future<AcknowlegedAnswerRequest> answerFuture = hostService.getHostAnswer();
		try {AnswerRequest answer = answerFuture.get(CONFIRM_ANSWER_WAIT_TIME, TimeUnit.SECONDS);
			response.resume(answer);
		}
		catch (TimeoutException e) {
			response.cancel();
		}
		catch (ExecutionException | InterruptedException e) {
			// TODO throw as runtime exception
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BuzzerAcknowledgmentResponse onBuzzardClick() {		
		return buzzardService.processContestantBuzzard();
	}
	
	@GET
	@Path("/clear")
	@Produces(MediaType.WILDCARD)
	public Response clearBuzzer() {
		buzzardService.clearBuzzer();
		return Response.ok("Success").build();
	}
}