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

import jodd.datetime.format.JdtFormat;
import jodd.datetime.format.JdtFormatter;
import jodd.util.HashCode;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static jodd.util.HashCode.hash;

/**
 * Universal all-in-one date and time class that uses Astronomical Julian
 * Dates for time calculations. Guaranteed precision for all manipulations
 * and calculations is up to 1 ms (0.001 sec).
 *
 * <p>
 * The Julian day or Julian day number (JDN) is the (integer) number of days that
 * have elapsed since Monday, January 1, 4713 BC in the proleptic Julian calendar 1.
 * That day is counted as Julian day zero. Thus the multiples of 7 are Mondays.
 * Negative values can also be used.
 *
 * <p>
 * The Julian Date (JD) is the number of days (with decimal fraction of the day) that
 * have elapsed since 12 noon Greenwich Mean Time (UT or TT) of that day.
 * Rounding to the nearest integer gives the Julian day number.
 *
 * <p>
 * <code>JDateTime</code> contains date/time information for current day. By
 * default, behaviour and formats are set to ISO standard, although this may
 * be changed.
 *
 * <p>
 * <code>JDateTime</code> can be set in many different ways by using <code>setXxx()</code>
 * methods or equivalent constructors. Moreover, date time information may be loaded from an instance
 * of any available java date-time class. This functionality can be easily
 * enhanced for any custom date/time class. Furthermore, <code>JDateTime</code>
 * can be converted to any such date/time class.
 *
 * <p>
 * Rolling dates with <code>JDateTime</code> is easy. For this
 * <code>JDateTime</code> contains many <code>addXxx()</code> methods. Time can be added
 * or subtracted with any value or more values at once. All combinations are
 * valid. Calculations also performs month fixes by default.
 *
 * <p>
 * <code>JDateTime</code> behaviour is set by several attributes (or
 * parameters). Each one contains 2 values: one is the default value, used by
 * all instances of <code>JDateTime</code> and the other one is just for a
 * specific instance of <code>JDateTime</code>. This means that it is
 * possible to set behaviour of all instances at once or of one particular
 * instance.
 *
 * <p>
 * Bellow is the list of behaviour attributes:
 *
 * <ul>
 *
 * <li>monthFix - since months do not have the same number of days, adding
 * months and years may be calculated in two different ways: with or
 * without month fix. when month fix is on, <code>JDateTime</code> will
 * additionally fix all time adding and fix the date. For example, adding
 * one month to 2003-01-31 will give 2003-02-28 and not 2003-03-03.
 * By default, monthFix is turned on and set to <code>true</code>.
 * </li>
 *
 * <li>locale - current locale, used for getting names during formatting the
 * output string.
 * </li>
 *
 * <li>timezone - current timezone</li>
 *
 * <li>format - is String that describes how time is converted and parsed to
 * and from a String. Default format matches ISO standard. An instance of
 * <code>JdtFormatter</code> parses and uses this template.</li>
 *
 * <li>week definition - is used for specifying the definition of the week.
 * Week is defined with first day of the week and with the must have day. A
 * must have day is a day that must exist in the 1st week of the year. For
 * example, default value is Thursday (4) as specified by ISO standard.
 * Alternatively, instead of must have day, minimal days of week may be used,
 * since this two numbers are in relation.
 * </li>
 *
 * </ul>
 *
 * Optimization: although based on heavy calculations, <code>JDateTime</code>
 * works significantly faster then java's <code>Calendar</code>s. Since
 * <code>JDateTime</code> doesn't use lazy initialization, setXxx() method is
 * slower. However, this doesn't have much effect to the global performances:
 * settings are usually not used without gettings:) As soon as any other method is
 * used (getXxx() or addXxx()) performances of <code>JDateTime</code> becomes
 * significantly better.
 *
 * <p>
 * Year 1582 is (almost:) working: after 1582-10-04 (Thursday) is 1582-10-15 (Friday).
 * Users must be aware of this when doing time rolling before across this period.
 *
 * <p>
 * More info: <a href="http://en.wikipedia.org/wiki/Julian_Date">Julian Date on Wikipedia</a>
 */
public class JDateTime implements Comparable, Cloneable, Serializable {

	public static final String DEFAULT_FORMAT = "YYYY-MM-DD hh:mm:ss.mss";

	// day of week names
	public static final int MONDAY 		= 1;
	public static final int TUESDAY 	= 2;
	public static final int WEDNESDAY 	= 3;
	public static final int THURSDAY 	= 4;
	public static final int FRIDAY 		= 5;
	public static final int SATURDAY 	= 6;
	public static final int SUNDAY 		= 7;

	// month names
	public static final int JANUARY		= 1;
	public static final int FEBRUARY	= 2;
	public static final int MARCH		= 3;
	public static final int APRIL		= 4;
	public static final int MAY			= 5;
	public static final int JUNE		= 6;
	public static final int JULY		= 7;
	public static final int AUGUST		= 8;
	public static final int SEPTEMBER	= 9;
	public static final int OCTOBER		= 10;
	public static final int NOVEMBER	= 11;
	public static final int DECEMBER	= 12;

	/**
	 * {@link DateTimeStamp} for current date.
	 */
	protected DateTimeStamp time = new DateTimeStamp();

	/**
	 * Day of week, range: [1-7] == [Monday - Sunday]
	 */
	protected int dayofweek;

	/**
	 * Day of year, range: [1-365] or [1-366]
	 */
	protected int dayofyear;

	/**
	 * Leap year flag.
	 */
	protected boolean leap;

	/**
	 * Week of year, range: [1-52] or [1-53]
	 */
	protected int weekofyear;

	/**
	 * Week of month.
	 */
	protected int weekofmonth;

	/**
	 * Current Julian Date.
	 */
	protected JulianDateStamp jdate;


	// ---------------------------------------------------------------- some precalculated times

	/**
	 * Julian Date for 1970-01-01T00:00:00 (Thursday).
	 */
	public static final JulianDateStamp JD_1970 = new JulianDateStamp(2440587, 0.5);

	/**
	 * Julian Date for 2001-01-01T00:00:00 (Monday).
	 */
	public static final JulianDateStamp JD_2001 = new JulianDateStamp(2451910, 0.5);

	// ---------------------------------------------------------------- julian date (CORE)

