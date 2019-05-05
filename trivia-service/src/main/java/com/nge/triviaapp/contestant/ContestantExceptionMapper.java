package com.nge.triviaapp.contestant;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.nge.triviaapp.contestant.ContestantException.Reason;

import lombok.extern.java.Log;

@Provider
@Log
public class ContestantExceptionMapper implements ExceptionMapper<ContestantException> {
	
	private static final String DUPLICATE_ERROR_MSG = "Contestant has already buzzed in for the active question";
	private static final String NO_ACTIVE_QUESTION_ERROR_MSG = "No active question yet";
	
	public Response toResponse(ContestantException exception) {
		
		String msg = getErrorMsg(exception.getReason());
		log.severe(exception.getContestant() + " " + msg);
		return Response.status(Status.BAD_REQUEST).entity(msg).build();
	}
	
	protected String getErrorMsg(Reason reason) {
		String error;
		switch (reason) {
			case DUPLICATE:
				error = DUPLICATE_ERROR_MSG;
				break;
				
			default:
				error = NO_ACTIVE_QUESTION_ERROR_MSG;
		}
		
		return error;
	}
}