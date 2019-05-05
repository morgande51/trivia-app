package com.nge.triviaapp.host;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import com.nge.triviaapp.domain.QuestionAnswerType;

@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD, TYPE, PARAMETER})
@Qualifier
public @interface Answer {

	QuestionAnswerType value();
}