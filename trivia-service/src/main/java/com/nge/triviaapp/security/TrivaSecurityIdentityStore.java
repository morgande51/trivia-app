package com.nge.triviaapp.security;

import java.io.InputStream;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

import com.nge.triviaapp.service.ContestantService;
import com.nge.triviaapp.service.category.domain.Contestant;

import lombok.extern.java.Log;

@ApplicationScoped
@Log
public class TrivaSecurityIdentityStore implements IdentityStore {
	
	private static final String USERS_LIST = "users.json";
	
	private Map<String, UserDetails> users;
	
	@Inject
	private ContestantService contestantService;
	
	@PostConstruct
	public void init() {
		log.info("CDI init commencing...");
		InputStream userListStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(USERS_LIST);
		JsonReader usersReader = Json.createReader(userListStream);
		JsonObject jsonObj = usersReader.readObject();
		users = jsonObj.getJsonArray("users").stream()
				.map(userObj -> new UserDetails(userObj.asJsonObject()))
				.collect(Collectors.toMap(UserDetails::getUserEmail, u -> u));
		log.info("CDI init completed. Total users: " + users.size());
	}

	public CredentialValidationResult validate(UsernamePasswordCredential credentials) {
		log.info("ContestantService injected: "  + (contestantService != null));
		UserDetails user = users.get(credentials.getCaller());
		log.info("User exist: " + (user != null));
		
		CredentialValidationResult result;
		if (user != null && credentials.compareTo(user.getUserEmail(), user.getUserPwd())) {
			Contestant contestant = contestantService.findFromEmail(user.getUserEmail());
			if (contestant == null) {
				contestant = contestantService.createFromUserDetails(user);
				user.setContestant(contestant);
			}
			
			log.info("found user...adding to security context: " + contestant);
			result = new CredentialValidationResult(contestant.getEmail());
        }
		else {
			result = CredentialValidationResult.INVALID_RESULT;
		}
		
		log.info("authentication results status: " + result.getStatus());
		return result;
	}
	
	@Override
	public Set<ValidationType> validationTypes() {
		return EnumSet.of(ValidationType.VALIDATE);
	}
	
	protected Set<String> getUserGroups(UserDetails user) {
//		return Stream.of(user.getUserRoles()).collect(Collectors.toSet());
		return Stream.of("host", "admin", "contestant").collect(Collectors.toSet());
	}
	
	@Override
	public int priority() {
		return 1;
	}
	
	public Map<String, UserDetails> getUserDetails() {
		return users;
	}
}