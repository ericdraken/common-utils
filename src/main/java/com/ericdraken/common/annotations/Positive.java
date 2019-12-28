/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifier;
import javax.annotation.meta.TypeQualifierValidator;
import javax.annotation.meta.When;

/**
 * This annotation is used to annotate a value that should only contain nonnegative values.
 * <p>
 * When this annotation is applied to a method it applies to the method return value.
 */
@Documented
@TypeQualifier(applicableTo = Number.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Positive {
	When when() default When.ALWAYS;

	class Checker implements TypeQualifierValidator<Positive> {

		@Nonnull
		public When forConstantValue( @Nonnull Positive annotation, Object v) {
			if (!(v instanceof Number))
				return When.NEVER;
			boolean isNonPositive;
			Number value = (Number) v;
			if (value instanceof Long)
				isNonPositive = value.longValue() <= 0;
			else if (value instanceof Double)
				isNonPositive = value.doubleValue() <= 0;
			else if (value instanceof Float)
				isNonPositive = value.floatValue() <= 0;
			else
				isNonPositive = value.intValue() <= 0;

			if (isNonPositive)
				return When.NEVER;
			else
				return When.ALWAYS;
		}
	}
}

