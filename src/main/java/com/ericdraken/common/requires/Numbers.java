/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.requires;

public class Numbers
{
	public static <T> T requireNonNegative(T obj) {
		if (!(obj instanceof Number))
			throw new IllegalArgumentException( "Not a number" );
		boolean isPositive;
		Number value = (Number) obj;
		if (value instanceof Long)
			isPositive = value.longValue() < 0;
		else if (value instanceof Double)
			isPositive = value.doubleValue() < 0;
		else if (value instanceof Float)
			isPositive = value.floatValue() < 0;
		else
			isPositive = value.intValue() < 0;

		if ( isPositive )
			throw new IllegalArgumentException( "Number " + obj + " is negative" );
		else
			return obj;
	}

	public static <T> T requirePositive(T obj) {
		if (!(obj instanceof Number))
			throw new IllegalArgumentException( "Not a number" );
		boolean isNonPositive;
		Number value = (Number) obj;
		if (value instanceof Long)
			isNonPositive = value.longValue() <= 0;
		else if (value instanceof Double)
			isNonPositive = value.doubleValue() <= 0;
		else if (value instanceof Float)
			isNonPositive = value.floatValue() <= 0;
		else
			isNonPositive = value.intValue() <= 0;

		if ( isNonPositive )
			throw new IllegalArgumentException( "Number " + obj + " is not positive" );
		else
			return obj;
	}
}
