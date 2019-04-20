package com.nge.triviaapp.domain;

import javax.enterprise.util.AnnotationLiteral;

public interface Active {

	public <D> D as();
	
	public AnnotationLiteral<ActiveUpdate> getLiteral();
}