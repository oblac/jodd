// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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
