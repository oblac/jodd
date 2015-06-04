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

import java.util.TimeZone;

/**
 * Misc timezone utilities.
 */
public class TimeZoneUtil {

	/**
	 * Returns raw offset difference in milliseconds.
	 */
	public static int getRawOffsetDifference(TimeZone from, TimeZone to) {
		int offsetBefore = from.getRawOffset();
		int offsetAfter = to.getRawOffset();
		return offsetAfter - offsetBefore;
	}

	/**
	 * Returns offset difference  in milliseconds for given time.
	 */
	public static int getOffsetDifference(long now, TimeZone from, TimeZone to) {
		int offsetBefore = from.getOffset(now);
		int offsetAfter = to.getOffset(now);
		return offsetAfter - offsetBefore;
	}

	/**
	 * Get offset difference in milliseconds for given jdatetime.
	 */
	public static int getOffset(JDateTime jdt, TimeZone tz) {
		return tz.getOffset(
				jdt.getEra(),
				jdt.getYear(),
				jdt.getMonth() - 1,
				jdt.getDay(),
				TimeUtil.toCalendarDayOfWeek(jdt.getDayOfWeek()),
				jdt.getMillisOfDay()
		);
	}

	public static int getOffsetDifference(JDateTime jdt, TimeZone from, TimeZone to) {
		int offsetBefore = getOffset(jdt, from);
		int offsetAfter = getOffset(jdt, to);
		return offsetAfter - offsetBefore;
	}
}
