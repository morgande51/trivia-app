package com.nge.triviaapp.security;

import java.security.Principal;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.SecurityContext;

/**
 * Session Bean implementation class PrincipalLocatorService
 */
@Stateless
public class PrincipalLocatorServiceSessionBean implements PrincipalLocatorService {

	@PersistenceContext
    private EntityManager em;
	
	@Inject
	private SecurityContext securityContext;
	
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public <T> T getPrincipalUser(Class<T> principalType) {
		Principal principal = securityContext.getCallerPrincipal();
//		return em.find(principalType, principal.getName());
		return em.createNamedQuery("Contestant.findFromEmail", principalType)
				.setParameter("email", principal.getName())
				.getResultStream()
				.findAny()
				.get();
	}
}