package com.nge.triviaapp.security;

import java.security.Principal;
import java.util.Set;

import javax.ejb.Local;

import com.nge.triviaapp.domain.Contestant;

@Local
public interface UserService {

	public Contestant getConstantant(Long contestantId);

	public Contestant findFromEmail(String email);

	public Contestant getConstantant(Principal contestantPrincipal);

	public void addUsers(Set<Contestant> users);
}