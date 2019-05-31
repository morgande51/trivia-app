package com.nge.triviaapp.security;

import static com.nge.triviaapp.security.TriviaSecurity.ADMIN_ROLE;
import static com.nge.triviaapp.security.TriviaSecurity.CONTESTANT_ROLE;
import static com.nge.triviaapp.security.TriviaSecurity.HOST_ROLE;

import java.security.Principal;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * Session Bean implementation class PrincipalLocatorService
 */
@Slf4j
@Stateless
@RolesAllowed({CONTESTANT_ROLE, HOST_ROLE, ADMIN_ROLE})
public class PrincipalLocatorServiceSessionBean implements PrincipalLocatorService {

	@PersistenceContext
    private EntityManager em;
	
	@Inject
	private SecurityContext securityContext;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public <T> T getPrincipalUser(Class<T> principalType) {
		Principal principal = getPrincipal();
		
		T t = null;
		if (principal != null) {
			t = em.createNamedQuery("Contestant.findByEmail", principalType)
				.setParameter("email", principal.getName().toUpperCase())
				.getResultStream()
				.findAny()
				.get();
			log.debug("located principal: " + t);
		}
		return t;
	}
	
	protected Principal getPrincipal() {
		Principal p = null;
		if (securityContext != null) {
			p = securityContext.getCallerPrincipal();
		}
		return p;
	}
}