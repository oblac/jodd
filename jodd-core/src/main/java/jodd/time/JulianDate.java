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

package jodd.time;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import static jodd.time.TimeUtil.MILLIS_IN_DAY;

/**
 * Julian Date stamp, for high precision calculations. Julian date is a real
 * number and it basically consist of two parts: integer and fraction. Integer
 * part carries date information, fraction carries time information.
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
 * <p>
 * For calculations that will have time precision of 1e-3 seconds, both
 * fraction and integer part must have enough digits in it. The problem is
 * that integer part is big and, on the other hand fractional is small, and
 * since final julian date is a sum of this two values, some fraction
 * numerals may be lost. Therefore, for higher precision both
 * fractional and integer part of julian date real number has to be
 * preserved.
 * <p>
 * This class stores the unmodified fraction part, but not all digits
 * are significant! For 1e-3 seconds precision, only 8 digits after
 * the decimal point are significant.
 *
 * @see TimeUtil
 */
public class JulianDate implements Serializable, Cloneable {
	/**
	 * Julian Date for 1970-01-01T00:00:00 (Thursday).
	 */
	public static final JulianDate JD_1970 = new JulianDate(2440587, 0.5);

	/**
	 * Julian Date for 2001-01-01T00:00:00 (Monday).
	 */
	public static final JulianDate JD_2001 = new JulianDate(2451910, 0.5);


	public static JulianDate of(final double value) {
		return new JulianDate(value);
	}

	public static JulianDate of(final LocalDateTime localDateTime) {
		return of(localDateTime.getYear(),
			localDateTime.getMonth().getValue(),
			localDateTime.getDayOfMonth(),
			localDateTime.getHour(),
			localDateTime.getMinute(),
			localDateTime.getSecond(),
			localDateTime.getNano() / 1_000_000);
	}

	public static JulianDate of(final LocalDate localDate) {
		return of(localDate.getYear(),
			localDate.getMonth().getValue(),
			localDate.getDayOfMonth(),
			0, 0, 0, 0);
	}

	public static JulianDate of(final long milliseconds) {
		int integer = (int) (milliseconds / MILLIS_IN_DAY);
		double fraction = (double)(milliseconds % MILLIS_IN_DAY) / MILLIS_IN_DAY;
		integer += JD_1970.integer;
		fraction += JD_1970.fraction;
		return new JulianDate(integer, fraction);
	}

	public static JulianDate of(final int i, final double f) {
		return new JulianDate(i, f);
	}

	public static JulianDate of(int year, int month, int day, final int hour, final int minute, final int second, final int millisecond) {
		// month range fix
		if ((month > 12) || (month < -12)) {
			month--;
			int delta = month / 12;
			year += delta;
			month -= delta * 12;
			month++;
		}
		if (month < 0) {
			year--;
			month += 12;
		}

		// decimal day fraction
		double frac = (hour / 24.0) + (minute / 1440.0) + (second / 86400.0) + (millisecond / 86400000.0);
		if (frac < 0) {		// negative time fix
			int delta = ((int)(-frac)) + 1;
			frac += delta;
			day -= delta;
		}
		//double gyr = year + (0.01 * month) + (0.0001 * day) + (0.0001 * frac) + 1.0e-9;
		double gyr = year + (0.01 * month) + (0.0001 * (day + frac)) + 1.0e-9;

		// conversion factors
		int iy0;
		int im0;
		if (month <= 2) {
			iy0 = year - 1;
			im0 = month + 12;
		} else {
			iy0 = year;
			im0 = month;
		}
		int ia = iy0 / 100;
		int ib = 2 - ia + (ia >> 2);

		// calculate julian date
		int jd;
		if (year <= 0) {
			jd = (int)((365.25 * iy0) - 0.75) + (int)(30.6001 * (im0 + 1)) + day + 1720994;
		} else {
			jd = (int)(365.25 * iy0) + (int)(30.6001 * (im0 + 1)) + day + 1720994;
		}
		if (gyr >= 1582.1015) {						// on or after 15 October 1582
			jd += ib;
		}
		//return  jd + frac + 0.5;

		return new JulianDate(jd, frac + 0.5);
	}