	/**
	 * Returns current {@link DateTimeStamp}.
	 * Returned instance is still used internally (i.e. it is not cloned before returning).
	 */
	public DateTimeStamp getDateTimeStamp() {
		return time;
	}

	/**
	 * Loads current date time information.
	 */
	public JDateTime setDateTimeStamp(DateTimeStamp dts) {
		return set(dts.year, dts.month, dts.day, dts.hour, dts.minute, dts.second, dts.millisecond);
	}

	/**
	 * Sets current Julian Date. This is the core of the JDateTime class and it
	 * is used by all other classes. This method performs all calculations
	 * required for whole class.
	 *
	 * @param jds    current julian date
	 */
	public JDateTime setJulianDate(JulianDateStamp jds) {
		setJdOnly(jds.clone());
		calculateAdditionalData();
		return this;
	}

	/**
	 * Returns {@link JulianDateStamp}. It is the same instance used internally.
	 */
	public JulianDateStamp getJulianDate() {
		return jdate;
	}

	/**
	 * Returns JDN. Note that JDN is not equal to integer part of julian date. It is calculated by
	 * rounding to the nearest integer.
	 */
	public int getJulianDayNumber() {
		return jdate.getJulianDayNumber();
	}

	/**
	 * Internal method for calculating various data other then date/time.
	 */
	private void calculateAdditionalData() {
		this.leap = TimeUtil.isLeapYear(time.year);
		this.dayofweek = calcDayOfWeek();
		this.dayofyear = calcDayOfYear();
		this.weekofyear = calcWeekOfYear(firstDayOfWeek, mustHaveDayOfFirstWeek);
		this.weekofmonth = calcWeekNumber(time.day, this.dayofweek);
	}

	/**
	 * Internal method that just sets the time stamp and not all other additional
	 * parameters. Used for faster calculations only and only by main core
	 * set/add methods.
	 *
	 * @param jds    julian date
	 */
	private void setJdOnly(JulianDateStamp jds) {
		jdate = jds;
		time = TimeUtil.fromJulianDate(jds);
	}

	/**
	 * Core method that sets date and time. All others set() methods use this
	 * one. Milliseconds are truncated after 3rd digit.
	 *
	 * @param year   year to set
	 * @param month  month to set
	 * @param day    day to set
	 * @param hour   hour to set
	 * @param minute minute to set
	 * @param second second to set
	 */
	public JDateTime set(int year, int month, int day, int hour, int minute, int second, int millisecond) {

		// fix seconds fractions because of float point arithmetic
		//second = ((int) second) + ((int) ((second - (int)second) * 1000 + 1e-9) / 1000.0);
/*
		double ms = (second - (int)second) * 1000;
		if (ms > 999) {
			ms = 999;
		} else {
			ms += 1.0e-9;
		}
		second = ((int) second) + ((int) ms / 1000.0);
*/
		jdate = TimeUtil.toJulianDate(year, month, day, hour, minute, second, millisecond);

		// if given time is valid it means that there is no need to calculate it
		// again from already calculated Julian date. however, it is still
		// necessary to fix milliseconds to match the value that would be
		// calculated as setJulianDate() is used. This fix only deals with the
		// time, not doing the complete and more extensive date calculation.
		// this means that method works faster when regular date is specified.
/*
		if (TimeUtil.isValidDateTime(year, month, day, hour, minute, second, millisecond)) {

			int ka = (int)(jdate.fraction + 0.5);
			double frac = jdate.fraction + 0.5 - ka + 1.0e-10;

			// hour with minute and second included as fraction
			double d_hour = frac * 24.0;

			// minute with second included as a fraction
			double d_minute = (d_hour - (int)d_hour) * 60.0;

			second = (d_minute - (int)d_minute) * 60.0;

			// fix calculation errors
			second = ((int) (second * 10000) + 0.5) / 10000.0;

			time.year = year; time.month = month; time.day = day;
			time.hour = hour; time.minute = minute; time.second = second;
			setParams();
		} else {
			setJulianDate(jdate);
		}
*/
		if (TimeUtil.isValidDateTime(year, month, day, hour, minute, second, millisecond)) {
			time.year = year; time.month = month; time.day = day;
			time.hour = hour; time.minute = minute; time.second = second;
			time.millisecond = millisecond;
			calculateAdditionalData();
		} else {
			setJulianDate(jdate);
		}

		return this;
	}

	/**
	 * Sets just Julian Date and no other parameter such as day of week etc.
	 * Used internally for speed.
	 *
	 * @param year   year to set
	 * @param month  month to set
	 * @param day    day to set
	 * @param hour   hour to set
	 * @param minute minute to set
	 * @param second second to set
	 */
	private void setJdOnly(int year, int month, int day, int hour, int minute, int second, int millisecond) {
		setJdOnly(TimeUtil.toJulianDate(year, month, day, hour, minute, second, millisecond));
	}


	// ---------------------------------------------------------------- core calculations

	/**
	 * Calculates day of week.
	 */
	private int calcDayOfWeek() {
		int jd = (int)(jdate.doubleValue() + 0.5);
		return (jd % 7) + 1;
		//return (jd + 1) % 7;		// return 0 (Sunday), 1 (Monday),...
	}

	private static final int[] NUM_DAYS = {-1, 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};        // 1-based
	private static final int[] LEAP_NUM_DAYS = {-1, 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};    // 1-based

	/**
	 * Calculates day of year.
	 */
	private int calcDayOfYear() {
		if (leap) {
			return LEAP_NUM_DAYS[time.month] + time.day;
		}
		return NUM_DAYS[time.month] + time.day;
	}


