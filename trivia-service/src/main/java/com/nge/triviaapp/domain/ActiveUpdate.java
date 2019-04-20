package com.nge.triviaapp.domain;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD, TYPE, PARAMETER})
@Qualifier
public @interface ActiveUpdate {

	Class<? extends Active> value();
}