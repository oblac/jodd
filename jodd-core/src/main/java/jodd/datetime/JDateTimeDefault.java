// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import jodd.datetime.format.Iso8601JdtFormatter;
import jodd.datetime.format.JdtFormatter;

import java.util.TimeZone;
import java.util.Locale;

/**
 * Defaults for {@link jodd.datetime.JDateTime}.
 */
@SuppressWarnings({"RedundantFieldInitialization"})
public class JDateTimeDefault {

	/**
	 * Default value for month fix.
	 */
	public static boolean monthFix = true;

	/**
	 * Default time zone. Set it to <code>null</code>
	 * for system default timezone.
	 */
	public static TimeZone timeZone = null;		// system default

	/**
	 * Default locale for date names. Set it to <code>null</code>
	 * for system default locale.
	 */
	public static Locale locale = null;			// system default

	/**
	 * Default format template.
	 */
	public static String format = JDateTime.DEFAULT_FORMAT;

	/**
	 * Default formatter.
	 */
	public static JdtFormatter formatter = new Iso8601JdtFormatter();

	/**
	 * Default definition of first day of week.
	 */
	public static int firstDayOfWeek = JDateTime.MONDAY;

	/**
	 * Default number of days first week of year must have.
	 */
	public static int mustHaveDayOfFirstWeek = 4;

	/**
	 * Default minimal number of days firs week of year must have.
	 */
	public static int minDaysInFirstWeek = 4;

	/**
	 * Default value for tracking DST.
	 */
	public static boolean trackDST = false;
}
