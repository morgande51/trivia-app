package com.nge.triviaapp.service;

import javax.ejb.Local;

@Local
public interface PrincipalLocatorService {

	public <T> T getPrincipalUser(Class<T> principalClass);

}
