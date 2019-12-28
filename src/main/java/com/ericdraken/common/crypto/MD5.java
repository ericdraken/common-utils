/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.crypto;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class MD5
{
	private MD5()
	{
	}

	/**
	 * Calculate the hexadecimal string MD5 of a file
	 *
	 * @param file Input file
	 * @return Hexadecimal MD5 representation
	 * @throws IOException IO problems
	 */
	public static String calcMD5Hex( File file ) throws IOException
	{
		try ( InputStream is = Files.newInputStream( file.toPath() ) )
		{
			// Calculate the hex MD5 string
			return DigestUtils.md5Hex( is );
		}
	}

	/**
	 * Calculate the base64 string MD5 of a file
	 *
	 * @param file Input file
	 * @return Base64 MD5 representation
	 * @throws IOException IO problems
	 */
	public static String calcMD5Base64( File file ) throws IOException
	{
		try ( InputStream is = Files.newInputStream( file.toPath() ) )
		{
			// Calculate the MD5 ourselves and encode is as base64 for AWS to check
			return new String(Base64.encodeBase64( DigestUtils.md5( is ) ));
		}
	}
}
