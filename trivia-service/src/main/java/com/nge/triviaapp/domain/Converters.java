package com.nge.triviaapp.domain;

import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import com.nge.triviaapp.ConverterOf;
import com.nge.triviaapp.contestant.BuzzerAcknowledgmentResponse;
import com.nge.triviaapp.contestant.BuzzerResetRequest;

@ApplicationScoped
public class Converters {
	
	@Inject
	private JsonbConfig config;
	
	@ConverterOf(Contestant.class)
	@Produces
	public Function<Contestant, String> contestantConverterFunction() {
		return (contestant) -> {
			Jsonb builder = JsonbBuilder.create(config);
			return builder.toJson(contestant);
		};
	}
	
	@ConverterOf(BuzzerResetRequest.class)
	@Produces
	public Function<BuzzerResetRequest, String> buzzerConverterFunction() {
		return (request) -> {
			Jsonb builder = JsonbBuilder.create(config);
			return builder.toJson(request);
		};
	}
	
	@ConverterOf(BuzzerAcknowledgmentResponse.class)
	@Produces
	public Function<BuzzerAcknowledgmentResponse, String> buzzerAckConverterFunction() {
		return (response) -> {
			Jsonb builder = JsonbBuilder.create(config);
			return builder.toJson(response);
		};
	}
	
	@ConverterOf(Round.class)
	@Produces
	public Function<Round, String> categoryConverterFunction() {
		return (round) -> {
			Jsonb builder = JsonbBuilder.create(config);
			return builder.toJson(round);
		};
	}
	
	@ConverterOf(Question.class)
	@Produces
	public Function<Question, String> questionConverterFunction() {
		return (question) -> {
			Jsonb builder = JsonbBuilder.create(config);
			return builder.toJson(question);
		};
	}
}