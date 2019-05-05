package com.nge.triviaapp.security;

import static com.nge.triviaapp.security.TriviaSecurity.*;

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

import com.nge.triviaapp.domain.Contestant;

@BasicAuthenticationMechanismDefinition(realmName=DOMAIN)
@DeclareRoles({CONTESTANT_ROLE, HOST_ROLE, ADMIN_ROLE})
@Path("/me")
@ApplicationScoped
public class TriviaSecurityEndpoint {
	
	@Inject
	private TrivaSecurityIdentityStore idProvider;
	
	@Inject
	private PrincipalLocatorService principalService;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({CONTESTANT_ROLE, HOST_ROLE, ADMIN_ROLE})
	public UserDetails login() {
		String email = principalService.getPrincipalUser(Contestant.class).getEmail();
		return idProvider.getUserDetails().get(email);
	}
}