/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.collections;

public class Holder<T> {
    private T value;

    public Holder( T value ) {
        setValue( value );
    }

    public T getValue() {
        return value;
    }

    public void setValue( T value ) {
        this.value = value;
    }
}
