package com.nge.triviaapp.security;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.nge.triviaapp.domain.Contestant;

import lombok.extern.java.Log;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@PermitAll
@Log
public class UserServiceSessionBean implements UserService {
	
	private static final String CONTESTANT_FIND_FROM_EMAIL = "Contestant.findFromEmail";
	private static final String EMAIL_PARAM = "email";
	
	@PersistenceContext
	private EntityManager em;
	
	public void addUsers(Set<Contestant> users) {
		users.forEach(u -> {
			log.info("Attempting to lookup: " + u);
			Contestant contestant = searchForContestantBy(u.getEmail().toUpperCase()).findAny().orElse(null);
			if (contestant == null) {
				em.persist(u);
			}
		});
	}
	
	public Contestant getConstantant(Long contestantId) {
		return em.find(Contestant.class, contestantId);
	}
	
	public Contestant getConstantant(Principal contestantPrincipal) {
		return searchForContestantBy(contestantPrincipal.getName()).findAny().get();
	}

	public Contestant findFromEmail(String email) {
		return searchForContestantBy(email.toUpperCase()).findAny().orElse(null);
	}
	
	private Stream<Contestant> searchForContestantBy(String email) {
		return em.createNamedQuery(CONTESTANT_FIND_FROM_EMAIL, Contestant.class)
				.setParameter(EMAIL_PARAM, email)
				.getResultStream();
	}
}