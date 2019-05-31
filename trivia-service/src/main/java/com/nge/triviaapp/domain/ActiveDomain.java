package com.nge.triviaapp.domain;

public interface ActiveDomain {

	public <D> D as();
	
	public ActiveActionLiteral getLiteral();

	public default ActiveActionLiteral getLiteral(ActiveActionType action) {
		ActiveActionLiteral literal = getLiteral();
		literal.setAction(action);
		return literal;
	}
}