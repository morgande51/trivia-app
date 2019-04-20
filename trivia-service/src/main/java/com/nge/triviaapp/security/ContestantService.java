package com.nge.triviaapp.security;

import java.security.Principal;

import javax.ejb.Local;

import com.nge.triviaapp.domain.Contestant;

@Local
public interface ContestantService {

	public Contestant getConstantant(Long contestantId);

	public Contestant findFromEmail(String email);

	public Contestant createFromUserDetails(UserDetails details);

	public Contestant getConstantant(Principal contestantPrincipal);
}