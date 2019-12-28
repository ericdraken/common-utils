/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.collections;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class RandomCollectionTest
{
	@Test
	void add_None()
	{
		// Add the job supplier to the collection
		RandomCollection<Integer> collection = new RandomCollection<>();
		assertEquals( 0, collection.getNumInitialElements() );
	}

	@Test
	void add_Three()
	{
		// Add the job supplier to the collection
		RandomCollection<Integer> collection = new RandomCollection<>();
		collection.add( 1, 1.0, 1 );
		collection.add( 1, 1.0, 2 );
		collection.add( 1, 1.0, 3 );
		assertEquals( 3, collection.getNumInitialElements() );
	}

	@Test
	void next()
	{
		// Add the job supplier to the collection
		RandomCollection<Integer> collection = new RandomCollection<>();
		collection.add( 1, 1.0, 1 );

		// Infinite next()
		assertEquals( 1, Objects.requireNonNull( collection.next() ).intValue() );
		assertEquals( 1, Objects.requireNonNull( collection.next() ).intValue() );
		assertEquals( 1, Objects.requireNonNull( collection.next() ).intValue() );
	}

	@Test
	void remove()
	{
		// Add the job supplier to the collection
		RandomCollection<Integer> collection = new RandomCollection<>();

		collection.add( 1, 1.0, 1 );
		assertEquals( 1, Objects.requireNonNull( collection.next() ).intValue() );

		assertTrue( collection.remove( 1 ) );
		assertNull( collection.next() );
	}

	@Test
	void remove_NotPresent()
	{
		// Add the job supplier to the collection
		RandomCollection<Integer> collection = new RandomCollection<>();
		assertFalse( collection.remove( 1 ) );
	}

	@Test
	void remove_Several()
	{
		// Add the job supplier to the collection
		RandomCollection<Integer> collection = new RandomCollection<>();

		collection.add( 1, 1.0, 1 );
		collection.add( 1, 1.0, 2 );
		collection.add( 1, 1.0, 3 );

		assertTrue( collection.remove( 1 ) );
		assertTrue( collection.remove( 2 ) );
		assertEquals( 3, Objects.requireNonNull( collection.next() ).intValue() );

		assertTrue( collection.remove( 3 ) );
		assertNull( collection.next() );
	}

	@Test
	void size()
	{
		// Add the job supplier to the collection
		RandomCollection<Integer> collection = new RandomCollection<>();
		assertEquals( 0, collection.size() );
	}

	@Test
	void size_Three()
	{
		// Add the job supplier to the collection
		RandomCollection<Integer> collection = new RandomCollection<>();
		collection.add( 1, 1.0, 1 );
		collection.add( 1, 1.0, 2 );
		collection.add( 1, 1.0, 3 );
		assertEquals( 3, collection.size() );
	}

	@Test
	void getNumInitialElements()
	{
		// Add the job supplier to the collection
		RandomCollection<Integer> collection = new RandomCollection<>();
		collection.add( 1, 1.0, 1 );
		collection.add( 1, 1.0, 2 );
		assertTrue( collection.remove( 1 ) );

		// Still 2, not 1
		assertEquals( 2, collection.getNumInitialElements() );
	}
}