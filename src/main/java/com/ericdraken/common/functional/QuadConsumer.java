/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.functional;

@FunctionalInterface
public interface QuadConsumer<K, V, S, T> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param k the first input argument
     * @param v the second input argument
     * @param s the third input argument
     * @param t the fourth input argument
     */
    void accept( K k, V v, S s, T t );
}
