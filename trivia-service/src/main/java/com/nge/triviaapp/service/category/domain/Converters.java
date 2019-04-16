package com.nge.triviaapp.service.category.domain;

import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import com.nge.triviaapp.ConverterOf;
import com.nge.triviaapp.service.BuzzerAcknowledgmentResponse;
import com.nge.triviaapp.service.BuzzerResetRequest;

@ApplicationScoped
public class Converters {
	
	@ConverterOf(Contestant.class)
	@Produces
	public Function<Contestant, String> contestantConverterFunction() {
		return (contestant) -> {
			Jsonb builder = JsonbBuilder.create();
			return builder.toJson(contestant);
		};
	}
	
	@ConverterOf(BuzzerResetRequest.class)
	@Produces
	public Function<BuzzerResetRequest, String> buzzerConverterFunction() {
		return (request) -> {
			Jsonb builder = JsonbBuilder.create();
			return builder.toJson(request);
		};
	}
	
	@ConverterOf(BuzzerAcknowledgmentResponse.class)
	@Produces
	public Function<BuzzerAcknowledgmentResponse, String> buzzerAckConverterFunction() {
		return (response) -> {
			Jsonb builder = JsonbBuilder.create();
			return builder.toJson(response);
		};
	}
}