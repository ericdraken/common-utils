/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.executors;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The default named thread factory.
 */
public class NamedThreadFactory implements ThreadFactory
{
	private static final AtomicInteger poolNumber = new AtomicInteger( 1 );
	private final AtomicInteger threadNumber = new AtomicInteger( 1 );
	private final String namePrefix;

	public NamedThreadFactory( String prefix )
	{
		namePrefix = prefix + "-" +	poolNumber.getAndIncrement() + "-thread-";
	}

	public Thread newThread( @NotNull Runnable r )
	{
		Thread t = new Thread( r,namePrefix + threadNumber.getAndIncrement() );
		if ( t.isDaemon() )
		{
			t.setDaemon( false );
		}
		if ( t.getPriority() != Thread.NORM_PRIORITY )
		{
			t.setPriority( Thread.NORM_PRIORITY );
		}
		return t;
	}
}
