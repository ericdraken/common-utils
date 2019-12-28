/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.functional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface CheckedBooleanSupplier {

	class Logger0 {
		// Log with the class name
		public static final Logger logger = LoggerFactory.getLogger(
			MethodHandles.lookup().lookupClass().getEnclosingClass() );

		private Logger0()
		{
		}
	}

	boolean getAsBoolean() throws Exception;

	static boolean falsy( CheckedBooleanSupplier function )
	{
		try
		{
			return function.getAsBoolean();
		}
		catch ( Exception e )
		{
			Logger0.logger.warn( "Return false on exception: {} - {}", e.getClass().getName(), e.getMessage() );
			return false;
		}
	}

	static boolean truthy( CheckedBooleanSupplier function )
	{
		try
		{
			return function.getAsBoolean();
		}
		catch ( Exception e )
		{
			Logger0.logger.warn( "Return true on exception: {} - {}", e.getClass().getName(), e.getMessage() );
			return true;
		}
	}

	static <T, R> Supplier<R> bind( Function<T, R> fn, T val ) {
		return () -> fn.apply(val);
	}
}
