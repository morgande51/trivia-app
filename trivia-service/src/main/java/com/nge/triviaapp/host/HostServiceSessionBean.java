package com.nge.triviaapp.host;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import com.nge.triviaapp.domain.ActiveDomain;
import com.nge.triviaapp.domain.Active;
import com.nge.triviaapp.domain.ActiveActionType;
import com.nge.triviaapp.domain.Contestant;
import com.nge.triviaapp.domain.Question;
import com.nge.triviaapp.domain.QuestionAnswerType;
import com.nge.triviaapp.domain.Round;
import com.nge.triviaapp.domain.TriviaDataService;
import com.nge.triviaapp.security.TriviaSecurity;

import lombok.extern.java.Log;

@Singleton
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@PermitAll
@Log
public class HostServiceSessionBean implements HostService {
	
	private Contestant activeContestant;
	
	private Question activeQuestion;
	
	@Inject
	private Event<AnswerRequest> questionAnsweredEvent;
	
	@Resource
	private ManagedExecutorService executorService;
	
	@Inject
	@Any
	private Event<ActiveDomain> activeEvent; 
	
	private AnswerRequestCallback answerRequestCallback;
	
	private Future<AcknowlegedAnswerRequest> futureAnswer;
	
	@Inject
	private TriviaDataService dataService;
	
	@PostConstruct
	public void init() {
		answerRequestCallback = new AnswerRequestCallback();
		futureAnswer = null;
//		futureAnswer = executorService.submit(answerRequestCallback);
	}
	
	@RolesAllowed(TriviaSecurity.HOST_ROLE)
	@Lock
	public void processAnswerRequest(AnswerRequest request) {
		// get the answer type
		QuestionAnswerType answerType = request.getAnswerType();
		
		// set the question answer
		Question question = dataService.merge(activeQuestion);
		question.setAnswerType(answerType);
		
		// contestant is identifed based on the answer type
		Contestant contestant = null;
		switch (answerType) {
			case CORRECT:
				contestant = dataService.merge(activeContestant);
				contestant.updateScore(question);
				this.activeQuestion = null;
				break;
				
			case INCORRECT:
				contestant = dataService.merge(activeContestant);
				contestant.updateScore(question);
				this.activeContestant = null;
				break;
				
			default:
				this.activeContestant = null;
				this.activeQuestion = null;
		}
		
		answerRequestCallback.setRequest(new AcknowlegedAnswerRequest(request, contestant));
		questionAnsweredEvent.fire(request);
	}
	
	public String getActiveQuestionAnswer() {
		return answerRequestCallback.hasAnswer() ? activeQuestion.getAnswer() : null;
	}
	
	@Lock
	public Future<AcknowlegedAnswerRequest> getHostAnswer() {
		if (futureAnswer == null) {
			futureAnswer = executorService.submit(answerRequestCallback); 
		}
		return futureAnswer;
	}
	
	@Lock
	public void onActiveQuestionChange(@Observes @Active(Question.class) Question question) {
		init();
		this.activeQuestion = question;
	}
	
	@Lock
	public void onActiveContestantChange(@Observes @Active(Contestant.class) Contestant contestant) {
		init();
		this.activeContestant = contestant;
	}
	
	@Lock
	public void onActiveRoundChange(@Observes @Active(Round.class) Round round) {
		init();
		this.activeQuestion = null;
	}
	
	@Lock
	public void onActiveRoundEnd(@Observes @Active(value=Round.class, action=ActiveActionType.DELETE) Round round) {
		init();
		this.activeQuestion = null;
		this.activeContestant = null;
	}
	
	@Lock
	public void onActiveHostAnswer(@Observes(during=TransactionPhase.AFTER_SUCCESS) AnswerRequest request) {
		/*
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
		*/
	}
	
	class AnswerRequestCallback implements Callable<AcknowlegedAnswerRequest> {
		
		private static final long SLEEP_TIME = 1000 * 5;
		
		private AcknowlegedAnswerRequest request;
		
		public AcknowlegedAnswerRequest call() throws Exception {
			while (request == null) {
				log.fine("No answer ack from host");
				Thread.sleep(SLEEP_TIME);
			}
			return request;
		}
		
		public synchronized void setRequest(AcknowlegedAnswerRequest request) {
			this.request = request;
		}
		
		public boolean hasAnswer() {
			return this.request != null;
		}
	}
}