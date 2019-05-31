package com.nge.triviaapp.contestant;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/contestant")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ContestantEndpoint {
	
	@Inject
	private ContestantService buzzardService;
	
	@GET
	@Path("/buzzer")
	public BuzzerAcknowledgmentResponse onBuzzardClick() throws ContestantException {
		return buzzardService.processContestantBuzzard();
	}
	
	@DELETE
	@Path("/buzzer")
	@Produces(MediaType.WILDCARD)
	public void clearBuzzer() {
		buzzardService.clearBuzzer();
	}
}