	/**
	 * Calculates week of year. Based on:
	 * "Algorithm for Converting Gregorian Dates to ISO 8601 Week Date"
	 * by Rick McCarty, 1999
	 *
	 * @param start  first day of week
	 * @param must   must have day of week
	 *
	 * @return week of year number
	 */
	private int calcWeekOfYear(int start, int must) {

		// is modification required?
		// modification is a fix for the days of year because of the different
		// starting day of week. when modification is required, one week is added
		// or subtracted to the current day, so calculation of the week of year
		// would be correct.
		int delta = 0;
		if (start <= this.dayofweek) {
			if (must < start) {
				delta = 7;
			}
		} else {
			if (must >= start) {
				delta = -7;
			}
		}

		int jd = (int)(jdate.doubleValue() + 0.5) + delta;
		int WeekDay = (jd % 7) + 1;

		int time_year = time.year;
		int DayOfYearNumber = this.dayofyear + delta;
		if (DayOfYearNumber < 1) {
			time_year--;
			DayOfYearNumber = TimeUtil.isLeapYear(time_year) ? 366 + DayOfYearNumber: 365 + DayOfYearNumber;
		} else if (DayOfYearNumber > (this.leap ? 366 : 365)) {
			DayOfYearNumber = this.leap ? DayOfYearNumber - 366: DayOfYearNumber - 365;
			time_year++;
		}

		// modification, if required, is finished. proceed to the calculation.

		int firstDay = jd - DayOfYearNumber + 1;
		int Jan1WeekDay = (firstDay % 7) + 1;

		// find if the date falls in YearNumber Y - 1 set WeekNumber to 52 or 53
		int YearNumber = time_year;
		int WeekNumber = 52;
		if ((DayOfYearNumber <= (8 - Jan1WeekDay)) && (Jan1WeekDay > must)) {
			YearNumber--;
			if ((Jan1WeekDay == must + 1) || ( (Jan1WeekDay == must + 2) && (TimeUtil.isLeapYear(YearNumber)) ) ) {
				WeekNumber = 53;
			}
		}

		// set WeekNumber to 1 to 53 if date falls in YearNumber
		int m = 365;
		if (YearNumber == time_year) {
			if (TimeUtil.isLeapYear(time_year)) {
				m = 366;
			}
			if ((m - DayOfYearNumber) < (must - WeekDay)) {
				YearNumber = time_year + 1;
				WeekNumber = 1;
			}
		}

		if (YearNumber == time_year) {
			int n = DayOfYearNumber + (7 - WeekDay) + (Jan1WeekDay - 1);
			WeekNumber = n / 7;
			if (Jan1WeekDay > must) {
				WeekNumber -= 1;
			}
		}
		return WeekNumber;
	}

	/**
	 * Return the week number of a day, within a period. This may be the week number in
	 * a year, or the week number in a month. Usually this will be a value >= 1, but if
	 * some initial days of the period are excluded from week 1, because
	 * minimalDaysInFirstWeek is > 1, then the week number will be zero for those
	 * initial days. Requires the day of week for the given date in order to determine
	 * the day of week of the first day of the period.
	 *
	 * @param dayOfPeriod
	 *                  Day-of-year or day-of-month. Should be 1 for first day of period.
	 * @param dayOfWeek day of week
	 *
	 * @return Week number, one-based, or zero if the day falls in part of the
	 *         month before the first week, when there are days before the first
	 *         week because the minimum days in the first week is more than one.
	 */
	private int calcWeekNumber(int dayOfPeriod, int dayOfWeek) {
		// Determine the day of the week of the first day of the period
		// in question (either a year or a month).  Zero represents the
		// first day of the week on this calendar.
		int periodStartDayOfWeek = (dayOfWeek - firstDayOfWeek - dayOfPeriod + 1) % 7;
		if (periodStartDayOfWeek < 0) {
			periodStartDayOfWeek += 7;
		}

		// Compute the week number.  Initially, ignore the first week, which
		// may be fractional (or may not be).  We add periodStartDayOfWeek in
		// order to fill out the first week, if it is fractional.
		int weekNo = (dayOfPeriod + periodStartDayOfWeek - 1) / 7;

		// If the first week is long enough, then count it.  If
		// the minimal days in the first week is one, or if the period start
		// is zero, we always increment weekNo.
		if ((7 - periodStartDayOfWeek) >= minDaysInFirstWeek) {
			++weekNo;
		}

		return weekNo;
	}

	// ---------------------------------------------------------------- add/sub time

	/**
	 * Adds time to current time. The main add method - all other add() methods
	 * must use this one.
	 * <p>
	 * There are 2 different kinds of addings, when months are added:
	 * <ul>
	 * <li>when months are not specially concern. All remaining days will be
	 * added to the next month. Example: 2003-01-31 + 0-01-0 = 2003-03-03</li>
	 * <li>when months addition is fixed, and month is not approximated.
	 * example: 2003-01-31 + 0-01-0 = 2003-02-28</li>
	 * </ul>
	 *
	 * @param year     delta year
	 * @param month    delta month
	 * @param day      delta days
	 * @param hour     delta hours
	 * @param minute   delta minutes
	 * @param second   delta seconds
	 * @param monthFix <code>true</code> for month fixing, <code>false</code> otherwise
	 */
	public JDateTime add(int year, int month, int day, int hour, int minute, int second, int millisecond, boolean monthFix) {
		int difference = 0;
		if (trackDST) {
			difference = TimeZoneUtil.getOffset(this, timezone);
		}
		addNoDST(year, month, day, hour, minute, second, millisecond, monthFix);
		if (trackDST) {
			difference = TimeZoneUtil.getOffset(this, timezone) - difference;
			if (difference != 0) {
				addNoDST(0, 0, 0, 0, 0, 0, difference, false);
			}
		}

		return this;
	}
	protected void addNoDST(int year, int month, int day, int hour, int minute, int second, int millisecond, boolean monthFix) {
		millisecond += time.millisecond;
		second += time.second;
		minute += time.minute;
		hour += time.hour;
		day += time.day;
		if (!monthFix) {
			month += time.month;
			year += time.year;
			set(year, month, day, hour, minute, second, millisecond);
		} else {
			// month fix:
			// 1. add all except month and year
			// 2. store day value
			// 3. add just months
			// 4. if new date is not equal to stored, return to last day of previous month
			setJdOnly(time.year, time.month, day, hour, minute, second, millisecond);
			int from = time.day;
			month += time.month + (year * 12);		// delta years to add are converted to delta months
			setJdOnly(time.year, month, time.day, time.hour, time.minute, time.second, time.millisecond);
			if (time.day < from) {
				set(time.year, time.month, 0, time.hour, time.minute, time.second, time.millisecond);
			} else {
				calculateAdditionalData();
			}

			/*
			// 5. store month value
			// 6. add just year
			// 7. if new month is not equal to stored, return to last day of previous month
			from = time.month;
			year += time.year;
			setJdOnly(year, time.month, time.day, time.hour, time.minute, time.second);
			if (time.month > from) {
				set(time.year, time.month, 0, time.hour, time.minute, time.second);
			}*/
		}
	}

