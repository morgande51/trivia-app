package com.nge.triviaapp.host;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Asynchronous;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import com.nge.triviaapp.domain.ActiveDomain;
import com.nge.triviaapp.contestant.BuzzerReset;
import com.nge.triviaapp.contestant.BuzzerResetRequest;
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

		
//		final Contestant arcContesant = contestant;
//		answerRequestCallbacks.forEach(arc -> arc.setRequest(new AcknowlegedAnswerRequest(request, arcContesant)));
		answerRequestCallback.setRequest(new AcknowlegedAnswerRequest(request, contestant));
		questionAnsweredEvent.fire(request);
	}
	
	/*
	public String getActiveQuestionAnswer() {
		return answerRequestCallback.hasAnswer() ? activeQuestion.getAnswer() : null;
	}
	*/
	
	@Lock
	public Future<AcknowlegedAnswerRequest> getHostAnswer() {
		if (futureAnswer == null) {
			futureAnswer = executorService.submit(answerRequestCallback); 
		}
		return futureAnswer;
	}
	
	@Lock
	public void onActiveQuestionChange(@Observes @Active(Question.class) Question question) {
		log.info("Host Service is handling the Action Question");
		resetFuture(true);
		this.activeQuestion = question;
	}
	
	@Lock
	public void onActiveQuestionClear(@Observes @Active(value=Question.class, action=ActiveActionType.DELETE) Question question) {
		log.info("Host Service is handling the Action Question(DELETE)");
		resetFuture(false);
		this.activeQuestion = null;
	}
	
	@Lock
	public void onActiveContestantChange(@Observes @Active(Contestant.class) Contestant contestant) {
		log.info("Host Service is handling the Action Contestant");
		this.activeContestant = contestant;
	}
	
	@Lock
	public void onActiveRoundChange(@Observes @Active(Round.class) Round round) {
		log.info("Host Service is handling the Action Round");
		resetFuture(false);
		this.activeQuestion = null;
	}
	
	@Lock
	public void onActiveRoundEnd(@Observes @Active(value=Round.class, action=ActiveActionType.DELETE) Round round) {
		log.info("Host Service is handling the Action Round(DELETE)");
		resetFuture(false);
		this.activeQuestion = null;
		this.activeContestant = null;
	}
	
	@Lock
	public void onBuzzerReset(@Observes @BuzzerReset(admin=true) BuzzerResetRequest buzzerReset) {
		log.info("Host Service is handling the Buzzer Reset Request");
		resetFuture(false);
		this.activeContestant = null;
	}
	
	protected void resetFuture(boolean recreate) {
//		if (futureAnswer != null) {
//			log.info("The future cancel request is returning: " + futureAnswer.cancel(true));
//		}
//		if (recreate) {
//			answerRequestCallback = new AnswerRequestCallback();
//			futureAnswer = executorService.submit(answerRequestCallback);
//		}
		log.info("the callback is being re-init.  What happens to the future???");
		answerRequestCallback = new AnswerRequestCallback();
		futureAnswer = null;
	}
	
	/*
	@Lock
	public void onActiveHostAnswer(@Observes(during=TransactionPhase.AFTER_SUCCESS) AnswerRequest request) {
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
	 */
}