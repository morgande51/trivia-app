package com.nge.triviaapp.host;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import com.nge.triviaapp.domain.Active;
import com.nge.triviaapp.domain.ActiveLocator;
import com.nge.triviaapp.domain.ActiveUpdate;
import com.nge.triviaapp.domain.Contestant;
import com.nge.triviaapp.domain.Question;
import com.nge.triviaapp.domain.Round;
import com.nge.triviaapp.domain.TriviaDataService;
import com.nge.triviaapp.domain.TriviaSecurity;

import lombok.extern.java.Log;

@Singleton
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@RolesAllowed(TriviaSecurity.HOST_ROLE)
@Log
public class HostServiceSessionBean implements HostService {
	
	@Inject
	private TriviaDataService dataService;
	
	@Inject
	private ActiveLocator locator;
	
	@Inject
	private Event<AnswerRequest> questionAnsweredEvent;
	
	@Resource
	private ManagedExecutorService executorService;
	
	@Inject
	@Any
	private Event<Active> activeEvent; 
	
	private AnswerRequestCallback answerRequestCallback;
	
	@Override
	public String getActiveQuestionAnswer() {
		return locator.getActiveQuestion().getAnswer();
	}
	
	@RolesAllowed({TriviaSecurity.HOST_ROLE, TriviaSecurity.ADMIN_ROLE})
	public Round makeRoundActive(long roundId) {
		Round round = dataService.getRound(roundId);
		// TODO: null check round
		
		activeEvent.select(round.getLiteral()).fire(round);
		return round;
	}
	
	@Lock(LockType.WRITE)
	public void processAnswerRequest(AnswerRequest request) {	
		Question question = locator.getActiveQuestion();
		Contestant activeContestant = locator.getActiveContestant();
		switch (request.getAnswerType()) {
			case CORRECT:
				activeContestant.updateScore(question, true);
				question.setAnsweredBy(activeContestant);
//				resetTimer();
				break;
				
			case INCORRECT:
				activeContestant.updateScore(question, false);
				clearActiveContestant();
				break;
				
			default:
				clearActiveContestant();
		}
		
		answerRequestCallback = new AnswerRequestCallback(request);
		questionAnsweredEvent.fire(request);
	}
	
	@PermitAll
	@Override
	public Future<AcknowlegedAnswerRequest> getHostAnswer() {
		return executorService.submit(answerRequestCallback);
	}
	
	protected void clearActiveContestant() {
		AnnotationLiteral<ActiveUpdate> literal = locator.getActiveContestant().getLiteral();
		activeEvent.select(literal).fire(null);
		resetTimer();
	}
	
	// TODO: write timer
	protected void resetTimer() {}
	
	class AnswerRequestCallback implements Callable<AcknowlegedAnswerRequest> {
		
		private static final long SLEEP_TIME = 1000 * 5;
		
		private AnswerRequest request;
		
		protected AnswerRequestCallback(AnswerRequest request) {
			this.request = request;
		}
		
		@Override
		public AcknowlegedAnswerRequest call() throws Exception {
			while (request == null) {
				log.info("No answer ack from host");
				Thread.sleep(SLEEP_TIME);
			}
			return new AcknowlegedAnswerRequest(request, locator.getActiveContestant());
		}
	}
}