	public JDateTime sub(int year, int month, int day, int hour, int minute, int second, int millisecond, boolean monthFix) {
		return add(-year, -month,  -day,  -hour, -minute, -second, -millisecond, monthFix);
	}


	/**
	 * Performs time adding with preset value of monthFix attribute.
	 *
	 * @param year   delta year
	 * @param month  delta month
	 * @param day    delta days
	 * @param hour   delta hours
	 * @param minute delta minutes
	 * @param second delta seconds
	 *
	 * @see #add(int, int, int, int, int, int, int, boolean)
	 */
	public JDateTime add(int year, int month, int day, int hour, int minute, int second, int millisecond) {
		return add(year, month, day, hour, minute, second, millisecond, monthFix);
	}
	public JDateTime sub(int year, int month, int day, int hour, int minute, int second, int millisecond) {
		return add(-year, -month, -day, -hour, -minute, -second, millisecond, monthFix);
	}


	/**
	 * Adds date, leaving time unchanged.
	 *
	 * @param year     years to add
	 * @param month    months to add
	 * @param day      days to add
	 * @param monthFix <code>true</code> for month fixing, <code>false</code> otherwise
	 *
	 * @see #add(int, int, int, int, int, int, int, boolean)
	 */
	public JDateTime add(int year, int month, int day, boolean monthFix) {
		return add(year, month, day, 0, 0, 0, 0, monthFix);
	}
	public JDateTime sub(int year, int month, int day, boolean monthFix) {
		return add(-year, -month, -day, 0, 0, 0, 0, monthFix);
	}

	/**
	 * Adds date, leaving time unchanged, with preset value of monthFix.
	 * attribute.
	 *
	 * @param year   years to add
	 * @param month  months to add
	 * @param day    days to add
	 *
	 * @see #add(int, int, int, boolean)
	 */
	public JDateTime add(int year, int month, int day) {
		return add(year, month, day, monthFix);
	}
	public JDateTime sub(int year, int month, int day) {
		return add(-year, -month, -day, monthFix);
	}

	/**
	 * Adds time.
	 *
	 * @param hour     hours to add
	 * @param minute   minutes to add
	 * @param second   seconds to add
	 * @param monthFix <code>true</code> for month fixing, <code>false</code> otherwise
	 *
	 * @see #add(int, int, int, int, int, int, int)
	 */
	public JDateTime addTime(int hour, int minute, int second, int millisecond, boolean monthFix) {
		return add(0, 0, 0, hour, minute, second, millisecond, monthFix);
	}
	public JDateTime subTime(int hour, int minute, int second, int millisecond, boolean monthFix) {
		return add(0, 0, 0, -hour, -minute, -second, -millisecond, monthFix);
	}

	public JDateTime addTime(int hour, int minute, int second, boolean monthFix) {
		return add(0, 0, 0, hour, minute, second, 0, monthFix);
	}
	public JDateTime subTime(int hour, int minute, int second, boolean monthFix) {
		return add(0, 0, 0, -hour, -minute, -second, 0, monthFix);
	}


	/**
	 * Adds time, with preset value of monthFix.
	 *
	 * @param hour   hours to add
	 * @param minute minutes to add
	 * @param second seconds to add
	 *
	 * @see #addTime(int, int, int, int, boolean)
	 */
	public JDateTime addTime(int hour, int minute, int second, int millisecond) {
		return addTime(hour, minute, second, millisecond, monthFix);
	}
	public JDateTime subTime(int hour, int minute, int second, int millisecond) {
		return addTime(-hour, -minute, -second, -millisecond, monthFix);
	}
	public JDateTime addTime(int hour, int minute, int second) {
		return addTime(hour, minute, second, 0, monthFix);
	}
	public JDateTime subTime(int hour, int minute, int second) {
		return addTime(-hour, -minute, -second, 0, monthFix);
	}


	/**
	 * Adds year.
	 *
	 * @param y        year to add
	 * @param monthFix <code>true</code> for month fixing, <code>false</code> otherwise
	 */
	public JDateTime addYear(int y, boolean monthFix) {
		return add(y, 0, 0, monthFix);
	}
	public JDateTime subYear(int y, boolean monthFix) {
		return add(-y, 0, 0, monthFix);
	}
	/**
	 * Adds year, with preset value of monthFix.
	 *
	 * @param y        year to add
	 */
	public JDateTime addYear(int y) {
		return addYear(y, monthFix);
	}
	public JDateTime subYear(int y) {
		return addYear(-y, monthFix);
	}


	/**
	 * Adds month.
	 *
	 * @param m        month to add
	 * @param monthFix <code>true</code> for month fixing, <code>false</code> otherwise
	 */
	public JDateTime addMonth(int m, boolean monthFix) {
		return add(0, m, 0, monthFix);
	}
	public JDateTime subMonth(int m, boolean monthFix) {
		return add(0, -m, 0, monthFix);
	}
	/**
	 * Adds month, with preset value of monthFix.
	 *
	 * @param m        month to add
	 */
	public JDateTime addMonth(int m) {
		return addMonth(m, monthFix);
	}
	public JDateTime subMonth(int m) {
		return addMonth(-m, monthFix);
	}

	/**
	 * Adds days.
	 *
	 * @param d      days to add
	 * @param monthFix <code>true</code> for month fixing, <code>false</code> otherwise
	 */
	public JDateTime addDay(int d, boolean monthFix) {
		return add(0, 0, d, monthFix);
	}
	public JDateTime subDay(int d, boolean monthFix) {
		return add(0, 0, -d, monthFix);
	}
	/**
	 * Adds days, with preset value of monthFix.
	 *
	 * @param d      days to add
	 */
	public JDateTime addDay(int d) {
		return addDay(d, monthFix);
	}
	public JDateTime subDay(int d) {
		return addDay(-d, monthFix);
	}

	/**
	 * Adds hours.
	 *
	 * @param h      hours to add
	 * @param monthFix <code>true</code> for month fixing, <code>false</code> otherwise
	 */
	public JDateTime addHour(int h, boolean monthFix) {
		return addTime(h, 0, 0, 0, monthFix);
	}
	public JDateTime subHour(int h, boolean monthFix) {
		return addTime(-h, 0, 0, 0, monthFix);
	}
	/**
	 * Adds hours, with preset value of monthFix.
	 *
	 * @param h      hours to add
	 */
	public JDateTime addHour(int h) {
		return addHour(h, monthFix);
	}
	public JDateTime subHour(int h) {
		return addHour(-h, monthFix);
	}


