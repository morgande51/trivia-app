package com.nge.triviaapp.domain;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.bind.adapter.JsonbAdapter;

public class SimpleContestantAdapter implements JsonbAdapter<Contestant, JsonObject> {

	private static final String ID_PROPERTY = "id";
	private static final String EMAIL_PROPERTY = "email";
	private static final String FIRST_NAME_PROPERTY = "firstName";
	private static final String LAST_NAME_PROPERTY = "lastName";

	@Override
	public JsonObject adaptToJson(Contestant c) throws Exception {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add(ID_PROPERTY, c.getId());
		return builder.build();
	}

	@Override
	public Contestant adaptFromJson(JsonObject json) throws Exception {
		Long id = json.getJsonNumber(ID_PROPERTY).longValue();
		String email = json.getString(EMAIL_PROPERTY, null);
		String firstName =json.getString(FIRST_NAME_PROPERTY, null);
		String lastName = json.getString(LAST_NAME_PROPERTY, null);
		
		Contestant c = new Contestant();
		c.setId(id);
		c.setEmail(email);
		c.setFirstName(firstName);
		c.setLastName(lastName);
		return c;
	}
}