package com.nge.triviaapp.game;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public abstract class GameException extends Exception {
	
	private static final String ERROR_MSG = "errorMsg";
	
	protected Status responseStatus;
	
	public GameException() {
		super();
		responseStatus = Status.BAD_REQUEST;
	}
	
	public GameException(String message) {
		super(message);
		responseStatus = Status.BAD_REQUEST;
	}

	public GameException(String message, Throwable cause) {
		super(message, cause);
		responseStatus = Status.BAD_REQUEST;
	}
	
	public Response createResponse() {
		return Response.status(responseStatus).entity(createResponseBody()).build();
	}
	
	protected Object createResponseBody() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		return builder.add(ERROR_MSG, getMessage()).build();
	}

	private static final long serialVersionUID = 1L;
}