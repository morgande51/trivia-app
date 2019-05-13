package com.nge.triviaapp.security;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import javax.json.bind.config.PropertyVisibilityStrategy;
import javax.security.enterprise.SecurityContext;

import lombok.extern.java.Log;

@Log
public class SecuredPropertyVisibilityStategy implements PropertyVisibilityStrategy {
	
	private SecurityContext securityContext;

	public SecuredPropertyVisibilityStategy(SecurityContext securityContext) {
		super();
		this.securityContext = securityContext;
	}

	@Override
	public boolean isVisible(Field field) {
		SecuredByRoles rolesAllowed = field.getAnnotation(SecuredByRoles.class);
		return isVisible(rolesAllowed);
	}

	@Override
	public boolean isVisible(Method method) {
		return false;
	}
	
	protected boolean isVisible(SecuredByRoles rolesAllowed) {
		boolean visible = true;
		if (rolesAllowed != null) {
			log.fine("this property requres roles...");
			visible = (securityContext != null && 
					   Stream.of(rolesAllowed.value())
					   		.anyMatch(r -> securityContext.isCallerInRole(r)));
			log.fine("the visibility for this data will be: " + visible);
		}
		return visible;
	}
}