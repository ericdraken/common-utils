/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.io;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapWriterTest
{
	private File file;

	@BeforeEach
	void setUp() throws Exception
	{
		file = File.createTempFile( "jobs", "json" );
	}

	@AfterEach
	void tearDown() throws IOException
	{
		Files.delete( file.toPath() );
	}

	@Test
	void writeReadObjectForKey() throws IOException
	{
		try ( MapWriter p = new MapWriter( file ) )
		{
			p.writeNumberForKey( "123", 123 );
			assertEquals( 123, p.readNumberForKey( "123" ) );

//			System.out.println( p );
//
//			Scanner sc = new Scanner( file );
//			while ( sc.hasNextLine() )
//			{
//				System.out.println( sc.nextLine() );
//			}
		}
	}

	@Test
	void writeReadInstantForKey() throws IOException
	{
		try ( MapWriter p = new MapWriter( file ) )
		{
			p.writeInstantForKey( "123", Instant.ofEpochSecond( 123 ) );
			assertEquals( Instant.ofEpochSecond( 123 ), p.readInstantForKey( "123" ) );

//			System.out.println( p );
//
//			Scanner sc = new Scanner( file );
//			while ( sc.hasNextLine() )
//			{
//				System.out.println( sc.nextLine() );
//			}
		}
	}

	@Test
	void readTimeNotFoundTest()
	{
		try ( MapWriter p = new MapWriter( file ) )
		{
			assertEquals( Instant.ofEpochSecond( 0 ), p.readInstantForKey( "not found" ) );
		}
	}

	@Test
	void writeCloseOpenReadTest() throws IOException
	{
		File file = File.createTempFile( "writeCloseOpenReadTest", "json" );
		file.deleteOnExit();

		// Write and close
		try ( MapWriter p = new MapWriter( file ) )
		{
			p.writeNumberForKey( "123", 123 );
			assertEquals( 123, p.readNumberForKey( "123" ) );
		}

		// Open, read, and then close
		try ( MapWriter p = new MapWriter( file ) )
		{
			assertEquals( 123, p.readNumberForKey( "123" ) );
		}
	}
}