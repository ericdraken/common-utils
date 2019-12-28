/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.io;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class MapWriter implements Closeable
{
	private final File file;

	private HashMap<String, Object> numberMap;

	private final Lock lock = new ReentrantLock();

	protected static final Logger logger = LoggerFactory.getLogger( MethodHandles.lookup().lookupClass() );

	/**
	 * Default constructor
	 */
	public MapWriter( @Nonnull File file )
	{
		this.file = file;

		lock.lock();
		try ( FileReader reader = new FileReader( file ) )
		{
			// Load the json from disk
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>()
			{
			};
			numberMap = mapper.readValue( IOUtils.toString( reader ), typeRef );
			logger.info( "Opened {}", file.getAbsolutePath() );
		}
		catch ( Exception e )
		{
			// Create a new map
			logger.info( "Creating new map because: {}", e.getMessage() );
			numberMap = new HashMap<>();
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * Set the instant for a given key and write to disk immediately
	 *
	 * @param key     Key
	 * @param instant Instant
	 * @return boolean True if written successfully
	 */
	public boolean writeInstantForKey( String key, Instant instant )
	{
		return writeNumberForKey( key, instant.getEpochSecond() );
	}

	/**
	 * Set the instant for a given key and write to disk immediately
	 *
	 * @param key Key
	 * @return boolean True if written successfully
	 */
	public boolean writeInstantForKey( String key )
	{
		return writeInstantForKey( key, Instant.now() );
	}

	/**
	 * Get the instant gor a given key from the in-memory map
	 *
	 * @param key Key
	 * @return Instant Instant
	 */
	public Instant readInstantForKey( String key )
	{
		Number timestamp = readNumberForKey( key );
		return timestamp == null ?
			Instant.EPOCH :
			Instant.ofEpochSecond( timestamp.longValue() );
	}

	/**
	 * Get the object for a given key from the in-memory map
	 *
	 * @param key Key
	 * @return Number Number
	 */
	public Number readNumberForKey( String key )
	{
		lock.lock();
		try
		{
			return (Number) numberMap.get( key );
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * Set the object for a given key and write to disk immediately
	 *
	 * @param key   Key
	 * @param value Number
	 * @return boolean True if written successfully
	 */
	public boolean writeNumberForKey( String key, Number value )
	{
		lock.lock();
		try
		{
			numberMap.put( key, value );
			logger.debug( "Updated: {}:{}", key, value );
			return writeMapToDisk();
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * Write the map to disk
	 *
	 * @return true on success, false on failure
	 */
	private boolean writeMapToDisk()
	{
		// Write map to disk each time
		try ( FileWriter fw = new FileWriter( file, false ) )
		{
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString( numberMap );

			fw.write( json );
			return true;
		}
		catch ( Exception e )
		{
			logger.error( e.getMessage() );
			return false;
		}
	}

	@Override
	public void close()
	{
		lock.lock();
		try
		{
			writeMapToDisk();
		}
		finally
		{
			lock.unlock();
		}
	}

	@Override
	public String toString()
	{
		return numberMap.entrySet().stream()
			.map( x -> x.getKey() + ": " + x.getValue() )
			.collect( Collectors.joining( System.lineSeparator() ) );
	}
}
