package com.nge.triviaapp.domain;

import javax.enterprise.util.AnnotationLiteral;

public interface ActiveDomain {

	public <D> D as();
	
	public AnnotationLiteral<Active> getLiteral();
	
}