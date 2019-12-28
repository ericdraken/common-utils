/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.utils.functional;

@FunctionalInterface
public interface CheckedBiFunction<T, U, R>
{
	R apply( T t, U u ) throws Exception;
}