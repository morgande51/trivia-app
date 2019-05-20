package com.nge.triviaapp.contestant;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Retention(RUNTIME)
@Target({ FIELD, METHOD, TYPE, PARAMETER })
@Qualifier
public @interface BuzzerReset {

	boolean admin() default false;
}