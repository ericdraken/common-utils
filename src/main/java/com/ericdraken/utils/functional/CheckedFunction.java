/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.utils.functional;

@FunctionalInterface
public interface CheckedFunction<T, R>
{
	R apply( T t ) throws Exception;
}