	/**
	 * Integer part of the Julian Date (JD).
	 */
	protected int integer;

	/**
	 * Returns integer part of the Julian Date (JD).
	 */
	public int getInteger() {
		return integer;
	}

	/**
	 * Fraction part of the Julian Date (JD).
	 * Should be always in [0.0, 1.0) range.
	 */
	protected double fraction;

	/**
	 * Returns the fraction part of Julian Date (JD).
	 * Returned value is always in [0.0, 1.0) range.
	 */
	public double getFraction() {
		return fraction;
	}

	/**
	 * Calculates and returns significant fraction only as an int.
	 */
	public int getSignificantFraction() {
		return (int) (fraction * 100_000_000);
	}

	/**
	 * Returns JDN. Note that JDN is not equal to {@link #integer}. It is calculated by
	 * rounding to the nearest integer.
	 */
	public int getJulianDayNumber() {
		if (fraction >= 0.5) {
			return integer + 1;
		}
		return integer;
	}

	/**
	 * Creates JD from a <code>double</code>.
	 */
	public JulianDate(final double jd) {
		integer = (int) jd;
		fraction = jd - (double)integer;
	}

	/**
	 * Creates JD from both integer and fractional part using normalization.
	 * Normalization occurs when fractional part is out of range. 
	 *
	 * @see #set(int, double)
	 *
	 * @param i      integer part
	 * @param f      fractional part should be in range [0.0, 1.0)
	 */
	public JulianDate(final int i, final double f) {
		set(i, f);
	}

	/**
	 * Creates JD from <code>BigDecimal</code>.
	 */
	public JulianDate(final BigDecimal bd) {
		double d = bd.doubleValue();
		integer = (int) d;
		fraction = bd.subtract(new BigDecimal(integer)).doubleValue();
	}


	// ---------------------------------------------------------------- conversion
	

	/**
	 * Returns <code>double</code> value of JD.
	 * <b>CAUTION</b>: double values may not be suit for precision math due to
	 * loss of precision.
	 */
	public double doubleValue() {
		return (double)integer + fraction;
	}

	/**
	 * Returns <code>BigDecimal</code> value of JD.
	 */
	public BigDecimal toBigDecimal() {
		return new BigDecimal(integer).add(new BigDecimal(fraction));
	}

	/**
	 * Returns string representation of JD.
	 *
	 * @return julian integer as string
	 */
	@Override
	public String toString() {
		String s = Double.toString(fraction);
		int i = s.indexOf('.');
		s = s.substring(i);
		return integer + s;
	}

	/**
	 * Converts to milliseconds.
	 */
	public long toMilliseconds() {
		double then = (fraction - JD_1970.fraction) * MILLIS_IN_DAY;
		then += (integer - JD_1970.integer) * MILLIS_IN_DAY;
		then += then > 0 ? 1.0e-6 : -1.0e-6;
		return (long) then;
	}


	public LocalDateTime toLocalDateTime() {
		int year, month, day;
		double frac;
		int jd, ka, kb, kc, kd, ke, ialp;

		//double JD = jds.doubleValue();//jdate;
		//jd = (int)(JD + 0.5);							// integer julian date
		//frac = JD + 0.5 - (double)jd + 1.0e-10;		// day fraction

		ka = (int)(fraction + 0.5);
		jd = integer + ka;
		frac = fraction + 0.5 - ka + 1.0e-10;

		ka = jd;
		if (jd >= 2299161) {
			ialp = (int)(((double)jd - 1867216.25) / (36524.25));
			ka = jd + 1 + ialp - (ialp >> 2);
		}
		kb = ka + 1524;
		kc =  (int)(((double)kb - 122.1) / 365.25);
		kd = (int)((double)kc * 365.25);
		ke = (int)((double)(kb - kd) / 30.6001);
		day = kb - kd - ((int)((double)ke * 30.6001));
		if (ke > 13) {
			month = ke - 13;
		} else {
			month = ke - 1;
		}
		if ((month == 2) && (day > 28)){
			day = 29;
		}
		if ((month == 2) && (day == 29) && (ke == 3)) {
			year = kc - 4716;
		} else if (month > 2) {
			year = kc - 4716;
		} else {
			year = kc - 4715;
		}

		final int time_year = year;
		final int time_month = month;
		final int time_day = day;

		// hour with minute and second included as fraction
		double d_hour = frac * 24.0;
		final int time_hour = (int) d_hour;				// integer hour

		// minute with second included as a fraction
		double d_minute = (d_hour - (double)time_hour) * 60.0;
		final int time_minute = (int) d_minute;			// integer minute

		double d_second = (d_minute - (double)time_minute) * 60.0;
		final int time_second = (int) d_second;			// integer seconds

		double d_millis = (d_second - (double)time_second) * 1000.0;

		// fix calculation errors
		final int time_millisecond = (int) (((d_millis * 10) + 0.5) / 10);

		return LocalDateTime.of(time_year, time_month, time_day, time_hour, time_minute, time_second, time_millisecond * 1_000_000);
	}


