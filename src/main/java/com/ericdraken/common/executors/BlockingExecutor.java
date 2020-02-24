/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.*;

/**
 * An executor which blocks and prevents further tasks from
 * being submitted to the pool when the queue is full.
 * Based on the BoundedExecutor example in:
 * Brian Goetz, 2006. Java Concurrency in Practice. (Listing 8.4)
 */
public class BlockingExecutor extends ThreadPoolExecutor
{
    final Semaphore semaphore;

    // Log with the class name
    private static final Logger logger = LoggerFactory.getLogger( MethodHandles.lookup().lookupClass() );

    /**
     * Creates a BlockingExecutor which will block and prevent further
     * submission to the pool when the specified queue size has been reached.
     *
     * @param poolSize  the number of the threads in the pool
     */
    public BlockingExecutor( final int poolSize, String threadNamePrefix ) {
        super( poolSize, poolSize, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<>(), new NamedThreadFactory( threadNamePrefix ) );

        // the semaphore is bounding both the number of tasks currently executing
        // and those queued up
        semaphore = new Semaphore( poolSize );
    }

    /**
     * Executes the given task.
     * This method will block when the semaphore has no permits
     * i.e. when the queue has reached its capacity.
     */
    @Override
    public void execute( final Runnable task ) {
        boolean acquired = false;
        do {
            try {
                semaphore.acquire();
                acquired = true;
            } catch ( final InterruptedException e ) { // NOSONAR
                // Thread.currentThread().interrupt();
            }
        } while ( !acquired );

        try {
            super.execute( task );
        } catch ( final RejectedExecutionException e ) {
            semaphore.release();
            throw e;
        }
    }

    /**
     * Method invoked upon completion of execution of the given Runnable,
     * by the thread that executed the task.
     * Releases a semaphore permit.
     */
    @Override
    protected void afterExecute( final Runnable r, final Throwable t ) {
        super.afterExecute( r, t );
        semaphore.release();
    }

    // Same as above but without the semaphore release
    void afterExecuteNoRelease( final Runnable r, final Throwable t ) {
        super.afterExecute( r, t );
    }

    /**
     * Wait for all threads to finish, then shutdown gracefully
     */
    public void waitAndShutdown() {
        // Wait for all jobs and threads to fully complete
        while ( getActiveCount() > 0 || ! getQueue().isEmpty() ) {
            try {
                Thread.sleep(200);
            } catch ( InterruptedException e ) { // NOSONAR
                // Thread.currentThread().interrupt();
            }
        }

        // Graceful shutdown
        try {
            logger.debug("Attempting to shutdown executor pool");
            shutdown();
            awaitTermination(5, TimeUnit.SECONDS);
        }
        catch ( InterruptedException e) { // NOSONAR
            logger.warn("Shutdown interrupted: {}", e.getMessage());
            // Thread.currentThread().interrupt();
        }
        finally {
            if (!isTerminated()) {
                logger.warn("Forcibly canceling unfinished tasks");
            }
            shutdownNow();
            logger.debug("Shutdown finished");
        }
    }
}
