/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.functional;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckedBooleanSupplierTest
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
	void implicitlyFalse()
	{
		assertFalse( CheckedBooleanSupplier.falsy( this::alwaysFalse ) );
	}

	@Test
	void notImplicitlyFalse()
	{
		assertTrue( CheckedBooleanSupplier.falsy( this::alwaysTrue ) );
	}

	@Test
	void implicitlyFalseException()
	{
		assertFalse( CheckedBooleanSupplier.falsy( this::alwaysThrowsException ) );
	}
}