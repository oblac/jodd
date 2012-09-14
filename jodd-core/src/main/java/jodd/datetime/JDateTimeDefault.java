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

	public static boolean monthFix = true;

	public static TimeZone timeZone = null;		// system default

	public static Locale locale = null;			// system default

	public static String format = JDateTime.DEFAULT_FORMAT;

	public static JdtFormatter formatter = new Iso8601JdtFormatter();

	public static int firstDayOfWeek = JDateTime.MONDAY;

	public static int mustHaveDayOfFirstWeek = 4;

	public static int minDaysInFirstWeek = 4;

	public static boolean trackDST = false;
}
