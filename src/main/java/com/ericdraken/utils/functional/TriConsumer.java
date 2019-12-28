/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.utils.functional;

@FunctionalInterface
public interface TriConsumer<K, V, S> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param k the first input argument
     * @param v the second input argument
     * @param s the third input argument
     */
    void accept( K k, V v, S s );
}