	/**
	 * Adds minutes.
	 *
	 * @param m      minutes to add.
	 * @param monthFix <code>true</code> for month fixing, <code>false</code> otherwise
	 */
	public JDateTime addMinute(int m, boolean monthFix) {
		return addTime(0, m, 0, 0, monthFix);
	}
	public JDateTime subMinute(int m, boolean monthFix) {
		return addTime(0, -m, 0, 0, monthFix);
	}
	/**
	 * Adds minutes, with preset value of monthFix.
	 *
	 * @param m      minutes to add.
	 */
	public JDateTime addMinute(int m) {
		return addMinute(m, monthFix);
	}
	public JDateTime subMinute(int m) {
		return addMinute(-m, monthFix);
	}

	/**
	 * Adds seconds.
	 *
	 * @param s      seconds to add
	 * @param monthFix <code>true</code> for month fixing, <code>false</code> otherwise
	 */
	public JDateTime addSecond(int s, boolean monthFix) {
		return addTime(0, 0, s, 0, monthFix);
	}
	public JDateTime subSecond(int s, boolean monthFix) {
		return addTime(0, 0, -s, 0, monthFix);
	}
	/**
	 * Adds seconds, with preset value of monthFix.
	 *
	 * @param s      seconds to add
	 */
	public JDateTime addSecond(int s) {
		return addSecond(s, monthFix);
	}
	public JDateTime subSecond(int s) {
		return addSecond(-s, monthFix);
	}


	/**
	 * Adds milliseconds.
	 *
	 * @param ms     milliseconds to add
	 * @param monthFix <code>true</code> for month fixing, <code>false</code> otherwise
	 */
	public JDateTime addMillisecond(int ms, boolean monthFix) {
		return addTime(0, 0, 0, ms, monthFix);
	}
	public JDateTime subMillisecond(int ms, boolean monthFix) {
		return addTime(0, 0, 0, -ms, monthFix);
	}
	/**
	 * Adds milliseconds, with preset value of monthFix.
	 *
	 * @param ms     milliseconds to add
	 */
	public JDateTime addMillisecond(int ms) {
		return addMillisecond(ms, monthFix);
	}
	public JDateTime subMillisecond(int ms) {
		return addMillisecond(-ms, monthFix);
	}

	// ----------------------------------------------------------------	ctors & sets

	/**
	 * Constructor that set date and time.
	 *
	 * @param year   year to set
	 * @param month  month to set
	 * @param day    day to set
	 * @param hour   hours to set
	 * @param minute minutes to set
	 * @param second seconds to set
	 * @param millisecond milliseconds to set
	 *
	 * @see #set(int, int, int, int, int, int, int)
	 */
	public JDateTime(int year, int month, int day, int hour, int minute, int second, int millisecond) {
		this.set(year, month, day, hour, minute, second, millisecond);
	}

	/**
	 * Sets date, time is set to midnight (00:00:00.000).
	 *
	 * @param year   year to set
	 * @param month  month to set
	 * @param day    day to set
	 */
	public JDateTime set(int year, int month, int day) {
		return set(year, month, day, 0, 0, 0, 0);
	}

	/**
	 * Constructor that sets just date. Time is set to 00:00:00.
	 *
	 * @param year   year to set
	 * @param month  month to set
	 * @param day    day to set
	 *
	 * @see #set(int, int, int)
	 */
	public JDateTime(int year, int month, int day) {
		this.set(year, month, day);
	}

	/**
	 * Sets time, date is unchanged.
	 *
	 * @param hour   hours to set
	 * @param minute minutes to set
	 * @param second seconds to set
	 */
	public JDateTime setTime(int hour, int minute, int second, int millisecond) {
		return set(time.year, time.month, time.day, hour, minute, second, millisecond);
	}

	/**
	 * Sets date, time remains unchanged.
	 *
	 * @param year   year
	 * @param month  month
	 * @param day    day
	 */
	public JDateTime setDate(int year, int month, int day) {
		return set(year, month, day, time.hour, time.minute, time.second, time.millisecond);
	}


	// ---------------------------------------------------------------- milliseconds

	/**
	 * Constructor that sets current time specified as time in milliseconds, from
	 * the midnight, January 1, 1970 UTC.
	 *
	 * @param millis  time in milliseconds, from the midnight, January 1, 1970 UTC
	 *
	 * @see #setTimeInMillis(long )
	 */
	public JDateTime(long millis) {
		setTimeInMillis(millis);
	}

	/**
	 * Sets the time based on current time in milliseconds. Current time is
	 * calculated from the midnight, January 1, 1970 UTC.
	 * <p>
	 * Calculation is divided in two steps, due to precision issues.
	 *
	 * @param millis  time in milliseconds, from the midnight, January 1, 1970 UTC
	 */
	public JDateTime setTimeInMillis(long millis) {
		millis += timezone.getOffset(millis);
		int integer = (int) (millis / TimeUtil.MILLIS_IN_DAY);
		double fraction = (double)(millis % TimeUtil.MILLIS_IN_DAY) / TimeUtil.MILLIS_IN_DAY;
		integer += JD_1970.integer;
		fraction += JD_1970.fraction;
		return setJulianDate(new JulianDateStamp(integer, fraction));
	}

	/**
	 * Returns time based on current time in milliseconds. Current time is
	 * calculated from the midnight, January 1, 1970 UTC.
	 * <p>
	 * Due to possible huge values calculation is divided in two steps:
	 * first for fractional difference, and then for integer parts.
	 * 
	 * @return time in milliseconds, from the midnight, January 1, 1970 UTC
	 */
	public long getTimeInMillis() {
		double then = (jdate.fraction - JD_1970.fraction) * TimeUtil.MILLIS_IN_DAY;
		then += (jdate.integer - JD_1970.integer) * TimeUtil.MILLIS_IN_DAY;
		then -= timezone.getOffset((long) then);
		then += then > 0 ? 1.0e-6 : -1.0e-6; 
		return (long) then;
	}


	// ---------------------------------------------------------------- date/time sets

	/**
	 * Sets current year.
	 *
	 * @param y      year to set
	 */
	public JDateTime setYear(int y) {
		return setDate(y, time.month, time.day);
	}

	/**
	 * Sets current month.
	 *
	 * @param m      month to set
	 */
	public JDateTime setMonth(int m) {
		return setDate(time.year, m, time.day);
	}

