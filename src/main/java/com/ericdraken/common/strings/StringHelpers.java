/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.strings;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.List;

public class StringHelpers
{
	public static final int TAB_SPACES = 4;

	private StringHelpers()
	{
	}

	public static <T> String listToString( @Nonnull List<T> list, @Nonnull String header )
	{
		StringBuilder res = new StringBuilder();
		res.append( header );
		res.append( "{" );
		res.append( System.lineSeparator() );
		for ( T order : list )
		{
			res.append( "    " );
			res.append( order );
			res.append( System.lineSeparator() );
		}
		res.append( "}" );
		res.append( System.lineSeparator() );
		return res.toString();
	}

	public static <T> String listToStringWithBrackets( @Nonnull List<T> list, int indentTabs )
	{
		StringBuilder res = new StringBuilder();
		res.append( StringUtils.repeat( " ", TAB_SPACES * indentTabs ) );
		res.append( "{" );
		res.append( System.lineSeparator() );
		for ( T order : list )
		{
			res.append( StringUtils.repeat( " ", TAB_SPACES + (TAB_SPACES * indentTabs) ) );
			res.append( order );
			res.append( System.lineSeparator() );
		}
		res.append( StringUtils.repeat( " ", TAB_SPACES * indentTabs ) );
		res.append( "}" );
		return res.toString();
	}

	public static <T> String listToStringNoBrackets( @Nonnull List<T> list, int indentTabs )
	{
		StringBuilder res = new StringBuilder();
		for ( T order : list )
		{
			res.append( StringUtils.repeat( " ", TAB_SPACES * indentTabs ) );
			res.append( order );
			res.append( System.lineSeparator() );
		}
		return res.toString();
	}
}
