package com.nge.triviaapp.domain;

import java.lang.reflect.Field;

import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Getter
@Slf4j
public class ActiveDomainManagerSessionBean implements ActiveDomainManager {
	
	private static final String ILLEGAL_STATE_ERROR = "The resource current state and new state are both null";
	private static final String ACTIVE_DOMAIN_FIELD_FMT = "active%s";
	
	private Round activeRound;
	
	private Question activeQuestion;
	
	private Contestant activeContestant;
	
	@Inject
	@Getter(AccessLevel.NONE)
	private Event<ActiveDomain> activeEvent;
	
	@Lock
	public <T extends ActiveDomain> void clearActiveResource(Class<T> domainClass, boolean fireEvent) {
		log.trace("Clearing Resource: {}", domainClass.getName());
		Field domainField = getActiveDomainField(domainClass);
		updateResource(domainField, null, fireEvent);
	}
	
	@Lock
	public <T extends ActiveDomain> void setActiveResource(T domain, boolean fireEvent) {
		log.trace("Setting active resource: {}", domain.getClass().getName());
		Field domainField = getActiveDomainField(domain);
		updateResource(domainField, domain, fireEvent);
	}
	
	private <T extends ActiveDomain> Field getActiveDomainField(T domain) {
		return getActiveDomainField(domain.getClass());
	}
	
	private <T extends ActiveDomain> Field getActiveDomainField(Class<T> domainClass) {
		String domainName = domainClass.getSimpleName();
		String fieldName = String.format(ACTIVE_DOMAIN_FIELD_FMT, domainName);
		Field field = null;
		try {
			field = getClass().getDeclaredField(fieldName);
		}
		catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		return field;
	}
	
	private <T extends ActiveDomain> void updateResource(Field domainField, T resource, boolean fireEvent) {
		ActiveActionType actionType = resource == null ? ActiveActionType.DELETE : ActiveActionType.UPDATE;
		updateResource(domainField, resource, actionType, fireEvent);
	}
	
	@SuppressWarnings("unchecked")
	private <T extends ActiveDomain> void updateResource(Field domainField, T resource, ActiveActionType action, boolean fireEvent) {
		T activeResource = null;
		try {
			Object obj = domainField.get(this);
			if (obj != null) {
				activeResource = (T) obj;
			}
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		
		if (activeResource == null && resource == null) {
			log.error(ILLEGAL_STATE_ERROR);
			return;
//			throw new IllegalStateException(ILLEGAL_STATE_ERROR);
		}
		
		AnnotationLiteral<Active> literal;
		if (resource != null) {
			literal = resource.getLiteral(action);
		}
		else {
			log.warn("be careful here, the {} is going to be set null", domainField.getName());
			literal = activeResource.getLiteral(action);
		}
		
		if (fireEvent) {
			T target = resource != null ? resource : activeResource;
			activeEvent.select(literal).fire(target);
		}
		
		activeResource = resource;
		try {
			domainField.set(this, resource);
			log.trace("Resource activated: {}", resource);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}