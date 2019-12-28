/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.collections;

import java.util.Map;

public abstract class NullableMap
{
	private NullableMap()
	{
	}

	public static <K, V> Map<K, V> of( K k1, V v1)
	{
		if ( k1 == null || v1 == null )
			return null;

		return Map.of( k1, v1 );
	}
}
