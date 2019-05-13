package com.nge.triviaapp.security;

import static com.nge.triviaapp.security.TriviaSecurity.*;

import java.util.Arrays;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.security.enterprise.authentication.mechanism.http.AutoApplySession;
import javax.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.nge.triviaapp.domain.Contestant;

@BasicAuthenticationMechanismDefinition(realmName=DOMAIN)
@AutoApplySession
@DeclareRoles({CONTESTANT_ROLE, HOST_ROLE, ADMIN_ROLE})
@RolesAllowed({CONTESTANT_ROLE, HOST_ROLE, ADMIN_ROLE})
@Path("/me")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TriviaSecurityEndpoint {
	
	@Inject
	private TrivaSecurityIdentityStore idProvider;
	
	@Inject
	private PrincipalLocatorService principalService;
	
	@GET
	public Contestant login() {
		return principalService.getPrincipalUser(Contestant.class);
	}
	
	@GET
	@Path("/roles")
	public JsonArray getMyRoles() {
		JsonArray roles = null;
		Contestant contestant = principalService.getPrincipalUser(Contestant.class);
		if (contestant != null) {
			roles = convertRoles(idProvider.getUserDetails().get(contestant.getEmail()).getUserRoles());
		}
		return roles;
	}
	
	protected JsonArray convertRoles(String[] roles) {
		return Json.createArrayBuilder(Arrays.asList(roles)).build();
	}
}