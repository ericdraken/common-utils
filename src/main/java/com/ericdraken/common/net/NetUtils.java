package com.ericdraken.common.net;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ericdraken.common.exceptions.ExceptionUtils.getMessage;

public class NetUtils
{
	private static final Logger logger = LoggerFactory.getLogger( MethodHandles.lookup().lookupClass() );

	private static final String WAN_REACHABLE_TEST_IP = "1.1.1.1";

	private static final ArrayList<String> DEFAULT_IP_REPORTER_URLS = new ArrayList<>( List.of(
		"https://ipinfo.io/ip",
		"https://bot.whatismyipaddress.com"
	) );

	private NetUtils()
	{
	}

	/**
	 * Get the external IP of the system, or return null on failure
	 *
	 * @return InetAddress or null
	 * @throws IOException Any IO exception
	 */
	public static InetAddress getPublicIp() throws IOException
	{
		return getPublicIp( null, null, null );
	}

	/**
	 * Get the external IP of the system, or return null on failure
	 *
	 * @param ipReporters IP reporter service URLs
	 * @return InetAddress or null
	 * @throws IOException Any IO exception
	 */
	public static InetAddress getPublicIp(
		@Nullable List<String> ipReporters
	) throws IOException
	{
		return getPublicIp( null, null, ipReporters );
	}

	/**
	 * Get the external IP of the system, or return null on failure
	 *
	 * @param proxyHost Proxy server host
	 * @param proxyPort Proxy server port
	 * @return InetAddress or null
	 * @throws IOException Any IO exception
	 */
	public static InetAddress getPublicIp(
		@Nonnull String proxyHost,
		@Nonnegative int proxyPort
	) throws IOException
	{
		return getPublicIp( proxyHost, proxyPort, null, null );
	}

	/**
	 * Get the external IP of the system, or return null on failure
	 *
	 * @param proxyHost Proxy server host
	 * @param proxyPort Proxy server port
	 * @param ipReporters IP reporter service URLs
	 * @return InetAddress or null
	 * @throws IOException Any IO exception
	 */
	public static InetAddress getPublicIp(
		@Nonnull String proxyHost,
		@Nonnegative int proxyPort,
		@Nullable List<String> ipReporters
	) throws IOException
	{
		return getPublicIp( proxyHost, proxyPort, null, ipReporters );
	}

	/**
	 * Get the external IP of the system, or return null on failure
	 *
	 * @param proxyHost   Proxy server host
	 * @param proxyPort   Proxy server port
	 * @param vpnCookie   VPN affinity cookie or null
	 * @param ipReporters IP reporter service URLs
	 * @return InetAddress or null
	 * @throws IOException Any IO exception
	 */
	public static InetAddress getPublicIp(
		@Nonnull String proxyHost,
		@Nonnegative int proxyPort,
		@Nullable String vpnCookie,
		@Nullable List<String> ipReporters
	) throws IOException
	{
		final var proxy = new Proxy( Proxy.Type.HTTP, new InetSocketAddress( proxyHost, proxyPort ) );
		return getPublicIp( proxy, vpnCookie, ipReporters );
	}

