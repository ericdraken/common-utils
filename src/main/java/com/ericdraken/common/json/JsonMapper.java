/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonMapper
{
	private JsonMapper()
	{
	}

	/**
	 * Map an object's public methods to a JSON string
	 *
	 * @param object Object
	 * @return JSON string
	 * @throws JsonProcessingException Exception on JSON processing
	 */
	public static String toJson( Object object ) throws JsonProcessingException
	{
		// Write JSON from object
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString( object );
	}

	/**
	 * Map an object's public methods to an Object
	 *
	 * @param json JSON string
	 * @param type Output object
	 * @param <T>  Object type
	 * @return Object of type T
	 * @throws IOException Disk exception
	 */
	public static <T> T fromJson( String json, Class<T> type ) throws IOException
	{
		// Write JSON from object
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue( json, type );
	}
}
