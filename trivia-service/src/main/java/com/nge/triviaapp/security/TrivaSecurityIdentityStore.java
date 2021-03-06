package com.nge.triviaapp.security;

import java.util.EnumSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

import com.nge.triviaapp.domain.Contestant;

import lombok.extern.java.Log;

@ApplicationScoped
@Log
public class TrivaSecurityIdentityStore implements IdentityStore {
	
	@Inject
	private UserService userService;

	public CredentialValidationResult validate(UsernamePasswordCredential credentials) {
		log.fine("target user: " + credentials.getCaller());
		Contestant contestant =  userService.findFromEmail(credentials.getCaller());
		log.fine("Found the following user: " + contestant);
		
		CredentialValidationResult result;
		if (contestant != null && contestant.validate(credentials.getPasswordAsString())) {
			log.fine("found user...adding to security context: " + contestant);
			result = new CredentialValidationResult(contestant.getEmail());
        }
		else {
			result = CredentialValidationResult.INVALID_RESULT;
		}
		
		log.fine("authentication results status: " + result.getStatus());
		return result;
	}
	
	@Override
	public Set<ValidationType> validationTypes() {
		return EnumSet.of(ValidationType.VALIDATE);
	}
	
	@Override
	public int priority() {
		return 1;
	}
}