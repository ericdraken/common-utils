/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.exceptions;

public class ExceptionUtils
{
	private ExceptionUtils()
	{
	}

	public static String getMessage( Throwable e )
	{
		// Return something like:
		// NullPointerException: lala	at com.ericdraken.questrade.utils.ExceptionUtilsTest.exceptionMessageTest(ExceptionUtilsTest.java:15)
		String[] frames = org.apache.commons.lang3.exception.ExceptionUtils.getStackFrames( e );
		return org.apache.commons.lang3.exception.ExceptionUtils.getMessage( e ) + (frames.length > 1 ? frames[1] : "");
	}
}
