package com.nge.triviaapp.domain;

public interface ActiveLocator {

	public Contestant getActiveContestant();
	
	public Question getActiveQuestion();
	
	public Round getActiveRound();
}