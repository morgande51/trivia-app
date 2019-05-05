package com.nge.triviaapp.game;

import javax.ws.rs.core.Response.Status;

public class NoActiveRoundException extends GameException {
	
	public NoActiveRoundException() {
		super("No Active Round Selected");
		responseStatus = Status.NOT_FOUND;
	}

	private static final long serialVersionUID = 1L;
}