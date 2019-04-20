package com.nge.triviaapp.host;

import java.io.Serializable;

import lombok.Data;

@Data
public class ActivateRoundRequest implements Serializable {

	private Long roundId;
	
	private static final long serialVersionUID = 1L;
}