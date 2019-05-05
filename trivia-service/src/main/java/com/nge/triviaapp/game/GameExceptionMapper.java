package com.nge.triviaapp.game;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class GameExceptionMapper implements ExceptionMapper<GameException> {

	@Override
	public Response toResponse(GameException exception) {
		return exception.createResponse();
	}
}