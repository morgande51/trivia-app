package com.nge.triviaapp.security;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.nge.triviaapp.domain.Contestant;
import com.nge.triviaapp.domain.ContestantRole;
import com.nge.triviaapp.domain.TriviaDataService;

import lombok.extern.java.Log;

@Singleton
@Startup
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@PermitAll
@Log
public class UserServiceSessionBean implements UserService {
	
	private static final String USERS_LIST_PROPERTY = "com.nge.triviaapp.users";
	private static final String CONTESTANT_PROPERTY = "users";
	
	@Inject
	private TriviaDataService dataService;
	
	@PostConstruct
	public void init() {
		try (InputStream userListStream = getUserConfigFile()) {
			JsonReader usersReader = Json.createReader(userListStream);
			JsonObject jsonObj = usersReader.readObject();
			Set<Contestant> users = jsonObj.getJsonArray(CONTESTANT_PROPERTY)
				.stream()
				.map(userObj -> Contestant.createFrom(userObj.asJsonObject()))
				.collect(Collectors.toSet());
			addUsers(users);
		}
		catch (IOException e) {
			log.severe("Unable to load users.  Abort!!!!!");
			log.severe(e.getMessage());
			throw new RuntimeException(e);
		}		
	}
	
	public void addUsers(Set<Contestant> users) {
		users.forEach(u -> {
			log.info("Attempting to lookup: " + u);
			Contestant contestant = dataService.getContestantByEmail(u.getEmail());
			if (contestant == null) {
				Set<ContestantRole> roles = new HashSet<>(u.getContestantRoles());
				u.setContestantRoles(null);
				dataService.persist(u);
				dataService.flush();
				log.info("after the user is saved, we save the roles...");
				roles.forEach(r -> r.getId().setContestantId(u.getId()));
				u.setContestantRoles(roles);
			}
		});
	}
	
	@Override
	public Contestant findFromEmail(String email) {
		return dataService.getContestantByEmail(email);
	}
	
	private InputStream getUserConfigFile() throws FileNotFoundException {
		String externalLocation = System.getProperty(USERS_LIST_PROPERTY);
		return new FileInputStream(externalLocation);
	}
}