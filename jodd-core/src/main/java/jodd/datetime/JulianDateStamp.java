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

import jodd.util.HashCode;
import static jodd.util.HashCode.hash;

import java.math.BigDecimal;
import java.io.Serializable;

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
 * @see JDateTime
 * @see DateTimeStamp
 */
public class JulianDateStamp implements Serializable, Cloneable {

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
		return (int) (fraction * 100000000);
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

	// ---------------------------------------------------------------- ctors

	/**
	 * Default empty constructor.
	 */
	public JulianDateStamp() {
	}

	/**
	 * Creates JD from a <code>double</code>.
	 */
	public JulianDateStamp(double jd) {
		set(jd);
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
	public JulianDateStamp(int i, double f) {
		set(i, f);
	}

	/**
	 * Creates JD from <code>BigDecimal</code>.
	 */
	public JulianDateStamp(BigDecimal bd) {
		double d = bd.doubleValue();
		integer = (int) d;
		bd = bd.subtract(new BigDecimal(integer));
		fraction = bd.doubleValue();
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
	@SuppressWarnings({"UnpredictableBigDecimalConstructorCall"})
	public BigDecimal toBigDecimal() {
		BigDecimal bd = new BigDecimal(integer);
		return bd.add(new BigDecimal(fraction));
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


	// ---------------------------------------------------------------- math

	/**
	 * Adds a JD to current instance.
	 */
	public JulianDateStamp add(JulianDateStamp jds) {
		int i = this.integer + jds.integer;
		double f = this.fraction + jds.fraction;
		set(i, f);
		return this;
	}

	/**
	 * Adds a double to current instance.
	 */
	public JulianDateStamp add(double delta) {
		set(this.integer, this.fraction + delta);
		return this;
	}


	/**
	 * Subtracts a JD from current instance.
	 */
	public JulianDateStamp sub(JulianDateStamp jds) {
		int i = this.integer - jds.integer;
		double f = this.fraction -jds.fraction;
		set(i, f);
		return this;
	}

	/**
	 * Subtracts a double from current instance.
	 */
	public JulianDateStamp sub(double delta) {
		set(this.integer, this.fraction - delta);
		return this;
	}

	/**
	 * Sets integer and fractional part with normalization.
	 * Normalization means that if double is out of range,
	 * values will be correctly fixed. 
	 */
	public void set(int i, double f) {
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

	public void set(double jd) {
		integer = (int)jd;
		fraction = jd - (double)integer;
	}


	// ---------------------------------------------------------------- between

	/**
	 * Calculates the number of days between two dates. Returned value is always positive.
	 */
	public int daysBetween(JulianDateStamp otherDate) {
		int difference = daysSpan(otherDate);
		return difference >= 0 ? difference : -difference;
	}

	/**
	 * Returns span between two days. Returned value may be positive (when this date
	 * is after the provided one) or negative (when comparing to future date).
	 */
	public int daysSpan(JulianDateStamp otherDate) {
		int now = getJulianDayNumber();
		int then = otherDate.getJulianDayNumber();
		return now - then;
	}

	// ---------------------------------------------------------------- equals & hashCode

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (this.getClass() != object.getClass()) {
			return false;
		}
		JulianDateStamp stamp = (JulianDateStamp) object;
		return  (stamp.integer == this.integer) &&
				(Double.compare(stamp.fraction, this.fraction) == 0);
	}

	@Override
	public int hashCode() {
		int result = HashCode.SEED;
		result = hash(result, integer);
		result = hash(result, fraction);
		return result;
	}

	// ---------------------------------------------------------------- clone

	@Override
	protected JulianDateStamp clone() {
		return new JulianDateStamp(this.integer, this.fraction);
	}

	// ---------------------------------------------------------------- conversion

	/**
	 * Returns Reduced Julian Date (RJD), used by astronomers.
	 * RJD = JD − 2400000
	 */
	public JulianDateStamp getReducedJulianDate() {
		return new JulianDateStamp(integer - 2400000, fraction);
	}

	public void setReducedJulianDate(double rjd) {
		set(rjd + 2400000);
	}

	/**
	 * Returns Modified Julian Date (MJD), where date starts from midnight rather than noon.
	 * RJD = JD − 2400000.5
	 */
	public JulianDateStamp getModifiedJulianDate() {
		return new JulianDateStamp(integer - 2400000, fraction - 0.5);
	}

	public void setModifiedJulianDate(double mjd) {
		set(mjd + 2400000.5);
	}

	/**
	 * Returns Truncated Julian Day (TJD), introduced by NASA for the space program.
	 * TJD began at midnight at the beginning of May 24, 1968 (Friday).
	 */
	public JulianDateStamp getTruncatedJulianDate() {
		return new JulianDateStamp(integer - 2440000, fraction - 0.5);
	}

	public void setTruncatedJulianDate(double tjd) {
		set(tjd + 2440000.5);
	}
}
