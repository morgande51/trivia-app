package com.nge.triviaapp.host;

import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;

import com.nge.triviaapp.domain.ActiveDomainManager;
import com.nge.triviaapp.domain.Contestant;
import com.nge.triviaapp.domain.Question;
import com.nge.triviaapp.domain.QuestionAnswerType;
import com.nge.triviaapp.domain.Round;
import com.nge.triviaapp.domain.TriviaDataService;
import com.nge.triviaapp.security.TriviaSecurity;

import lombok.extern.slf4j.Slf4j;

@Singleton
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@PermitAll
@Slf4j
public class HostServiceSessionBean implements HostService {
	
	@Inject
	private Event<AnswerRequest> questionAnsweredEvent;
	
	@Resource
	private ManagedExecutorService executorService;
	
	private AnswerRequestCallback answerRequestCallback;
	
	private Future<AcknowlegedAnswerRequest> futureAnswer;
	
	@Inject
	private TriviaDataService dataService;
	
	@Inject
	private ActiveDomainManager domainManager;
	
	@PostConstruct
	public void init() {
		answerRequestCallback = new AnswerRequestCallback();
		futureAnswer = null;
//		futureAnswer = executorService.submit(answerRequestCallback);
	}
	
	@RolesAllowed(TriviaSecurity.HOST_ROLE)
	public void processAnswerRequest(AnswerRequest request) {
		// get the answer type
		QuestionAnswerType answerType = request.getAnswerType();
		
		// set the question answer
		Question question = dataService.merge(domainManager.getActiveQuestion());
		question.setAnswerType(answerType);
		
		// contestant is identifed based on the answer type
		Contestant contestant = null;
		switch (request.getAnswerType()) {
			case CORRECT:
				contestant = dataService.merge(domainManager.getActiveContestant());
				contestant.updateScore(question);
				domainManager.clearActiveResource(Question.class, false);
				break;
				
			case INCORRECT:
				contestant = dataService.merge(domainManager.getActiveContestant());
				contestant.updateScore(question);
				log.warn("be careful here, the activeContestant is going to be set null");
				domainManager.clearActiveResource(Contestant.class);
				break;
				
			default:
				domainManager.clearActiveResource(Question.class, false);
				if (domainManager.getActiveContestant() != null) {
					domainManager.clearActiveResource(Contestant.class);
				}
		}
		
//		final Contestant arcContesant = contestant;
//		answerRequestCallbacks.forEach(arc -> arc.setRequest(new AcknowlegedAnswerRequest(request, arcContesant)));
		log.info("The answerRequestCallback value is being set.  Check the logs here");
		answerRequestCallback.setRequest(new AcknowlegedAnswerRequest(request, contestant));
		questionAnsweredEvent.fire(request);
	}
	
	@Asynchronous
	public Future<AcknowlegedAnswerRequest> getHostAnswer() {
		if (futureAnswer == null) {
			futureAnswer = executorService.submit(answerRequestCallback); 
		}
		return futureAnswer;
	}
	
	public void onActiveContestantChange(@Observes(during=TransactionPhase.AFTER_COMPLETION) Contestant contestant) {
		log.info("Host Service is handling the Active Contestant update/delete");
		resetFuture();
	}
	
	public void onActiveRoundChange(@Observes Round round) {
		log.info("Host Service is handling the Active Round update/delete");
		resetFuture();
	}
	
	public void onActiveQuestionChange(@Observes(during=TransactionPhase.AFTER_COMPLETION)  Question question) {
		log.info("Host Service is handling the Active Question update/delete");
		resetFuture();
	}
	
	protected void resetFuture() {
		log.debug("Is future answer null: {}", futureAnswer == null);
		if (futureAnswer != null) {
			log.debug("Is future answer done: {}", futureAnswer.isDone());
		}
		if (futureAnswer != null && !futureAnswer.isDone()) {
			log.warn("The futureAnswer is running.  Should it be canceled?");
//			futureAnswer.cancel(true);
			answerRequestCallback = new AnswerRequestCallback();
			futureAnswer = null;
		}
		else if (futureAnswer != null) {
			log.info("The futureAnswer is being cleared and the answerRequestCallback is being re-init.");
			answerRequestCallback = new AnswerRequestCallback();
			futureAnswer = null;
		}
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