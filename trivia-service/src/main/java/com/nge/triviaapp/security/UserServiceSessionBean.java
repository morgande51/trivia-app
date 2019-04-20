package com.nge.triviaapp.security;

import java.security.Principal;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.nge.triviaapp.domain.Contestant;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserServiceSessionBean implements UserService {

	@PersistenceContext
	private EntityManager em;
	
	@Override
	public Contestant getConstantant(Long contestantId) {
		return em.find(Contestant.class, contestantId);
	}
	
	@Override
	public Contestant getConstantant(Principal contestantPrincipal) {
		return searchForContestantBy(contestantPrincipal.getName()).findAny().get();
	}
	
	@Override
	public Contestant findFromEmail(String email) {
		return searchForContestantBy(email).findAny().orElse(null);
	}
	
	private Stream<Contestant> searchForContestantBy(String email) {
		return em.createNamedQuery("Contestant.findFromEmail", Contestant.class)
				.setParameter("email", email)
				.getResultStream();
	}
	
	@Override
	public Contestant createFromUserDetails(UserDetails details) {
		Contestant contestant = details.getContestant();
		em.persist(contestant);
		return contestant;
	}
}
