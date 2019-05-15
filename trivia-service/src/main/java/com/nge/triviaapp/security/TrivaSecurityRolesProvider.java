package com.nge.triviaapp.security;

import static javax.security.enterprise.identitystore.IdentityStore.ValidationType.PROVIDE_GROUPS;

import java.security.Principal;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

import com.nge.triviaapp.domain.Contestant;

import lombok.extern.java.Log;

@ApplicationScoped
@Log
public class TrivaSecurityRolesProvider implements IdentityStore {
	
	@Inject
	private UserService userServicer;
	
	@Override
	public Set<ValidationType> validationTypes() {
		return EnumSet.of(PROVIDE_GROUPS);
	}
	
	@Override
	public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
		Principal authenticatedUser = validationResult.getCallerPrincipal();		
		Contestant user = userServicer.findFromEmail(authenticatedUser.getName());
		log.fine("getting roles for user: " + user);
		return user.getRoles();
//		return Stream.of(TriviaSecurity.HOST_ROLE, TriviaSecurity.ADMIN_ROLE, TriviaSecurity.CONTESTANT_ROLE).collect(Collectors.toSet());
	}
	
	@Override
	public int priority() {
		return 2;
	}
}