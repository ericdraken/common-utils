/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.threads;

import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SleepOnFalsyTest
{
	private boolean alwaysTrue()
	{
		return true;
	}

	private boolean alwaysFalse()
	{
		return false;
	}

	private boolean alwaysThrowsException() throws Exception
	{
		throw new Exception( "Test exception" );
	}

	@Test
	public void implicitlyFalseSleep()
	{
		long start = System.currentTimeMillis();
		assertFalse( Sleep.sleepOnFalsy( this::alwaysFalse, 100, ChronoUnit.MILLIS ) );
		assertTrue( System.currentTimeMillis() - start >= 100 );
	}

	@Test
	public void notImplicitlyFalseSleep()
	{
		// Should not sleep at all
		long start = System.currentTimeMillis();
		assertTrue( Sleep.sleepOnFalsy( this::alwaysTrue, 5000, ChronoUnit.MILLIS ) );
		assertTrue( System.currentTimeMillis() - start < 1000 );
	}

	@Test
	public void implicitlyFalseExceptionSleep()
	{
		long start = System.currentTimeMillis();
		assertFalse( Sleep.sleepOnFalsy( this::alwaysThrowsException, 100, ChronoUnit.MILLIS ) );
		assertTrue( System.currentTimeMillis() - start >= 100 );
	}
}