	// ---------------------------------------------------------------- math

	/**
	 * Adds a JD to current instance.
	 */
	public JulianDate add(final JulianDate jds) {
		int i = this.integer + jds.integer;
		double f = this.fraction + jds.fraction;
		return new JulianDate(i, f);
	}

	/**
	 * Adds a double to current instance.
	 */
	public JulianDate add(final double delta) {
		return new JulianDate(this.integer, this.fraction + delta);
	}


	/**
	 * Subtracts a JD from current instance.
	 */
	public JulianDate sub(final JulianDate jds) {
		int i = this.integer - jds.integer;
		double f = this.fraction -jds.fraction;
		return new JulianDate(i, f);
	}

	/**
	 * Subtracts a double from current instance.
	 */
	public JulianDate sub(final double delta) {
		return new JulianDate(this.integer, this.fraction - delta);
	}

	/**
	 * Sets integer and fractional part with normalization.
	 * Normalization means that if double is out of range,
	 * values will be correctly fixed. 
	 */
	private void set(final int i, double f) {
		integer = i;
		int fi = (int) f;
		f -= fi;
		integer += fi;
		if (f < 0) {
			f += 1;
			integer--;
		}
		this.fraction = f;
	}


	// ---------------------------------------------------------------- between

	/**
	 * Calculates the number of days between two dates. Returned value is always positive.
	 */
	public int daysBetween(final JulianDate otherDate) {
		int difference = daysSpan(otherDate);
		return difference >= 0 ? difference : -difference;
	}

	/**
	 * Returns span between two days. Returned value may be positive (when this date
	 * is after the provided one) or negative (when comparing to future date).
	 */
	public int daysSpan(final JulianDate otherDate) {
		int now = getJulianDayNumber();
		int then = otherDate.getJulianDayNumber();
		return now - then;
	}

	// ---------------------------------------------------------------- equals & hashCode

	@Override
	public boolean equals(final Object object) {
		if (this == object) {
			return true;
		}
		if (this.getClass() != object.getClass()) {
			return false;
		}
		JulianDate stamp = (JulianDate) object;
		return  (stamp.integer == this.integer) &&
				(Double.compare(stamp.fraction, this.fraction) == 0);
	}

	@Override
	public int hashCode() {
		return Objects.hash(integer, fraction);
	}

	// ---------------------------------------------------------------- clone

	@Override
	protected JulianDate clone() {
		return new JulianDate(this.integer, this.fraction);
	}

	// ---------------------------------------------------------------- conversion

	/**
	 * Returns Reduced Julian Date (RJD), used by astronomers.
	 * RJD = JD − 2400000
	 */
	public JulianDate getReducedJulianDate() {
		return new JulianDate(integer - 2400000, fraction);
	}

	/**
	 * Returns Modified Julian Date (MJD), where date starts from midnight rather than noon.
	 * RJD = JD − 2400000.5
	 */
	public JulianDate getModifiedJulianDate() {
		return new JulianDate(integer - 2400000, fraction - 0.5);
	}

	/**
	 * Returns Truncated Julian Day (TJD), introduced by NASA for the space program.
	 * TJD began at midnight at the beginning of May 24, 1968 (Friday).
	 */
	public JulianDate getTruncatedJulianDate() {
		return new JulianDate(integer - 2440000, fraction - 0.5);
	}

}
