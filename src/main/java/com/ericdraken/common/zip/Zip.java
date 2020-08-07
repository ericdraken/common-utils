/*
 * Copyright (c) 2020. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.zip;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static com.ericdraken.common.io.Folders.makeParentFolders;

public class Zip
{
	private static final int EOF = -1;

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	/**
	 * Zip a file
	 *
	 * @param sourcePath
	 * @param outPath
	 * @param internalTargetPath
	 * @throws IOException
	 */
	public static void zipFile( Path sourcePath, Path outPath, String internalTargetPath ) throws IOException
	{
		zipFile( sourcePath, outPath, internalTargetPath, ZipEntry.DEFLATED );
	}

	/**
	 * Zip a file
	 *
	 * @param sourcePath
	 * @param outPath
	 * @param internalTargetPath
	 * @throws IOException
	 */
	public static void zipFile( Path sourcePath, Path outPath, String internalTargetPath, int method ) throws IOException
	{
		// Make the folders if not present
		makeParentFolders( outPath.toFile() );

		try (
			FileOutputStream fos = new FileOutputStream( outPath.toString() );
			ZipOutputStream zipOut = new ZipOutputStream( fos );
			FileInputStream fis = new FileInputStream( sourcePath.toString() )
		)
		{
			ZipEntry zipEntry = new ZipEntry( internalTargetPath );
			zipEntry.setLastModifiedTime( FileTime.fromMillis( sourcePath.toFile().lastModified() ) );
			zipEntry.setMethod( method );
			zipOut.putNextEntry( zipEntry );

			int n;
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			while ( EOF != (n = fis.read( buffer )) )
			{
				zipOut.write( buffer, 0, n );
			}
		}
	}

	/**
	 * Unzip a file
	 *
	 * @param zipFile
	 * @param internalSourcePath
	 * @param outputFile
	 * @param overwrite
	 * @throws IOException
	 */
	public static void unzipFile(
		Path zipFile,
		Path internalSourcePath,
		Path outputFile,
		boolean overwrite
	) throws IOException
	{
		// Wrap the file system in a try-with-resources statement
		// to auto-close it when finished and prevent a memory leak
		try ( FileSystem fileSystem = FileSystems.newFileSystem( zipFile, null ) )
		{
			Path fileToExtract = fileSystem.getPath( internalSourcePath.toString() );
			Files.copy(
				fileToExtract,
				outputFile,
				overwrite ? StandardCopyOption.REPLACE_EXISTING : null
			);
		}
	}

	/**
	 * Get a zip entry from an archive
	 *
	 * @param zipFile
	 * @param internalSourcePath
	 * @return
	 * @throws IOException
	 */
	public static ZipEntry getZipEntry( Path zipFile, Path internalSourcePath ) throws IOException
	{
		try ( ZipFile file = new ZipFile( zipFile.toString() ) )
		{
			return file.getEntry( internalSourcePath.toString() );
		}
	}

	/**
	 * Get a zip entry from an archive
	 *
	 * @param zipFile
	 * @param internalSourcePath
	 * @return
	 * @throws IOException
	 */
	public static ZipEntry getZipEntry( Path zipFile, String internalSourcePath ) throws IOException
	{
		try ( ZipFile file = new ZipFile( zipFile.toString() ) )
		{
			return file.getEntry( internalSourcePath );
		}
	}
}
