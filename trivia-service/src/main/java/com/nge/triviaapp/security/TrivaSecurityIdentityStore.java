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
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

import com.nge.triviaapp.domain.Contestant;

import lombok.extern.java.Log;

@ApplicationScoped
@Log
public class TrivaSecurityIdentityStore implements IdentityStore {
	
	private static final String USERS_LIST_PROPERTY = "users.json";
	private static final String CONTESTANT_PROPERTY = "users";
	
	@Inject
	private UserService userService;
	
	@PostConstruct
	public void init() {
		String usersConfig = getUserConfigFile();
		log.info("Attempting to locate users: " + usersConfig);
		InputStream userListStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(usersConfig);
		JsonReader usersReader = Json.createReader(userListStream);
		JsonObject jsonObj = usersReader.readObject();
		Set<Contestant> users = jsonObj.getJsonArray(CONTESTANT_PROPERTY)
			.stream()
			.map(userObj -> Contestant.createFrom(userObj.asJsonObject()))
			.collect(Collectors.toSet());
		userService.addUsers(users);
	}

	public CredentialValidationResult validate(UsernamePasswordCredential credentials) {
		log.info("target user: " + credentials.getCaller());
		Contestant contestant =  userService.findFromEmail(credentials.getCaller());
		log.info("Found the following user: " + contestant);
		
		CredentialValidationResult result;
		if (contestant != null && contestant.validate(credentials.getPasswordAsString())) {
			log.info("found user...adding to security context: " + contestant);
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

	protected String getUserConfigFile() {
		return System.getProperty(USERS_LIST_PROPERTY, USERS_LIST_PROPERTY);
	}
}