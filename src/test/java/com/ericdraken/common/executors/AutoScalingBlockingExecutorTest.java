package com.ericdraken.common.executors;

import com.ericdraken.common.functional.CheckedBooleanSupplier;
import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AutoScalingBlockingExecutorTest
{
	@Test
	void testTimings() throws InterruptedException
	{
		// Set the executor
		AutoScalingBlockingExecutor exec = new AutoScalingBlockingExecutor( 3, 100 );
		int waitMs = 500;

		assertEquals( 0, exec.getPoolSize() );		// No threads running yet
		assertEquals( 3, exec.getCurrPoolSize() );
		exec.execute( () -> {} );
		exec.execute( () -> {} );
		exec.execute( () -> {} );
		assertEquals( 3, exec.getPoolSize() );	// Three threads have run
		sleep(waitMs);

		assertEquals( 3, exec.getCurrPoolSize() );
		exec.execute( () -> CheckedBooleanSupplier.falsy( () -> { sleep(200); return true; } ) );
		sleep(waitMs);

		assertEquals( 2, exec.getCurrPoolSize() );
		exec.execute( () -> CheckedBooleanSupplier.falsy( () -> { sleep(200); return true; } ) );
		sleep(waitMs);

		assertEquals( 1, exec.getCurrPoolSize() );
		exec.execute( () -> CheckedBooleanSupplier.falsy( () -> { sleep(200); return true; } ) );
		sleep(waitMs);

		assertEquals( 1, exec.getCurrPoolSize() );
		exec.execute( () -> CheckedBooleanSupplier.falsy( () -> { sleep(200); return true; } ) );
		sleep(waitMs);

		assertEquals( 1, exec.getCurrPoolSize() );
		exec.execute( () -> {} );
		sleep(waitMs);

		assertEquals( 2, exec.getCurrPoolSize() );
		exec.execute( () -> {} );
		sleep(waitMs);

		assertEquals( 3, exec.getCurrPoolSize() );
		exec.execute( () -> {} );
		sleep(waitMs);

		assertEquals( 3, exec.getCurrPoolSize() );
		exec.execute( () -> {} );
		sleep(waitMs);
		assertEquals( 0, exec.getPoolSize() );	// All threads have died

		assertEquals( 3, exec.getCurrPoolSize() );
		exec.execute( () -> CheckedBooleanSupplier.falsy( () -> { sleep(10); return true; } ) );
		assertEquals( 1, exec.getPoolSize() );	// One thread should be spun up
		sleep(waitMs);
		assertEquals( 0, exec.getPoolSize() );	// All threads have died
	}

	@Test
	public void testTimings2()
	{
		// Set the executor
		AutoScalingBlockingExecutor exec = new AutoScalingBlockingExecutor( 5, 500 );

		for ( int i = 0; i < 10; i++ )
		{
			exec.execute( () -> CheckedBooleanSupplier.falsy( () -> { sleep(1000); return true; } ) );
		}
		assertEquals( 1, exec.getPoolSize() );	// All threads have died

		for ( int i = 0; i < 10; i++ )
		{
			exec.execute( () -> CheckedBooleanSupplier.falsy( () -> { sleep(100); return true; } ) );
		}
		assertEquals( 5, exec.getPoolSize() );	// All threads recovered
	}
}