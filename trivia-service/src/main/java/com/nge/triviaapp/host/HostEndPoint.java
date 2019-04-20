package com.nge.triviaapp.host;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

@Path("/host")
@ApplicationScoped
@Produces(MediaType.WILDCARD)
public class HostEndPoint {

	public static final String HOST_REGISTRATION_EVENT = "sse.host.registration";

	@Inject
	private HostService hostService;
	
	@Context
    private Sse sse;
	
	private volatile SseBroadcaster hostBroadcaster;
	
	@PostConstruct
	public void init() {
		hostBroadcaster = sse.newBroadcaster();
	}
	
	@POST
	@Path("/register")
	@Consumes(MediaType.SERVER_SENT_EVENTS)
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public void buzzerRegisration(@Context SseEventSink sinkEvent) {
		sinkEvent.send(sse.newEvent(HOST_REGISTRATION_EVENT));
		hostBroadcaster.register(sinkEvent);
	}
	
	@POST
	@Path("/rounds")
	@Consumes(MediaType.APPLICATION_JSON)
	public void selectRount(ActivateRoundRequest request) {
		hostService.makeRoundActive(request.getRoundId());
	}
	
	@POST
	@Path("/answer")
	@Consumes(MediaType.APPLICATION_JSON)
	public void processContestantAnswer(AnswerRequest request) {
		hostService.processAnswerRequest(request);
	}
}