/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.collections;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

// REF: https://stackoverflow.com/a/6409791/1938889
// Improved by Eric Draken
public class RandomCollection<E>
{
	private final TreeMap<Double, E> map = new TreeMap<>();
	private final HashMap<E, Double> hashMap = new HashMap<>();
	private final Random random;
	private double total = 0;
	private int numInitialElements = 0;

	public RandomCollection()
	{
		this( new Random() );
	}

	private RandomCollection( Random random )
	{
		this.random = random;
	}

	/**
	 * Allow extended classes to access this collection
	 * @return Map
	 */
	protected final Map<Double, E> getMap()
	{
		return map;
	}

	public final synchronized RandomCollection<E> add( long numElements, double weight, E result )
	{
		if ( weight <= 0.00001 )
		{
			return this;
		}
		if ( numElements <= 0 )
		{
			return this;
		}

		// Save a list of the added values for when one is removed
		hashMap.put( result, weight );

		numInitialElements += numElements;
		total += weight;
		map.put( total, result );
		return this;
	}

	public final synchronized E next()
	{
		double value = random.nextDouble() * total;
		Map.Entry<Double, E> val = map.higherEntry( value );
		return val != null ? val.getValue() : null;
	}

	public final synchronized boolean remove( E e )
	{
		// Rebuild the collection
		if ( e != null && map.containsValue( e ) )
		{
			hashMap.remove( e );
			map.clear();
			total = 0;
			hashMap.forEach( ( result, weight ) -> {
				total += weight;
				map.put( total, result );
			} );
			return true;
		}
		return false;
	}

	public final synchronized long size()
	{
		return map.size();
	}

	/**
	 * Return the number of initial elements in the collection
	 *
	 * @return int
	 */
	public final int getNumInitialElements()
	{
		return numInitialElements;
	}
}
