package com.ericdraken.common.zip;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.*;

public class ZipTest
{
	@Test
	public void zipFileTest() throws IOException
	{
		File tempIn = File.createTempFile("tempIn", ".txt");
		tempIn.deleteOnExit();

		File tempOut = File.createTempFile("tempOut", ".zip");
		tempOut.deleteOnExit();

		File tempCheck = File.createTempFile("tempCheck", ".txt");
		tempCheck.deleteOnExit();

		// Write a compressible string
		try ( PrintStream out = new PrintStream( new FileOutputStream( tempIn ) ) )
		{
			out.print( "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" );
		}

		// Update the modified time (ms)
		assertTrue( tempIn.setLastModified( 123 * 1000 ) );

		Path internalPath = relativize( tempIn, tempIn );

		// The primary method under test
		Zip.zipFile( tempIn.toPath(), tempOut.toPath(), internalPath.toString() );

		// Wrap the file system in a try-with-resources statement
		// to auto-close it when finished and prevent a memory leak
		try ( FileSystem fileSystem = FileSystems.newFileSystem( tempOut.toPath(), null ) )
		{
			Path fileToExtract = fileSystem.getPath( internalPath.toString() );
			Files.copy( fileToExtract, tempCheck.toPath(), StandardCopyOption.REPLACE_EXISTING );
		}

		// Compare contents
		assertEquals(
			FileUtils.readFileToString( tempIn, StandardCharsets.US_ASCII ),
			FileUtils.readFileToString( tempCheck, StandardCharsets.US_ASCII ),
			"The files differ!"
		);

		// Compare modified times
		try (ZipFile zipFile = new ZipFile( tempOut ))
		{
			ZipEntry entry = zipFile.getEntry( internalPath.toString() );
			assertTrue( tempIn.lastModified() > 0 );
			assertEquals( tempIn.lastModified(), entry.getLastModifiedTime().toMillis() );
		}

		///////

		File tempCheck2 = File.createTempFile("tempCheck2", ".txt");
		tempCheck.deleteOnExit();

		// Unzip the file
		Zip.unzipFile( tempOut.toPath(), internalPath, tempCheck2.toPath(), true );

		// Compare contents
		assertEquals(
			FileUtils.readFileToString( tempIn, StandardCharsets.US_ASCII ),
			FileUtils.readFileToString( tempCheck2, StandardCharsets.US_ASCII ),
			"The files differ!"
		);

		ZipEntry entry2 = Zip.getZipEntry( tempOut.toPath(), internalPath );
		assertEquals( tempIn.length(), entry2.getSize() );
	}

	@Test
	void zipFileTest_zeroLength() throws IOException
	{
		File zeroZip = File.createTempFile("zeroZip", ".zip");
		zeroZip.deleteOnExit();

		try
		{
			Zip.getZipEntry( zeroZip.toPath(), "not exists" );
			fail( "Should be an exception" );
		}
		catch ( ZipException e )
		{
			// TODO: Something...
		}
	}

	private Path relativize( final File base, final File file )
	{
		String srcPath = base.getParentFile().getAbsolutePath();
		final int rootLength = srcPath.length();
		return Paths.get( file.getAbsolutePath().substring( rootLength + 1 ) );
	}
}