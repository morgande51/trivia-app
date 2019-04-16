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

import lombok.extern.java.Log;

@ApplicationScoped
@Log
public class TrivaSecurityRolesProvider implements IdentityStore {
	
	@Inject
	private TrivaSecurityIdentityStore idStore;
	
	@Override
	public Set<ValidationType> validationTypes() {
		return EnumSet.of(PROVIDE_GROUPS);
	}
	
	@Override
	public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
		log.info("Do we even HIT THIS CODE??????");
		Principal authenticatedUser = validationResult.getCallerPrincipal();		
		UserDetails user = idStore.getUserDetails().get(authenticatedUser.getName());
		log.info("getting roles for user: " + user);
		return Stream.of(user.getUserRoles()).collect(Collectors.toSet());
//		return Stream.of(TriviaSecurity.HOST_ROLE, TriviaSecurity.ADMIN_ROLE, TriviaSecurity.CONTESTANT_ROLE).collect(Collectors.toSet());
	}
	
	@Override
	public int priority() {
		return 2;
	}
}