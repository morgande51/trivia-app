package com.nge.triviaapp.contestant;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class BuzzerAcknowledgmentResponse implements Serializable {

	boolean isFirst;
	
	private LocalDateTime timestamp;
	
	public BuzzerAcknowledgmentResponse(boolean isFirst) {
		this.isFirst = isFirst;
		timestamp = LocalDateTime.now();
	}
	
	private static final long serialVersionUID = 1L;
}