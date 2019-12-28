/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.functional;

@FunctionalInterface
public interface CheckedFunction<T, R>
{
	R apply( T t ) throws Exception;
}
