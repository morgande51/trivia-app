package com.nge.triviaapp.contestant;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.nge.triviaapp.contestant.ContestantException.Reason;
import com.nge.triviaapp.domain.Active;
import com.nge.triviaapp.domain.ActiveActionType;
import com.nge.triviaapp.domain.Contestant;
import com.nge.triviaapp.domain.Question;
import com.nge.triviaapp.domain.QuestionAnswerType;
import com.nge.triviaapp.domain.Round;
import com.nge.triviaapp.host.AnswerRequest;
import com.nge.triviaapp.security.PrincipalLocatorService;
import com.nge.triviaapp.security.TriviaSecurity;

import lombok.extern.java.Log;

@Singleton
@Startup
@RolesAllowed(TriviaSecurity.CONTESTANT_ROLE)
@Log
public class ContestantServiceSessionBean implements ContestantService {
	
	private boolean activeQuestionExist;
	
	private boolean firstContestant;
	
	private Queue<Contestant> buzzedContestants;
	
	private Set<Contestant> previousContestants;
	
	@Inject
	private Event<BuzzerAcknowledgmentResponse> activeBuzzerEvent;
	
	@Inject
	private Event<BuzzerResetRequest> buzzerResetEvent;
	
	@Inject
	private PrincipalLocatorService principalLocatorService;
	
	@PostConstruct
	public void init() {
		log.info("########################...very key, the Buzzer is being init...########################");
		buzzedContestants = new LinkedList<>();
		previousContestants = new HashSet<>();
		activeQuestionExist = false;
		firstContestant = true;
	}
	
	@Lock
	public BuzzerAcknowledgmentResponse processContestantBuzzard() throws ContestantException {
 		// identify the contestant
		Contestant contestant = principalLocatorService.getPrincipalUser(Contestant.class);
		log.info("Contestant[" + contestant + "] has attempted to buzz in...");
		
		// determine if contestant has buzzed in prior
		if (previousContestants.contains(contestant)) {
			throw new ContestantException(contestant, Reason.DUPLICATE);
		}
		else if (!activeQuestionExist) {
			throw new ContestantException(contestant, Reason.NO_ACTIVE_QUESTION);
		}
		
		// officially recognize the contestant buzz
		log.info("Constant[" + contestant + "] buzz is recognized.");
		buzzedContestants.add(contestant);
		
		// notify host contestant is the first to buzz in
		BuzzerAcknowledgmentResponse response;
		if (firstContestant) {
			response = recognizeBuzzedContestant();
			firstContestant = false;
		}
		else {
			response = new BuzzerAcknowledgmentResponse();
		}
		
		return response;
	}
	
	@PermitAll
	@Lock
	public void handleAnswerRequest(@Observes AnswerRequest request) {
		log.info("Contestant Serivce is handling AnswerRequest event: " + request);
		QuestionAnswerType type = request.getAnswerType();
		switch (type) {
			case CORRECT:
			case NO_ANSWER:
				resetBuzzer(type);
				break;
			
			default:
				if (!getNextContestant()) {
					firstContestant = true;
				}
		}
	}
	
	@PermitAll
	@Lock
	public void handleRoundEndEvent(@Observes @Active(value=Round.class, action=ActiveActionType.DELETE) Round round) {
		log.info("Contestant Serivce is handling the end round event");
		cleanup();
	}
	
	@PermitAll
	@Lock
	public void handleRoundUpdateEvent(@Observes @Active(Round.class) Round round) {
		log.info("Contestant Serivce is handling the round update event");
		cleanup();
	}
	
	@PermitAll
	public void handleActiveQuestionEvent(@Observes @Active(Question.class) Question question) {
		log.info("Contestant Serivce is handling Question Selected event");
		activeQuestionExist = true;
	}
	
	@RolesAllowed(TriviaSecurity.ADMIN_ROLE)
	@Lock
	public void clearBuzzer() {
		resetBuzzer(QuestionAnswerType.NO_ANSWER);
	}
	
	protected boolean getNextContestant() {
		Contestant contestant = buzzedContestants.peek();
		boolean exist = (contestant != null);
		if (exist) {
			recognizeBuzzedContestant();
		}
		return exist;	
	}
	
	protected BuzzerAcknowledgmentResponse recognizeBuzzedContestant() {
		Contestant contestant = buzzedContestants.poll();
		BuzzerAcknowledgmentResponse response = new BuzzerAcknowledgmentResponse(contestant);
		log.info("Recognizeing contestant[" + contestant + "]");
		previousContestants.add(contestant);
		activeBuzzerEvent.fire(response);
		return response;
	}
	
	protected void resetBuzzer(QuestionAnswerType type) {
		BuzzerResetRequest request = new BuzzerResetRequest(type);
		buzzerResetEvent.fire(request);
		cleanup();
	}
	
	protected final void cleanup() {
		buzzedContestants.clear();
		previousContestants.clear();
		activeQuestionExist = false;
		firstContestant = true;
	}
}