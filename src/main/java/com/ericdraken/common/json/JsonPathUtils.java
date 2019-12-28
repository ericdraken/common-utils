/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.json;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class JsonPathUtils
{
	private static final Logger logger = LoggerFactory.getLogger( MethodHandles.lookup().lookupClass() );

	private JsonPathUtils()
	{
	}

	public static <T> T readOrFail( @Nonnull Object o, @Nonnull String jsonPathStr )
	{
		try
		{
			return JsonPath.read( o, jsonPathStr );
		}
		catch ( PathNotFoundException e )
		{
			logException( e, o );
			throw e;
		}
	}

	public static <T> T readOrDefault( @Nonnull Object o, T defaultValue, @Nonnull String jsonPathStr )
	{
		try
		{
			return JsonPath.read( o, jsonPathStr );
		}
		catch ( PathNotFoundException e )
		{
			logException( e, o );
			return defaultValue;
		}
	}

	public static <T> T readOrNull( @Nonnull Object o, @Nonnull String jsonPathStr )
	{
		try
		{
			return JsonPath.read( o, jsonPathStr );
		}
		catch ( PathNotFoundException e )
		{
			logException( e, o );
			return null;
		}
	}

	public static <T> T readOrNullSilent( @Nonnull Object o, @Nonnull String jsonPathStr )
	{
		try
		{
			return JsonPath.read( o, jsonPathStr );
		}
		catch ( PathNotFoundException e )
		{
			return null;
		}
	}

	public static int readIntOrDefault( @Nonnull Object o, int defaultVal, @Nonnull String jsonPathStr )
	{
		try
		{
			return JsonPath.read( o, jsonPathStr );
		}
		catch ( PathNotFoundException e )
		{
			logException( e, o );
			return defaultVal;
		}
	}

	private static String getJsonString( @Nonnull Object o )
	{
		return Configuration
			.defaultConfiguration()
			.jsonProvider()
			.toJson( o );
	}

	private static void logException( @Nonnull Exception e, @Nonnull final Object o )
	{
		Objects.requireNonNull( e );
		logger.warn( "{} - Json is: {}", e.getMessage(), getJsonString( o ) );
	}
}
