package com.nge.triviaapp.security;

import java.security.Principal;
import java.util.stream.Stream;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.ejb3.annotation.SecurityDomain;

import com.nge.triviaapp.domain.Contestant;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@SecurityDomain(TriviaSecurity.DOMAIN)
//@DeclareRoles({TriviaSecurity.CONTESTANT_ROLE, TriviaSecurity.HOST_ROLE, TriviaSecurity.ADMIN_ROLE})
public class UserServiceSessionBean implements UserService {

	@PersistenceContext
	private EntityManager em;
	
	@PermitAll
	public Contestant getConstantant(Long contestantId) {
		return em.find(Contestant.class, contestantId);
	}
	
	@PermitAll
	public Contestant getConstantant(Principal contestantPrincipal) {
		return searchForContestantBy(contestantPrincipal.getName()).findAny().get();
	}
	
	@PermitAll
	public Contestant findFromEmail(String email) {
		return searchForContestantBy(email).findAny().orElse(null);
	}
	
	private Stream<Contestant> searchForContestantBy(String email) {
		return em.createNamedQuery("Contestant.findFromEmail", Contestant.class)
				.setParameter("email", email)
				.getResultStream();
	}
	
	@PermitAll
	public Contestant createFromUserDetails(UserDetails details) {
		Contestant contestant = details.getContestant();
		em.persist(contestant);
		return contestant;
	}
}
