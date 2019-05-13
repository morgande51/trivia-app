package com.nge.triviaapp.contestant;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.json.bind.annotation.JsonbTransient;

import com.nge.triviaapp.domain.Contestant;

import lombok.Getter;

@Getter
public class BuzzerAcknowledgmentResponse implements Serializable {
	
	@JsonbTransient
	private Contestant contestant;
	
	private LocalDateTime timestamp;
	
	private boolean isFirst;
	
	public BuzzerAcknowledgmentResponse() {
		timestamp = LocalDateTime.now();
		isFirst = false;
	}
	
	public BuzzerAcknowledgmentResponse(Contestant contestant) {
		this();
		this.contestant = contestant;
		isFirst = true;
	}

	private static final long serialVersionUID = 1L;
}