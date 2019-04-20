package com.nge.triviaapp.domain;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.nge.triviaapp.security.TrivaSecurityIdentityStore;
import com.nge.triviaapp.security.UserDetails;

@BasicAuthenticationMechanismDefinition(realmName="TriviaApp")
@DeclareRoles({"host", "admin", "contestant"})
@Path("/me")
@ApplicationScoped
public class TriviaSecurityEndpoint {
	
	@Inject
	private TrivaSecurityIdentityStore idProvider;
	
	@Inject
	private SecurityContext securityContext;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({"host", "admin", "contestant"})
	public UserDetails login() {
		String email = securityContext.getCallerPrincipal().getName();
		return idProvider.getUserDetails().get(email);
	}
	
}