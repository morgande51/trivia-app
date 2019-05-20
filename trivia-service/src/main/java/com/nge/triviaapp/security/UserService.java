package com.nge.triviaapp.security;

import javax.ejb.Local;

import com.nge.triviaapp.domain.Contestant;

@Local
public interface UserService {

	public Contestant findFromEmail(String email);
}