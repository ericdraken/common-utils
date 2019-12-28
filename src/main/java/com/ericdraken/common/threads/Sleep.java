/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.threads;

import com.ericdraken.common.functional.CheckedBooleanSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public abstract class Sleep
{
	private static final Logger logger = LoggerFactory.getLogger( MethodHandles.lookup().lookupClass() );

	private static final SimpleDateFormat formatter = new SimpleDateFormat( "MMM dd, HH:mm", Locale.CANADA ){{
		setTimeZone( TimeZone.getDefault() );
	}};

	private Sleep()
	{
	}

	/**
	 * Sleep for a specified number of time units
	 *
	 * @param sleepFor How long to sleep
	 * @param unit     Sleep time units
	 * @throws InterruptedException Thread is interrupted
	 */
	public static void sleep( long sleepFor, TimeUnit unit ) throws InterruptedException
	{
		Thread.sleep( unit.toMillis( sleepFor ) );
	}

	/**
	 * Park, not sleep, the thread for hopefully more precise control
	 * of how long the thread "sleeps". Notice that the thread is dormant
	 * and disabled, not signalling it will give yield control, just parked
	 *
	 * @param parkFor How long to park for
	 * @param unit Time unit
	 * @throws InterruptedException Thread is interrupted
	 * @see [https://www.reddit.com/r/java/comments/beq0wx/is_threadsleep_good_for_game_loops/]
	 */
	public static void parkThread( long parkFor, TimeUnit unit ) throws InterruptedException
	{
		long parkUntilMs = unit.toMillis( parkFor ) + System.currentTimeMillis();
		for ( ; ; )
		{
			if ( System.currentTimeMillis() >= parkUntilMs )
			{
				return;
			}
			// Remember spurious wakeups
			LockSupport.parkUntil( parkUntilMs );
			if ( Thread.interrupted() )
			{
				throw new InterruptedException( "Park interrupted" );
			}
		}
	}

	/**
	 * Park, not sleep, the thread for hopefully more precise control
	 * of how long the thread "sleeps". Notice that the thread is dormant
	 * and disabled, not signalling it will give yield control, just parked
	 *
	 * @param parkForMs How long to park for in milliseconds
	 * @throws InterruptedException Thread is interrupted
	 * @see [https://www.reddit.com/r/java/comments/beq0wx/is_threadsleep_good_for_game_loops/]
	 */
	public static void parkThread( long parkForMs ) throws InterruptedException
	{
		parkThread( parkForMs, TimeUnit.MILLISECONDS );
	}

	/**
	 * If the function is implicitly false, sleep for N time units before returning
	 *
	 * @param function Boolean function to evaluate
	 * @param sleep    Time units to sleep for
	 * @param unit     Time unit
	 * @return True if a sleep occurred (function is falsy), false if a sleep did not occur (function is true)
	 */
	public static boolean sleepOnFalsy( CheckedBooleanSupplier function, long sleep, ChronoUnit unit )
	{
		boolean res = CheckedBooleanSupplier.falsy( function );
		if ( ! res )
		{
			Instant now = Instant.now();
			Instant then = now.plus( sleep, unit );
			if ( logger.isInfoEnabled() )
				logger.info( "Retrying {} at {}", function, formatter.format( Date.from( then ) ) );

			try
			{
				Thread.sleep( then.toEpochMilli() - now.toEpochMilli() );
			}
			catch ( InterruptedException e )
			{
				logger.info( "Woken up from sleep" );
				Thread.currentThread().interrupt();
			}
		}
		return res;
	}

	/**
	 * If the function throws an exception, sleep for N time units before returning
	 *
	 * @param function Boolean function to evaluate
	 * @param sleep    Time units to sleep for
	 * @param unit     Time unit
	 * @return Return the value of the supplied function, or false on exception
	 */
	public static boolean sleepOnExceptionFalsy( CheckedBooleanSupplier function, long sleep, ChronoUnit unit )
	{
		try
		{
			return function.getAsBoolean();
		}
		catch ( Exception e )
		{
			Instant now = Instant.now();
			Instant then = now.plus( sleep, unit );
			logger.info( "Retrying {} at {}", function, formatter.format( Date.from( then ) ) );

			try
			{
				Thread.sleep( then.toEpochMilli() - now.toEpochMilli() );
			}
			catch ( InterruptedException e1 )
			{
				logger.info( "Woken up from sleep" );
				Thread.currentThread().interrupt();
			}
		}

		return false;
	}
}
