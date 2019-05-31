package com.nge.triviaapp.domain;

import javax.ejb.Local;

@Local
public interface ActiveDomainManager {

	public Round getActiveRound();

	public Question getActiveQuestion();

	public Contestant getActiveContestant();
	
	public default <T extends ActiveDomain> void setActiveResource(T domain) {
		setActiveResource(domain, true);
	}

	public <T extends ActiveDomain> void setActiveResource(T domain, boolean fireUpdate);
	
	public default <T extends ActiveDomain> void clearActiveResource(Class<T> domainClass) {
		clearActiveResource(domainClass, true);
	}
	
	public <T extends ActiveDomain> void clearActiveResource(Class<T> domainClass, boolean fireEvent);
}