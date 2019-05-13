package com.nge.triviaapp.security;

import javax.ejb.Local;

@Local
public interface PrincipalLocatorService {

	public <T> T getPrincipalUser(Class<T> principalClass);

}
