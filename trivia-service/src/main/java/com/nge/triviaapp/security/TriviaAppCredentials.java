package com.nge.triviaapp.security;

import javax.security.enterprise.credential.UsernamePasswordCredential;

public class TriviaAppCredentials extends UsernamePasswordCredential {

	public TriviaAppCredentials(UsernamePasswordCredential credentials) {
		super(credentials.getCaller().toUpperCase(), credentials.getPassword());
	}
}