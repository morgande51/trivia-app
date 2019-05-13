package com.nge.triviaapp.contestant;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lombok.extern.java.Log;

@Path("/contestant")
@ApplicationScoped
@Log
public class ContestantEndpoint {
	
	@Inject
	private ContestantService buzzardService;
	
	@GET
	@Path("/buzzer")
	@Produces(MediaType.APPLICATION_JSON)
//	@RolesAllowed(TriviaSecurity.CONTESTANT_ROLE)
	public BuzzerAcknowledgmentResponse onBuzzardClick() throws ContestantException {
		return buzzardService.processContestantBuzzard();
	}
	
	@GET
	@Path("/clear")
	@Produces(MediaType.WILDCARD)
	public Response clearBuzzer() {
		buzzardService.clearBuzzer();
		return Response.ok("Success").build();
	}
}