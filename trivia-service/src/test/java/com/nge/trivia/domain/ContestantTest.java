package com.nge.trivia.domain;

import com.nge.triviaapp.domain.Contestant;

public class ContestantTest {
	
	private static final String EXPOSED_PWD  = "Password@123";
	
	public static void main(String[] args) {
		Contestant c = new Contestant();
		c.savePassword(EXPOSED_PWD);
		System.out.println("Validate pwd: " + c.validate(EXPOSED_PWD));
	}

}
