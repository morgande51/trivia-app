package com.nge.triviaapp.host;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

import lombok.extern.java.Log;

@Path("/host")
@ApplicationScoped
@Produces(MediaType.WILDCARD)
@Log
public class HostEndPoint {
	
	private static final long CONFIRM_ANSWER_WAIT_TIME = 60;

	@Inject
	private HostService hostService;
	
	@POST
	@Path("/answer")
	@Consumes(MediaType.APPLICATION_JSON)
	public void processContestantAnswer(AnswerRequest request) {
		hostService.processAnswerRequest(request);
	}
	
	@GET
	@Path("/answer")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public void confirmAnswer(@Suspended final AsyncResponse response) {
		Future<AcknowlegedAnswerRequest> answerFuture = hostService.getHostAnswer();
		try {
			AcknowlegedAnswerRequest answer = answerFuture.get(CONFIRM_ANSWER_WAIT_TIME, TimeUnit.SECONDS);
			response.resume(answer);
		}
		catch (TimeoutException e) {
			log.severe("answer future has timed out and the host never sent a response");
			response.cancel();
		}
		catch (CancellationException e) {
			log.severe("***** answer future was canceled. maybe call this again??? *****");
//			response.cancel();
			// TODO: hack.  lets revist this ASAP
			log.warning("This is a hack.  lets revist this ASAP");
			confirmAnswer(response);
		}
		catch (ExecutionException | InterruptedException e) {
			// TODO throw as runtime exception
			e.printStackTrace();
		}
	}
}