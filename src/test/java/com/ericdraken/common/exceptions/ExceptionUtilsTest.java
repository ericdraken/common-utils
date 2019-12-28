/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.exceptions;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionUtilsTest
{
	@Test
	void exceptionMessageTest()
	{
		try {
			throw new NullPointerException( "lala" );
		}
		catch ( Exception e )
		{
			String str = (ExceptionUtils.getMessage( e ) + ExceptionUtils.getStackFrames( e )[1] );
			System.out.println( str );
			assertEquals( str, com.ericdraken.common.exceptions.ExceptionUtils.getMessage( e ) );
		}
	}
}
