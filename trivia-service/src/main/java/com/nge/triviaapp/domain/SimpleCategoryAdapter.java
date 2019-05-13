package com.nge.triviaapp.domain;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.bind.adapter.JsonbAdapter;

public class SimpleCategoryAdapter implements JsonbAdapter<Category, JsonObject> {

	private static final String ID_PROPERTY = "id";
	private static final String NAME_PROPERTY = "name";
	private static final String ROUND_PROPERTY = "round";

	@Override
	public JsonObject adaptToJson(Category c) throws Exception {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add(ID_PROPERTY, c.getId());
		builder.add(NAME_PROPERTY, c.getName());
		builder.add(ROUND_PROPERTY, c.getRound().getId());
		return builder.build();
	}

	@Override
	public Category adaptFromJson(JsonObject json) throws Exception {
		Long id = json.getJsonNumber(ID_PROPERTY).longValue();
		String name = json.getString(NAME_PROPERTY, null);
		Long roundId = json.getJsonNumber(ROUND_PROPERTY).longValue();
		
		Category c = new Category();
		c.setId(id);
		c.setName(name);
		
		Round round = new Round();
		round.setId(roundId);
		c.setRound(round);
		return c;
	}
}