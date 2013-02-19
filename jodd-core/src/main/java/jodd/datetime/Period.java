// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

/**
 * Holds a time period. With julian dates and {@link JDateTime} it is quite
 * easy to calculate period in days - just by subtracting two julian day numbers.
 * However, calculating hours, minutes and seconds would require more calculation
 * and this class provides simple and faster period calculation.
 */
public class Period {

	protected final long days;
	protected final int hours;
	protected final int minutes;
	protected final int seconds;
	protected final int milliseconds;

	public Period(JDateTime jdt1, JDateTime jdt2) {
		if (jdt2.isBefore(jdt1)) {
			JDateTime temp = jdt1;
			jdt1 = jdt2;
			jdt2 = temp;
		}
		long julian2 = jdt2.getJulianDayNumber();
		long julian1 = jdt1.getJulianDayNumber();

		long days = julian2 - julian1;
		int milliseconds = jdt2.getMillisecond() - jdt1.getMillisecond();
		int seconds = jdt2.getSecond() - jdt1.getSecond();
		int minutes = jdt2.getMinute() - jdt1.getMinute();
		int hours = jdt2.getHour() - jdt1.getHour();

		if (milliseconds < 0) {
			seconds--;
			milliseconds += 1000;
		}
		if (seconds < 0) {
			minutes--;
			seconds += 60;
		}
		if (minutes < 0) {
			hours--;
			minutes += 60;
		}
		if (hours < 0) {
			days--;
			hours += 24;
		}

		this.days = days;
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		this.milliseconds = milliseconds;
	}

	/**
	 * Returns number of days in period.
	 */
	public long getDays() {
		return days;
	}

	/**
	 * Returns hours in period.
	 */
	public int getHours() {
		return hours;
	}

	/**
	 * Returns minutes in period.
	 */
	public int getMinutes() {
		return minutes;
	}

	/**
	 * Returns seconds in period.
	 */
	public int getSeconds() {
		return seconds;
	}

	/**
	 * Returns milliseconds in period.
	 */
	public int getMilliseconds() {
		return milliseconds;
	}
}
