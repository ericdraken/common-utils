package com.ericdraken.common.io;

import java.io.File;
import java.io.IOException;

public class Folders
{
	private Folders()
	{
	}

	public static void makeParentFolders( File outFile ) throws IOException
	{
		if ( outFile.exists() )
			return;

		// Ensure the folder structure exists
		File parent = outFile.getParentFile();
		if ( parent != null && !parent.exists() )
		{
			parent.mkdirs();
			if ( !parent.exists() )
			{
				throw new IOException( "Couldn't create dir: " + parent );
			}
		}
	}
}
