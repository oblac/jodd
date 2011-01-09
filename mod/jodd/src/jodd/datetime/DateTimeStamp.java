// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import jodd.util.HashCode;
import jodd.util.StringPool;
import static jodd.util.HashCode.hash;

import java.io.Serializable;

/**
 * Generic date time stamp just stores and holds date and time information.
 * This class does not contain any date/time logic, neither guarantees
 * that date is valid.
 *
 * @see JDateTime
 * @see JulianDateStamp
 */
public class DateTimeStamp implements Comparable, Serializable, Cloneable {

	/**
	 * Default empty constructor.
	 */
	public DateTimeStamp() {
	}

	/**
	 * Constructor that sets date and time.
	 */
	public DateTimeStamp(int year, int month, int day, int hour, int minute, int second, int millisecond) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.millisecond = millisecond;
	}

	/**
	 * Constructor that sets just date. Time is set to zeros.
	 */
	public DateTimeStamp(int year, int month, int day) {
		this(year, month, day, 0, 0, 0, 0);
	}

	/**
	 * Year.
	 */
	public int year;

	/**
	 * Month, range: [1 - 12]
	 */
	public int month = 1;

	/**
	 * Day, range: [1 - 31]
	 */
	public int day = 1;

	/**
	 * Hour, range: [0 - 23]
	 */
	public int hour;

	/**
	 * Minute, range [0 - 59]
	 */
	public int minute;

	/**
	 * Second, range: [0 - 59]
	 */
	public int second;

	/**
	 * Millisecond, range: [0 - 1000]
	 */
	public int millisecond;


	// ---------------------------------------------------------------- get/set

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public int getMillisecond() {
		return millisecond;
	}

	public void setMillisecond(int millisecond) {
		this.millisecond = millisecond;
	}

	// ---------------------------------------------------------------- compare

	/**
	 * Compares this object with the specified object for order.  Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 *
	 * @param   o the Object to be compared.
	 * @return  a negative integer, zero, or a positive integer as this object
	 *		is less than, equal to, or greater than the specified object.
	 *
	 * @throws ClassCastException if the specified object's type prevents it
	 *         from being compared to this Object.
	 */
	public int compareTo(Object o) {
		DateTimeStamp dts = (DateTimeStamp) o;

		int date1 = year * 10000 + month * 100 + day;
		int date2 = dts.year * 10000 + dts.month * 100 + dts.day;

		if (date1 < date2) {
			return -1;
		}
		if (date1 > date2) {
			return 1;
		}

		date1 = (hour * 10000000) + (minute * 100000) + (second * 1000) + millisecond;
		date2 = (dts.hour * 10000000) + (dts.minute * 100000) + (dts.second * 1000) + dts.millisecond;

		if (date1 < date2) {
			return -1;
		}
		if (date1 > date2) {
			return 1;
		}
		return 0;
	}

	/**
	 * Compares just date component of two date time stamps.
	 */
	public int compareDateTo(Object o) {
		DateTimeStamp dts = (DateTimeStamp) o;

		int date1 = year * 10000 + month * 100 + day;
		int date2 = dts.year * 10000 + dts.month * 100 + dts.day;

		if (date1 < date2) {
			return -1;
		}
		if (date1 > date2) {
			return 1;
		}
		return 0;
	}


	// ---------------------------------------------------------------- toString

	/**
	 * Simple to string conversion.
	 *
	 * @return date/time string in 'y-m-d h:m:m.s' format
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(25);
		sb.append(year).append('-').append(month).append('-').append(day).append(' ');
		sb.append(hour).append(':').append(minute).append(':').append(second).append('.').append(millisecond);
		return sb.toString();
	}

	// ---------------------------------------------------------------- equals & hashCode

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof DateTimeStamp)) {
			return false;
		}
		DateTimeStamp stamp = (DateTimeStamp) object;
		return  (stamp.year == this.year) &&
				(stamp.month == this.month) &&
				(stamp.day == this.day) &&
				(stamp.hour == this.hour) &&
				(stamp.minute == this.minute) &&
				(stamp.second == this.second) &&
				(stamp.millisecond == this.millisecond);
	}

	@Override
	public int hashCode() {
		int result = HashCode.SEED;
		result = hash(result, year);
		result = hash(result, month);
		result = hash(result, day);
		result = hash(result, hour);
		result = hash(result, minute);
		result = hash(result, second);
		result = hash(result, millisecond);
		return result;
	}

// ---------------------------------------------------------------- clone


	@Override
	protected DateTimeStamp clone() {
		DateTimeStamp dts = new DateTimeStamp();
		dts.year = this.year;
		dts.month = this.month;
		dts.day = this.day;
		dts.hour = this.hour;
		dts.minute = this.minute;
		dts.second = this.second;
		dts.millisecond = this.millisecond;
		return dts;
	}

	// ---------------------------------------------------------------- equals

	public boolean isEqualDate(DateTimeStamp date) {
		return date.day == this.day
				&& date.month == this.month
				&& date.year == this.year;
	}

	public boolean isEqualTime(DateTimeStamp time) {
		return time.hour == this.hour
				&& time.minute == this.minute
				&& time.second == this.second
				&& time.millisecond == this.millisecond;
	}


}
