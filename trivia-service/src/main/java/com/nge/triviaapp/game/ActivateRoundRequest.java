package com.nge.triviaapp.game;

import java.io.Serializable;

import lombok.Data;

@Data
public class ActivateRoundRequest implements Serializable {

	private Long roundId;
	
	private static final long serialVersionUID = 1L;
}