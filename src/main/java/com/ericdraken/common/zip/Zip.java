/*
 * Copyright (c) 2020. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.zip;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static com.ericdraken.common.io.Folders.makeParentFolders;

public class Zip
{
	private static final int EOF = -1;

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	private Zip()
	{
	}

	/**
	 * Zip a file to an archive and replace the archive completely. Use this method
	 * to zip a a single file only as it will not append.
	 *
	 * @param sourcePath Source file to zip
	 * @param outPath Destination zip file
	 * @param internalTargetPath Internal path
	 * @throws IOException
	 */
	public static void zipFile( Path sourcePath, Path outPath, String internalTargetPath ) throws IOException
	{
		zipFile( sourcePath, outPath, internalTargetPath, ZipEntry.DEFLATED );
	}

	/**
	 * Zip a file to an archive and replace the archive completely. Use this method
	 * to zip a a single file only as it will not append.
	 *
	 * @param sourcePath Source file to zip
	 * @param outPath Destination zip file
	 * @param internalTargetPath Internal path
	 * @param method Zip method
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

	/**
	 * Example:
	 * Path toBeAdded = FileSystems.getDefault().getPath("a.txt").toAbsolutePath();
	 * addFile(zipLocation, toBeAdded, "aa/aa.txt");
	 *
	 * @param zipfs
	 * @param toBeAdded
	 * @param internalPath
	 * REF: https://stackoverflow.com/a/14733863/1938889
	 * REF: http://www.pixeldonor.com/2013/oct/12/concurrent-zip-compression-java-nio/
	 */
	public static boolean addFile( final FileSystem zipfs, final Path toBeAdded, final String internalPath ) throws IOException
	{
		// Copy input file to ZipFileSystem
		File inFile = toBeAdded.toFile();
		try ( FileInputStream in = new FileInputStream( inFile ) )
		{
			// Create internal path in the zipfs
			Path internalTargetPath = zipfs.getPath( internalPath );

			// Check if the file size is the same
			if ( Files.exists( internalTargetPath ) )
			{
				final long zippedSize = (long) Files.getAttribute( internalTargetPath, "zip:size" );
				if ( zippedSize == inFile.length() )
				{
					// No need to replace it
					return false;
				}
			}

			// Create output stream into the zipfs
			try ( OutputStream out = Files.newOutputStream( internalTargetPath ) )
			{
				// Copy the source file to the Zip FS
				IOUtils.copy( in, out );
			}
			catch ( IOException e )
			{
				// Create the parent folder(s) on exception
				Files.createDirectories( internalTargetPath.getParent() );
				try ( OutputStream out = Files.newOutputStream( internalTargetPath ) )
				{
					// Copy the source file to the Zip FS
					IOUtils.copy( in, out );
				}
			}
		}
		return true;
	}

	/**
	 * Create a new zip file system that is autocloseable
	 *
	 * @param zipLocation The resultant zip file
	 * @return A new FileSystem
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static FileSystem createZipFileSystem( final Path zipLocation ) throws IOException, URISyntaxException
	{
		Map<String, Object> env = new HashMap<>();

		// Check if file exists
		env.put( "create", String.valueOf( Files.notExists( zipLocation ) ) );

		// To prevent heap memory errors
		// REF: https://stackoverflow.com/a/23861949/1938889
		env.put( "useTempFile", Boolean.TRUE );

		// Use a Zip filesystem URI
		URI fileUri = zipLocation.toUri();
		URI zipUri = new URI( "jar:" + fileUri.getScheme(), fileUri.getPath(), null );

		return FileSystems.newFileSystem( zipUri, env );
	}
}
