package com.ericdraken.common.executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AutoScalingBlockingExecutor extends BlockingExecutor
{
	private final ConcurrentHashMap<Thread, Long> timings;

	private final int threadTimeoutMs;

	private volatile int currPoolSize;

	private final int maxPoolSize;

	static final Logger logger = LoggerFactory.getLogger( MethodHandles.lookup().lookupClass() );

	/**
	 * Creates a BlockingExecutor which will block and prevent further
	 * submission to the pool when the specified queue size has been reached.
	 * If a thread takes longer than threadTimeoutMs to complete then
	 * the pool size will decrease until there is one thread left. If a thread
	 * finishes before the threadTimeoutMs, the pool size will be increased up to poolSize.
	 *
	 * @param poolSize the number of the threads in the pool
	 */
	AutoScalingBlockingExecutor( int poolSize, int threadTimeoutMs )
	{
		// Both min and max are the same, only the number of
		// permits available changes, and core threads can die
		super( poolSize, "AutoScalingBlockingExecutor" );

		// Time to wait idle before reclaiming a thread
		setKeepAliveTime( Math.min( threadTimeoutMs * 2, Integer.MAX_VALUE ), TimeUnit.MILLISECONDS );
		this.allowCoreThreadTimeOut( true );

		timings = new ConcurrentHashMap<>( poolSize );
		this.threadTimeoutMs = threadTimeoutMs > 0 ? threadTimeoutMs : Integer.MAX_VALUE;
		currPoolSize = poolSize;
		maxPoolSize = poolSize;
	}

	int getCurrPoolSize()
	{
		synchronized ( semaphore )
		{
			return currPoolSize;
		}
	}

	/**
	 * Method invoked prior to executing the given Runnable in the
	 * given thread.  This method is invoked by thread {@code t} that
	 * will execute task {@code r}, and may be used to re-initialize
	 * ThreadLocals, or to perform logging.
	 *
	 * <p>This implementation does nothing, but may be customized in
	 * subclasses. Note: To properly nest multiple overridings, subclasses
	 * should generally invoke {@code super.beforeExecute} at the end of
	 * this method.
	 *
	 * @param t the thread that will run task {@code r}
	 * @param r the task that will be executed
	 */
	@Override
	protected void beforeExecute( Thread t, Runnable r )
	{
		super.beforeExecute( t, r );

		// Get the start time using the Runnable as the key
		timings.put( t, System.currentTimeMillis() );
	}

	/**
	 * Method invoked upon completion of execution of the given Runnable,
	 * by the thread that executed the task.
	 * Releases a semaphore permit.
	 */
	@Override
	protected void afterExecute( final Runnable r, final Throwable t )
	{
		synchronized ( semaphore )
		{
			super.afterExecuteNoRelease( r, t );

			// Get the end time using the Runnable as the key
			long endTime = System.currentTimeMillis();
			long startTime = timings.remove( Thread.currentThread() );	// Should never be null
			// System.out.println( "Took: " + (endTime - startTime) );

			if ( endTime - startTime < threadTimeoutMs )
			{
				semaphore.release();    // Release the acquired permit
				if ( currPoolSize < maxPoolSize )
				{
					currPoolSize++;
					setCorePoolSize( currPoolSize );
					semaphore.release();    // Increase the available permits

					logger.info( "Increased pool size to: {}", currPoolSize );
					// System.out.println( "Increased pool size to: " + currPoolSize );
				}
			}
			else if ( currPoolSize <= 1 )
			{
				semaphore.release();    // Must releases the acquired permit to prevent starvation
			}
			else
			{
				// Not releasing the permit
				currPoolSize--;
				setCorePoolSize( currPoolSize );

				logger.info( "Reduced pool size to: {}", currPoolSize );
				// System.out.println( "Reduced pool size to: " + currPoolSize );
			}
		}
	}
}
