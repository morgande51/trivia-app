package com.nge.triviaapp.domain;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.json.bind.config.PropertyVisibilityStrategy;

public class FieldVisibilityStrategy implements PropertyVisibilityStrategy {

	@Override
	public boolean isVisible(Field field) {
		return true;
	}

	@Override
	public boolean isVisible(Method method) {
		return false;
	}
}