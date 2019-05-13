package com.nge.triviaapp.security;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.json.bind.JsonbConfig;
import javax.security.enterprise.SecurityContext;

@ApplicationScoped
public class SecuredPropertyJsonbConfigProducer {

	@Inject
	private SecurityContext securityContext;
	
	@Produces
	public JsonbConfig getJsonbConfig() {
		SecuredPropertyVisibilityStategy pvs = new SecuredPropertyVisibilityStategy(securityContext);
		return new JsonbConfig().withPropertyVisibilityStrategy(pvs);
	}
}