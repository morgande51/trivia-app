package com.nge.triviaapp.domain;

import javax.enterprise.util.AnnotationLiteral;

public abstract class ActiveActionLiteral extends AnnotationLiteral<Active> implements Active {
	
	private ActiveActionType type;
	
	public ActiveActionLiteral() {
		this(ActiveActionType.UPDATE);
	}
	
	protected ActiveActionLiteral(ActiveActionType type) {
		this.type = type;
	}

	@Override
	public ActiveActionType action() {
		return type;
	}
	
	public void setAction(ActiveActionType type) {
		this.type = type;
	}
	
	private static final long serialVersionUID = 1L;
}