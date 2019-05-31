package com.nge.triviaapp.security;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import lombok.extern.slf4j.Slf4j;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class SecuredPropertyResolver implements ContextResolver<Jsonb> {
		
	@Inject
	private JsonbConfig config;
	
	@PostConstruct
	public void init() {
		log.info("our resolver has been init!!!!");
	}
	
	@Override
	public Jsonb getContext(Class<?> type) {
		log.debug("we are resolving this type: " + type.getName());
		Jsonb builder = null;
		
		if (isSecuredType(type)) {
			builder = JsonbBuilder.create(config);
		}
		else {
			builder = JsonbBuilder.create();
		}
		return builder;
	}
	
	protected boolean isSecuredType(Class<?> type) {
		return SecuredProperty.class.isAssignableFrom(type) || (type.isArray() && SecuredProperty.class.isAssignableFrom(type.getComponentType()));
	}
}