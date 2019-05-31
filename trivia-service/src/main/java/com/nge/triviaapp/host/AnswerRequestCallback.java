package com.nge.triviaapp.host;

import java.util.concurrent.Callable;

import com.nge.triviaapp.domain.QuestionAnswerType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnswerRequestCallback implements Callable<AcknowlegedAnswerRequest> {
	
	private static final long SLEEP_TIME = 100;
	private AcknowlegedAnswerRequest request;
	private final Object monitor = new Object();
	
	public AnswerRequestCallback() {
		log.info("*********************** someone or something called INIT on the future callback.  how many do we have?????");
	}
	
	public AcknowlegedAnswerRequest call() throws Exception {
		log.info("*********************** a process has executed the callback *****************************");
		synchronized (monitor) {
			monitor.wait();
		}
		log.info("*********************** Callback has a request!.  This callback will complete!!!!!");
		return request;
	}
	
	public synchronized void setRequest(AcknowlegedAnswerRequest request) {
		log.info("*********************** Callback set request is called with: " + request);
		this.request = request;
		synchronized (monitor) {
			monitor.notify();
		}
	}
	
	public boolean hasAnswer() {
		return this.request != null;
	}
	
	public QuestionAnswerType getAnswer() {
		return this.request.getAnswerType();
	}
	
	public AcknowlegedAnswerRequest getRequest() {
		return this.request;
	}

	public boolean hasAnswer(QuestionAnswerType answerType) {
		return (hasAnswer() && (getAnswer() == answerType));
	}
}