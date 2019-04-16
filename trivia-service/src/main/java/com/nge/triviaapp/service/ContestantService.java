package com.nge.triviaapp.service;

import java.security.Principal;

import javax.ejb.Local;

import com.nge.triviaapp.security.UserDetails;
import com.nge.triviaapp.service.category.domain.Contestant;

@Local
public interface ContestantService {

	public Contestant getConstantant(Long contestantId);

	public Contestant findFromEmail(String email);

	public Contestant createFromUserDetails(UserDetails details);

	public Contestant getConstantant(Principal contestantPrincipal);
}