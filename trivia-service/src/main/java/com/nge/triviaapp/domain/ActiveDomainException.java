package com.nge.triviaapp.domain;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class ActiveDomainException extends RuntimeException {
	
	private Class<?> domainType;
	
	private Serializable key;
	
	public ActiveDomainException(Class<?> domainType, Serializable key) {
		super("Unknown key[" + key + "] for domain: " + domainType.getSimpleName());
		this.domainType = domainType;
		this.key = key;
	}
	
	private static final long serialVersionUID = 1L;
}