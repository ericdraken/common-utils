/*
 * Copyright (c) 2019. Eric Draken - ericdraken.com
 */

package com.ericdraken.common.strings;

public class HumanReadable
{
	private HumanReadable()
	{
	}

	/**
	 * REF: https://stackoverflow.com/a/3758880/1938889
	 *
	 * @param bytes
	 * @param si
	 * @return
	 */
	public static String humanReadableByteCount( long bytes, boolean si )
	{
		int unit = si ? 1000 : 1024;
		if ( bytes < unit )
		{
			return bytes + " B";
		}
		int exp = (int) (Math.log( bytes ) / Math.log( unit ));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt( exp - 1 ) + (si ? "" : "i");
		return String.format( "%.1f %sB", bytes / Math.pow( unit, exp ), pre );
	}

	/**
	 * Return a human-readable duration
	 *
	 * @param durationSeconds
	 * @return
	 */
	public static String humanReadableDuration( long durationSeconds )
	{
		long minutesInSeconds = 60;
		long hoursInSeconds = minutesInSeconds * 60;
		long daysInSeconds = hoursInSeconds * 24;
		long yearsInSeconds = Math.round( daysInSeconds * 365.25 );

		long elapsedYears = durationSeconds / yearsInSeconds;
		durationSeconds = durationSeconds % yearsInSeconds;

		long elapsedDays = durationSeconds / daysInSeconds;
		durationSeconds = durationSeconds % daysInSeconds;

		long elapsedHours = durationSeconds / hoursInSeconds;
		durationSeconds = durationSeconds % hoursInSeconds;

		long elapsedMinutes = durationSeconds / minutesInSeconds;
		durationSeconds = durationSeconds % minutesInSeconds;

		long elapsedSeconds = durationSeconds;

		return String.format(
			"%d years, %d days, %d hours, %d minutes, %d seconds",
			elapsedYears, elapsedDays,
			elapsedHours, elapsedMinutes, elapsedSeconds
		);
	}
}