	/**
	 * Sets current day of month.
	 *
	 * @param d      day to set
	 */
	public JDateTime setDay(int d) {
		return setDate(time.year, time.month, d);
	}

	/**
	 * Set current hour.
	 *
	 * @param h      hour to set
	 */
	public JDateTime setHour(int h) {
		return setTime(h, time.minute, time.second, time.millisecond);
	}

	/**
	 * Set current minute.
	 *
	 * @param m      minutes to set
	 */
	public JDateTime setMinute(int m) {
		return setTime(time.hour, m, time.second, time.millisecond);

	}

	/**
	 * Sets current second.
	 *
	 * @param s      seconds and milliseconds to set
	 */
	public JDateTime setSecond(int s) {
		return setTime(time.hour, time.minute, s, time.millisecond);
	}

	public JDateTime setSecond(int s, int m) {
		return setTime(time.hour, time.minute, s, m);
	}


	/**
	 * Sets current millisecond.
	 *
	 * @param m      milliseconds to set
	 */
	public JDateTime setMillisecond(int m) {
		return setTime(time.hour, time.minute, time.second, m);
	}


	// ----------------------------------------------------------------	date/time gets


	/**
	 * Returns current year.
	 */
	public int getYear() {
		return time.year;
	}

	/**
	 * Returns current month.
	 */
	public int getMonth() {
		return time.month;
	}

	/**
	 * Returns current day of month.
	 * @see #getDayOfMonth
	 */
	public int getDay() {
		return time.day;
	}

	/**
	 * Returns current day of month.
	 * @see #getDay
	 */
	public int getDayOfMonth() {
		return time.day;
	}

	/**
	 * Returns current hour.
	 */
	public int getHour() {
		return time.hour;
	}

	/**
	 * Returns current minutes.
	 */
	public int getMinute() {
		return time.minute;
	}

	/**
	 * Return current seconds. For an integer value, just cast the returned
	 * value.
	 */
	public int getSecond() {
		return time.second;
	}

	/**
	 * Returns current milliseconds.
	 */
	public int getMillisecond() {
		return time.millisecond;
	}

	// ----------------------------------------------------------------	other gets

	/**
	 * Returns current day of week.
	 */
	public int getDayOfWeek() {
		return dayofweek;
	}

	/**
	 * Returns current day of year.
	 */
	public int getDayOfYear() {
		return dayofyear;
	}

	/**
	 * Returns <code>true</code> if current year is leap year.
	 */
	public boolean isLeapYear() {
		return leap;
	}

	/**
	 * Returns current week of year.
	 */
	public int getWeekOfYear() {
		return weekofyear;
	}

	/**
	 * Returns current week of month.
	 */
	public int getWeekOfMonth() {
		return weekofmonth;
	}


	/**
	 * Returns the length of the specified month in days.
	 */
	public int getMonthLength(int month) {
		return TimeUtil.getMonthLength(time.year, month, this.leap);
	}

	/**
	 * Returns the length of the current month in days.
	 */
	public int getMonthLength() {
		return getMonthLength(time.month);
	}

	/**
	 * Returns era: AD(1) or BC(0).
	 */
	public int getEra() {
		return time.year > 0 ? 1 : 0;
	}

	/**
	 * Calculates the number of milliseconds of a day.
	 */
	public int getMillisOfDay() {
		return ((((time.hour * 60) + time.minute) * 60) + time.second) * 1000 + time.millisecond;
	}

	// ----------------------------------------------------------------	current date time

	/**
	 * Sets current local date and time.
	 */
	public JDateTime setCurrentTime() {
		return setTimeInMillis(System.currentTimeMillis());
	}

	/**
	 * Constructor that sets current local date and time.
	 */
	public JDateTime() {
		this.setCurrentTime();
	}

	// ---------------------------------------------------------------- conversion

	/**
	 * Creates <code>JDateTime</code> from <code>Calendar</code>.
	 */
	public JDateTime(Calendar calendar) {
		setDateTime(calendar);
	}

	/**
	 * Sets current date and time from <code>Calendar</code>.
	 */
	public JDateTime setDateTime(Calendar calendar) {
		setTimeInMillis(calendar.getTimeInMillis());
		changeTimeZone(calendar.getTimeZone());
		return this;
	}

	/**
	 * Creates <code>JDateTime</code> from <code>Date</code>.
	 */
	public JDateTime(Date date) {
		setDateTime(date);
	}

	/**
	 * Sets current date and time from <code>Date</code>.
	 */
	public JDateTime setDateTime(Date date) {
		return setTimeInMillis(date.getTime());
	}

	/**
	 * Converts to <code>Date</code> instance.
	 */
	public Date convertToDate() {
		return new Date(getTimeInMillis());
	}

	/**
	 * Converts to <code>Calendar</code> instance.
	 */
	public Calendar convertToCalendar() {
		Calendar calendar = Calendar.getInstance(getTimeZone());
		calendar.setTimeInMillis(getTimeInMillis());
		return calendar;
	}

	/**
	 * Converts to <code>java.sql.Date</code> instance.
	 */
	public java.sql.Date convertToSqlDate() {
		return new java.sql.Date(getTimeInMillis());
	}

	/**
	 * Converts to <code>Time</code> instance.
	 */
	public Time convertToSqlTime() {
		return new Time(getTimeInMillis());
	}

	/**
	 * Converts to <code>Timestamp</code> instance.
	 */
	public Timestamp convertToSqlTimestamp() {
		return new Timestamp(getTimeInMillis());
	}

	// ---------------------------------------------------------------- ctors from conversions

	/**
	 * Creates <code>JDateTime</code> from <code>DateTimeStamp</code>.
	 */
	public JDateTime(DateTimeStamp dts) {
		setDateTimeStamp(dts);
	}


	/**
	 * Creates <code>JDateTime</code> from <code>JulianDateStamp</code>.
	 */
	public JDateTime(JulianDateStamp jds) {
		setJulianDate(jds);
	}

	/**
	 * Creates <code>JDateTime</code> from <code>double</code> that represents JD.
	 */
	public JDateTime(double jd) {
		setJulianDate(new JulianDateStamp(jd));
	}

	/**
	 * Creates <code>JDateTime</code> from a string.
	 */
	public JDateTime(String src) {
		parse(src);
	}

