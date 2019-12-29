package com.ericdraken.common.net;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetUtilsTest
{
	@Test
	void isNetworkReachable()
	{
		assertTrue( NetUtils.isNetworkReachable() );
	}

	@Test
	void isReachable()
	{
		assertTrue( NetUtils.isReachable( "1.1.1.1" ) );
		assertTrue( NetUtils.isReachable( "8.8.8.8" ) );
	}

	@Test
	void isReachable_not()
	{
		/*
		192.0.2.0/24 (TEST-NET-1),
		198.51.100.0/24 (TEST-NET-2)
		203.0.113.0/24 (TEST-NET-3)
		*/
		assertFalse( NetUtils.isReachable( "192.0.2.100" ) );
		assertFalse( NetUtils.isReachable( "0.0.0.0" ) );
	}

	@Test
	void isReachableByPing() throws IOException
	{
		assertTrue( NetUtils.isReachableByPing( "1.1.1.1" ) );
		assertTrue( NetUtils.isReachableByPing( "8.8.8.8" ) );
	}

	@Test
	void isReachableByPing_not() throws IOException
	{
		/*
		192.0.2.0/24 (TEST-NET-1),
		198.51.100.0/24 (TEST-NET-2)
		203.0.113.0/24 (TEST-NET-3)
		*/
		assertFalse( NetUtils.isReachableByPing( "192.0.2.100" ) );
		assertFalse( NetUtils.isReachableByPing( "0.0.0.0" ) );
	}
}