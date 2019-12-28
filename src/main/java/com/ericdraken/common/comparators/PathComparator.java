/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.comparators;

import java.io.File;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.regex.Pattern;

// REF: http://www.java2s.com/Tutorials/Java/Data_Structure_How_to/Sort/Sort_file_path.htm
public class PathComparator implements Comparator<Path>
{
	private static String regex = Pattern.quote( File.separator );

	public int compare( Path path1, Path path2 )
	{
		String[] dirs1 = path1.normalize().toString().split( regex );
		String[] dirs2 = path2.normalize().toString().split( regex );
		if ( dirs1.length < dirs2.length )
		{
			return -1;
		}
		else if ( dirs1.length > dirs2.length )
		{
			return 1;
		}
		else
		{
			String lastDir1 = dirs1[dirs1.length - 1];
			String lastDir2 = dirs2[dirs2.length - 1];
			return lastDir1.compareTo( lastDir2 );
		}
	}
}