	/**
	 * Creates <code>JDateTime</code> from a string, using specified template.
	 */
	public JDateTime(String src, String template) {
		parse(src, template);
	}
	
	public JDateTime(String src, JdtFormat jdtFormat) {
		parse(src, jdtFormat);
	}


	// ---------------------------------------------------------------- dst

	protected boolean trackDST = JDateTimeDefault.trackDST;

	public boolean isTrackDST() {
		return trackDST;
	}

	public JDateTime setTrackDST(boolean trackDST) {
		this.trackDST = trackDST;
		return this;
	}

	// ---------------------------------------------------------------- monthFix

	protected boolean monthFix = JDateTimeDefault.monthFix;

	/**
	 * Returns <code>true</code> if month fix is active.
	 */
	public boolean isMonthFix() {
		return monthFix;
	}

	/**
	 * Sets custom month fix value.
	 */
	public JDateTime setMonthFix(boolean monthFix) {
		this.monthFix = monthFix;
		return this;
	}


	// ---------------------------------------------------------------- timezone

	protected TimeZone timezone = JDateTimeDefault.timeZone == null ? TimeZone.getDefault() : JDateTimeDefault.timeZone;

	/**
	 * Changes current timezone. Current time is changed if time zone has been changed.
	 */
	public JDateTime changeTimeZone(TimeZone timezone) {
		long now = getTimeInMillis();
		int difference = TimeZoneUtil.getOffsetDifference(now, this.timezone, timezone);
		this.timezone = timezone;
		if (difference != 0) {
			addMillisecond(difference);
		}
		return this;
	}

	/**
	 * Changes time zone. Equivalent to:
	 * <code>setTimeZone(from); changeTimeZone(to);</code>
	 */
	public JDateTime changeTimeZone(TimeZone from, TimeZone to) {
		this.timezone = from;
		changeTimeZone(to);
		return this;
	}

	/**
	 * Sets time zone <b>without</b> changing the time.
	 */
	public JDateTime setTimeZone(TimeZone timezone) {
		this.timezone = timezone;
		return this;
	}

	/**
	 * Return currently active time zone.
	 */
	public TimeZone getTimeZone() {
		return timezone;
	}

	/**
	 * Returns <code>true</code> if current date is in
	 * daylight savings time in the time zone.
	 */
	public boolean isInDaylightTime() {
		long now = getTimeInMillis();
		int offset = timezone.getOffset(now);
		int rawOffset = timezone.getRawOffset();
		return (offset != rawOffset);
	}

	// ---------------------------------------------------------------- locale

	protected Locale locale = JDateTimeDefault.locale == null ? Locale.getDefault() : JDateTimeDefault.locale;

	/**
	 * Sets custom locale.
	 */
	public JDateTime setLocale(Locale locale) {
		this.locale = locale;
		return this;
	}

	/**
	 * Return currently active locale.
	 */
	public Locale getLocale() {
		return locale;
	}

	// ---------------------------------------------------------------- format

	protected String format = JDateTimeDefault.format;

	/**
	 * Defines default format.
	 */
	public JDateTime setFormat(String format) {
		this.format = format;
		return this;
	}

	/**
	 * Returns format.
	 */
	public String getFormat() {
		return format;
	}


	protected JdtFormatter jdtFormatter = JDateTimeDefault.formatter;

	/**
	 * Defines custom formatter.
	 */
	public JDateTime setJdtFormatter(JdtFormatter jdtFormatter) {
		this.jdtFormatter = jdtFormatter;
		return this;
	}

	/**
	 * Returns actual {@link JdtFormatter}.
	 */
	public JdtFormatter getJdtFormatter() {
		return jdtFormatter;
	}

	/**
	 * Sets both format and formatter from provided {@link JdtFormat}.
	 */
	public JDateTime setJdtFormat(JdtFormat jdtFormat) {
		this.format = jdtFormat.getFormat();
		this.jdtFormatter = jdtFormat.getFormatter();
		return this;
	}


	// ---------------------------------------------------------------- toString and parse


	/**
	 * Returns string representation of date/time in specified format.
	 */
	public String toString(String format) {
		return jdtFormatter.convert(this, format);
	}

	/**
	 * Returns spring representation of current date/time in currently active format.
	 * @see #getFormat()
	 */
	@Override
	public String toString() {
		return jdtFormatter.convert(this, format);
	}

	public String toString(JdtFormat jdtFormat) {
		return jdtFormat.convert(this);
	}

	/**
	 * Sets date/time from a string given in provided template.
	 * @param src        string containing date time information
	 * @param format format template
	 */
	public JDateTime parse(String src, String format) {
		setDateTimeStamp(jdtFormatter.parse(src, format));
		return this;
	}

	/**
	 * Sets date/time from a string and currently active template.
	 * @param src        string containing date time information
	 * @see #getFormat()
	 */
	public JDateTime parse(String src) {
		setDateTimeStamp(jdtFormatter.parse(src, format));
		return this;
	}

	public JDateTime parse(String src, JdtFormat jdtFormat) {
		setDateTimeStamp(jdtFormat.parse(src));
		return this;
	}

	/**
	 * Checks if some string represents a valid date using actual template.
	 *
	 * @return <code>true</code> if date is valid, otherwise <code>false</code>
	 */
	public boolean isValid(String s) {
		return isValid(s, format);
	}

	/**
	 * Checks if some string represents a valid date using provided template.
	 *
	 * @return <code>true</code> if date is valid, otherwise <code>false</code>
	 */
	public boolean isValid(String s, String template) {
		DateTimeStamp dtsOriginal;
		try {
			dtsOriginal = jdtFormatter.parse(s, template);
		} catch (Exception ignore) {
			return false;
		}
		if (dtsOriginal == null) {
			return false;
		}
		return TimeUtil.isValidDateTime(dtsOriginal);
	}


	// ---------------------------------------------------------------- week definitions

	protected int firstDayOfWeek = JDateTimeDefault.firstDayOfWeek;
	protected int mustHaveDayOfFirstWeek = JDateTimeDefault.mustHaveDayOfFirstWeek;

	/**
	 * Defines week definitions.
	 *
	 * @param start  first day in week, [1-7],
	 * @param must   must have day of the 1st week, [1-7]
	 */
	public JDateTime setWeekDefinition(int start, int must) {
		if ((start >= 1) && (start <= 7)) {
			firstDayOfWeek = start;
		}
		if ((must >= 1) && (must <= 7)) {
			mustHaveDayOfFirstWeek = must;
			minDaysInFirstWeek = convertMin2Must(firstDayOfWeek, must);
		}
		return this;
	}

