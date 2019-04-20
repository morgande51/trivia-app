package com.nge.triviaapp.contestant;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.nge.triviaapp.domain.Contestant;
import com.nge.triviaapp.domain.TriviaSecurity;
import com.nge.triviaapp.host.AnswerRequest;
import com.nge.triviaapp.host.AnswerType;
import com.nge.triviaapp.security.PrincipalLocatorService;

import lombok.extern.java.Log;

@Log
@Singleton
public class ContestantServiceSessionBean implements ContestantService {
	
	private Queue<Contestant> buzzedContestants;
	private Set<Contestant> previousContestants;
	
	@Inject
	private Event<Contestant> activeContestantEvent;	// TODO: needs qualifier
	
	@Inject
	private Event<BuzzerResetRequest> buzzerResetEvent;
	
	@Inject
	private PrincipalLocatorService principalLocatorService;
	
	@PostConstruct
	public void init() {
		log.info("########################...very key, the Buzzer is being init...########################");
		buzzedContestants = new LinkedList<>();
		previousContestants = new HashSet<>();
	}
	
	@Lock(LockType.WRITE)
	@RolesAllowed(TriviaSecurity.CONTESTANT_ROLE)
	public BuzzerAcknowledgmentResponse processContestantBuzzard() {
		// identify the contestant
		Contestant contestant = principalLocatorService.getPrincipalUser(Contestant.class);
		log.info("Contestant[" + contestant + "] has attempted to buzz in...");
		
		// determine if contestant has buzzed in prior
		if (previousContestants.contains(contestant)) {
			String errorMsg = "Contestant " + contestant.getEmail() + " clicked more than once!!";
			log.severe(errorMsg);
			throw new RuntimeException(errorMsg);	// TODO: real exception here
		}
		
		// officially recognize the contestant buzz
		log.info("Constant[" + contestant + "] buzz is recognized.");
		boolean isContestantFirst = buzzedContestants.isEmpty();
		buzzedContestants.add(contestant);
		
		// notify host contestant is the first to buzz in
		if (isContestantFirst) {
			recognize(contestant);
		}
		
		return new BuzzerAcknowledgmentResponse(isContestantFirst);
	}

	@RolesAllowed(TriviaSecurity.ADMIN_ROLE)
	public void handleAnswerRequest(@Observes AnswerRequest request) {
		switch (request.getAnswerType()) {
			case CORRECT:
			case NO_ANSWER:
				resetBuzzer(request.getAnswerType());
				break;
				
			default:
				getNextContestant();
		}
	}
	
	@RolesAllowed(TriviaSecurity.ADMIN_ROLE)
	public void clearBuzzer() {
		resetBuzzer(AnswerType.NO_ANSWER);
	}
	
	protected void getNextContestant() {
		Contestant contestant = buzzedContestants.poll();
		if (contestant != null) {
			recognize(contestant);
		}
	}
	
	protected void recognize(Contestant contestant) {
		log.info("Recognizeing contestant[" + contestant + "]");
		previousContestants.add(contestant);
		activeContestantEvent.fire(contestant);
	}
	
	protected void resetBuzzer(AnswerType type) {
		buzzedContestants.clear();
		previousContestants.clear();
		BuzzerResetRequest request = new BuzzerResetRequest(type);
		buzzerResetEvent.fire(request);
	}
}