	/**
	 * Get the external IP of the system, or return null on failure
	 *
	 * @param proxy Proxy server
	 * @param vpnCookie VPN affinity cookie or null
	 * @param ipReporters IP reporter service URLs
	 * @return InetAddress or null
	 * @throws IOException Any IO exception
	 */
	public static InetAddress getPublicIp(
		@Nullable Proxy proxy,
		@Nullable String vpnCookie,
		@Nullable List<String> ipReporters
	) throws IOException
	{
		ArrayList<String> urls = new ArrayList<>( ipReporters == null ? DEFAULT_IP_REPORTER_URLS : ipReporters );
		Collections.shuffle( urls );

		for ( String urlStr : urls )
		{
			final var url = new URL( urlStr );    // Get external IP
			HttpURLConnection conn;

			if ( proxy != null )
			{
				conn = (HttpURLConnection) url.openConnection( proxy );
			}
			else
			{
				conn = (HttpURLConnection) url.openConnection();
			}

			if ( vpnCookie != null )
			{
				conn.addRequestProperty( "Cookie", vpnCookie );
			}

			try
			{
				conn.setInstanceFollowRedirects( true );
				conn.setRequestMethod( "GET" );
				conn.setConnectTimeout( 10_000 );
				conn.setReadTimeout( 10_000 );
				conn.connect();

				int responseCode = conn.getResponseCode();
				if ( responseCode != HttpURLConnection.HTTP_OK )
				{
					logger.warn( "External IP service {} had response code: {}", urlStr, responseCode );
					continue;
				}

				String res = "-";
				try ( BufferedReader br = new BufferedReader( new InputStreamReader( conn.getInputStream() ) ) ) // NOSONAR
				{
					res = br.readLine().trim();
					return InetAddress.getByName( res );
				}
				catch ( IOException e )
				{
					logger.warn( "Trouble extracting IP from {}, response: {}, exception {}", urlStr, res, getMessage( e ) );
				}
			}
			catch ( Exception e )
			{
				logger.warn( "Trouble with URL {}, exception {}", urlStr, getMessage( e ) );
			}
		}
		return null;
	}

	/**
	 * Ping the Cloudflare nameserver to test network connectivity
	 *
	 * @return True if yes
	 */
	public static boolean isNetworkReachable()
	{
		return isNetworkReachable( WAN_REACHABLE_TEST_IP );
	}

	/**
	 * Check if an address is reachable by ping
	 *
	 * @param ipAddress IP address
	 * @return True if yes
	 */
	public static boolean isNetworkReachable( String ipAddress )
	{
		try
		{
			InetAddress inet = InetAddress.getByName( ipAddress );
			return inet.isReachable( 10_000 );
		}
		catch ( UnknownHostException e )
		{
			logger.error( e.getMessage() );
			return false;
		}
		catch ( IOException e )
		{
			try
			{
				return isReachableByPing( ipAddress );
			}
			catch ( IOException e1 )
			{
				logger.error( e1.getMessage() );
				return false;
			}
		}
	}

	/**
	 * @param ipAddress The internet protocol address to ping
	 * @return True if the address is responsive, false otherwise
	 * @see [https://stackoverflow.com/questions/11506321/how-to-ping-an-ip-address]
	 */
	static boolean isReachableByPing( String ipAddress ) throws IOException
	{
		List<String> command = buildPingCommand( ipAddress );
		ProcessBuilder processBuilder = new ProcessBuilder( command );
		Process process = processBuilder.start();

		try ( BufferedReader standardOutput = new BufferedReader( new InputStreamReader( process.getInputStream() ) ) )
		{
			String outputLine;

			while ( (outputLine = standardOutput.readLine()) != null )
			{
				// Picks up Windows and Unix unreachable hosts
				if ( outputLine.toLowerCase().contains( "destination host unreachable" ) )
				{
					logger.warn( "{} unreachable", ipAddress );
					return false;
				}

				if ( outputLine.toLowerCase().contains( "timed out" ) )
				{
					logger.warn( "{} timed out", ipAddress );
					return false;
				}

				if ( outputLine.toLowerCase().contains( "100% loss" ) )
				{
					logger.warn( "{} 100% packet loss", ipAddress );
					return false;
				}
			}
		}

		return true;
	}

	private static List<String> buildPingCommand( String ipAddress )
	{
		List<String> command = new ArrayList<>();
		command.add( "ping" );

		if ( SystemUtils.IS_OS_WINDOWS )
		{
			command.add( "-n" );
		}
		else if ( SystemUtils.IS_OS_UNIX )
		{
			command.add( "-c" );
		}
		else
		{
			throw new UnsupportedOperationException( "Unsupported operating system" );
		}

		command.add( "1" );
		command.add( ipAddress );

		return command;
	}
}
