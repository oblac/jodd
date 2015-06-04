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
