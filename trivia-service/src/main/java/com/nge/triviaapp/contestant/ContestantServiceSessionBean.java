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
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;

import com.nge.triviaapp.contestant.ContestantException.Reason;
import com.nge.triviaapp.domain.ActiveDomainManager;
import com.nge.triviaapp.domain.Contestant;
import com.nge.triviaapp.domain.Question;
import com.nge.triviaapp.domain.QuestionAnswerType;
import com.nge.triviaapp.domain.Round;
import com.nge.triviaapp.host.AnswerRequest;
import com.nge.triviaapp.security.PrincipalLocatorService;
import com.nge.triviaapp.security.TriviaSecurity;

import lombok.extern.slf4j.Slf4j;

@Singleton
@PermitAll
@Slf4j
public class ContestantServiceSessionBean implements ContestantService {
	
	private boolean firstContestant;
	
	private Queue<Contestant> buzzedContestants;
	
	private Set<Contestant> previousContestants;
	
	@Inject
	private Event<BuzzerAcknowledgmentResponse> activeBuzzerEvent;
	
	@Inject
	private Event<BuzzerResetRequest> buzzerResetEvent;
	
	@Inject
	private PrincipalLocatorService principalLocatorService;
	
	@Inject
	private ActiveDomainManager domainManager;
	
	@PostConstruct
	public void init() {
		log.info("########################...very key, the Buzzer is being init...########################");
		buzzedContestants = new LinkedList<>();
		previousContestants = new HashSet<>();
		firstContestant = true;
	}
	
	@RolesAllowed(TriviaSecurity.CONTESTANT_ROLE)
	@Lock
	public BuzzerAcknowledgmentResponse processContestantBuzzard() throws ContestantException {
 		// identify the contestant
		Contestant contestant = principalLocatorService.getPrincipalUser(Contestant.class);
		log.info("Contestant[" + contestant.getEmail() + "] has attempted to buzz in...");
		
		// determine if contestant has buzzed in prior
		if (previousContestants.contains(contestant)) {
			throw new ContestantException(contestant, Reason.DUPLICATE);
		}
		else if (!doesActiveQuestionExist()) {
			throw new ContestantException(contestant, Reason.NO_ACTIVE_QUESTION);
		}
		
		// officially recognize the contestant buzz
		log.debug("Constant[" + contestant.getEmail() + "] buzz is recognized.");
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
	
	@Lock
	public void handleAnswerRequest(@Observes(during = TransactionPhase.AFTER_SUCCESS) AnswerRequest request) {
		log.info("Contestant Serivce is handling AnswerRequest event: " + request);
		QuestionAnswerType type = request.getAnswerType();
		switch (type) {
			case CORRECT:
			case NO_ANSWER:
				resetBuzzer(new BuzzerResetRequest(type));
				break;
			
			default:
				if (!getNextContestant()) {
					firstContestant = true;
				}
		}
	}
	
	public void onActiveRoundChange(@Observes Round round) {
		log.info("Contestant Service is handling the Active Round update/delete");
		cleanup();
	}
	
	@Lock
	public void onActiveQuestionChange(@Observes Question question) {
		log.info("Contestant Serivce is handling the Active Question update/delete");
		cleanup();
	}
	
	@RolesAllowed(TriviaSecurity.ADMIN_ROLE)
	@Lock
	public void clearBuzzer() {
		resetBuzzer(new BuzzerResetRequest());
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
		log.info("Recognized contestant[" + contestant.getEmail() + "]");
		previousContestants.add(contestant);
		domainManager.setActiveResource(contestant);
		activeBuzzerEvent.fire(response);
		return response;
	}
	
	protected void resetBuzzer(BuzzerResetRequest request) {
		if (request.isAdminReset()) {
			domainManager.clearActiveResource(Contestant.class);
		}
		buzzerResetEvent.select(request.getLiteral()).fire(request);
		cleanup();
	}
	
	protected final void cleanup() {
		buzzedContestants.clear();
		previousContestants.clear();
		firstContestant = true;
	}
	
	protected boolean doesActiveQuestionExist() {
		return (domainManager.getActiveQuestion() != null);
	}
}