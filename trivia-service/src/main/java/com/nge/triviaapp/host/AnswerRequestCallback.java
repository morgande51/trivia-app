package com.nge.triviaapp.host;

import java.util.concurrent.Callable;

import com.nge.triviaapp.domain.QuestionAnswerType;

import lombok.extern.java.Log;

@Log
public class AnswerRequestCallback implements Callable<AcknowlegedAnswerRequest> {
	
	private static final long SLEEP_TIME = 100;
	
	private AcknowlegedAnswerRequest request;
	
	public AnswerRequestCallback() {
		log.info("*********************** someone or something called INIT on the future callback.  how many do we have?????");
	}
	
	public AcknowlegedAnswerRequest call() throws Exception {
		System.out.println("*********************** WTF DOES THIS GET CALLED AT ALL *****************************");
		while (request == null) {
			log.finest("No answer ack from host");
			Thread.sleep(SLEEP_TIME);
		}
		log.info("Callback has a request!.  This callback will complete!!!!!");
		return request;
	}
	
	public void setRequest(AcknowlegedAnswerRequest request) {
		log.info("Callback set request is called with: " + request);
		this.request = request;
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