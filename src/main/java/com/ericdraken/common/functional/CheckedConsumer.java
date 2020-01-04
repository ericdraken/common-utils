/*
 * Copyright (c) 2020. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.functional;

@FunctionalInterface
public interface CheckedConsumer<T>
{
	void apply( T t ) throws Exception;
}