	/**
	 * Returns actual the first day of the week.
	 */
	public int getFirstDayOfWeek() {
		return firstDayOfWeek;
	}

	/**
	 * Returns actual must have day of the 1st week.
	 */
	public int getMustHaveDayOfFirstWeek() {
		return mustHaveDayOfFirstWeek;
	}


	// ---------------------------------------------------------------- week definitions (alt)

	protected int minDaysInFirstWeek = JDateTimeDefault.minDaysInFirstWeek;

	/**
	 * Returns actual minimal number of days of the first week. It is
	 * calculated from must have day of the first week.
	 *
	 * @return minimal number of days of the first week
	 */
	public int getMinDaysInFirstWeek() {
		return minDaysInFirstWeek;
	}

	/**
	 * Defines week alternatively.
	 *
	 * @param start  first day in week
	 * @param min    minimal days of week
	 */

	public JDateTime setWeekDefinitionAlt(int start, int min) {
		if ((start >= 1) && (start <= 7)) {
			firstDayOfWeek = start;
		}
		if ((min >= 1) && (min <= 7)) {
			mustHaveDayOfFirstWeek = convertMin2Must(firstDayOfWeek, min);
			minDaysInFirstWeek = min;
		}
		return this;
	}

	/**
	 * Converts minimal day of week to must have day of week.
	 * Method is symmetrical.
	 *
	 * @param start  first day of week
	 * @param min    minimal day of week
	 *
	 * @return must have day of week
	 */
	private static int convertMin2Must(int start, int min) {
		int must = 8 - min + (start - 1);
		if (must > 7) {
			must -= 7;
		}
		return must;
	}

	// ---------------------------------------------------------------- equals & hashCode

	/**
	 * Compares if two JDateTime instances are equal.
	 * Comparison precision is 1e-3 seconds.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		JDateTime jdt = (JDateTime) obj;
		return  (this.monthFix == jdt.monthFix) &&
				(this.firstDayOfWeek == jdt.firstDayOfWeek) &&
				(this.mustHaveDayOfFirstWeek == jdt.mustHaveDayOfFirstWeek) &&
				(this.time.equals(jdt.time)) &&
				(this.timezone.equals(jdt.timezone));
	}

	@Override
	public int hashCode() {
		int result = HashCode.SEED;
		result = hash(result, time);
		result = hash(result, timezone);
		result = hash(result, monthFix);
		result = hash(result, firstDayOfWeek);
		result = hash(result, mustHaveDayOfFirstWeek);
		return result;
	}


	// ---------------------------------------------------------------- clone

	@Override
	public JDateTime clone() {
		JDateTime jdt = new JDateTime(this.jdate);
		jdt.monthFix = this.monthFix;
		jdt.timezone = this.timezone;
		jdt.locale = this.locale;
		jdt.format = this.format;
		jdt.jdtFormatter = this.jdtFormatter;
		jdt.firstDayOfWeek = this.firstDayOfWeek;
		jdt.mustHaveDayOfFirstWeek = this.mustHaveDayOfFirstWeek;
		jdt.trackDST = this.trackDST;
		return jdt;
	}


	// ----------------------------------------------------------------	compare

	/**
	 * Compares current JDateTime object with another one, up to 1 millisecond.
	 *
	 * @param o     JDateTime to compare
	 *
	 * @return -1 if the current object is less than the argument, 0 if the argument is
	 *         equal, and 1 if the current object is greater than the argument
	 */
	public int compareTo(Object o) {
		return time.compareTo(((JDateTime) o).getDateTimeStamp());
	}
	public int compareTo(JDateTime jd) {
		return time.compareTo(jd.getDateTimeStamp());
	}

	/**
	 * Compares current JDateTime date with another date. Time component is ignored.
	 */
	public int compareDateTo(JDateTime jd) {
		return time.compareDateTo(jd.getDateTimeStamp());
	}

	/**
	 * Returns <code>true</code> if current time is after then provided time.
	 */
	public boolean isAfter(JDateTime then) {
		return time.compareTo((then).getDateTimeStamp()) > 0;
	}
	/**
	 * Returns <code>true</code> if current time is before then provided time.
	 */
	public boolean isBefore(JDateTime then) {
		return time.compareTo((then).getDateTimeStamp()) < 0;
	}

	/**
	 * Returns <code>true</code> if current date is after then provided date.
	 * Time component is ignored.
	 */
	public boolean isAfterDate(JDateTime then) {
		return time.compareDateTo((then).getDateTimeStamp()) > 0;
	}
	/**
	 * Returns <code>true</code> if current date is before then provided date.
	 * Time component is ignored.
	 */
	public boolean isBeforeDate(JDateTime then) {
		return time.compareDateTo((then).getDateTimeStamp()) < 0;
	}


	// ---------------------------------------------------------------- difference

	/**
	 * Returns number of full days between two dates.
	 */
	public int daysBetween(JDateTime then) {
		return this.jdate.daysBetween(then.jdate);
	}

	/**
	 * Returns number of full days between two dates.
	 */
	public int daysBetween(JulianDateStamp then) {
		return this.jdate.daysBetween(then);
	}


	// ---------------------------------------------------------------- alternative representation


	/**
	 * Returns JD as double value.
	 */
	public double getJulianDateDouble() {
		return jdate.doubleValue();
	}

	/**
	 * Sets JD.
	 */
	public JDateTime setJulianDate(double jd) {
		return setJulianDate(new JulianDateStamp(jd));
	}

	// ---------------------------------------------------------------- custom equals

	/**
	 * Returns <code>true</code> if provided date is equal to current one.
	 * May be used for date validation test.
	 */
	public boolean equalsDate(int year, int month, int day) {
		return (time.year == year) && (time.month == month) && (time.day == day);
	}

	/**
	 * Returns <code>true</code> if two dates are equal.
	 * Time component is ignored.
	 */
	public boolean equalsDate(JDateTime date) {
		return time.isEqualDate(date.time);
	}

	/**
	 * Returns <code>true</code> if two times are equal.
	 * Date component is ignored.
	 */
	public boolean equalsTime(JDateTime date) {
		return time.isEqualTime(date.time);
	}

}
