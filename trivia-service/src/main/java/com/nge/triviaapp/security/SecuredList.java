package com.nge.triviaapp.security;

import java.util.ArrayList;
import java.util.Collection;

public class SecuredList<E> extends ArrayList<E> {

	public SecuredList(Collection<? extends E> c) {
		super(c);
	}

	private static final long serialVersionUID = 1L;	
}