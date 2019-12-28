/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.exceptions;

public class ExceptionUtils
{
	private ExceptionUtils()
	{
	}

	/**
	 * Return a detailed exception message from Apache Commons, and
	 * add the stack trace at the first code point of com.ericdraken
	 * @param e Throwable
	 * @return Nicely-formatted exception string
	 */
	public static String getMessage( Throwable e )
	{
		String[] frames = org.apache.commons.lang3.exception.ExceptionUtils.getStackFrames( e );
		var msg = org.apache.commons.lang3.exception.ExceptionUtils.getMessage( e );

		for ( String frame : frames )
		{
			if ( frame.contains( "com.ericdraken" ) )
			{
				return msg + ", " + frame.trim();
			}
		}
		return msg;
	}
}
