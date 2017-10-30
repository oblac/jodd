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

package jodd.typeconverter;

import jodd.bean.JoddBean;
import jodd.datetime.JDateTime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

/**
 * Static version of {@link ConvertBean} of default {@link TypeConverterManagerBean}.
 */
public class Convert {

	/**
	 * Returns default {@link ConvertBean}. We should not store this instance
	 * in a static variable as default {@link TypeConverterManagerBean} may be changed.
	 */
	protected static ConvertBean convertBean() {
		return JoddBean.runtime().convertBean();
	}

	// ---------------------------------------------------------------- @@generated

	public static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue) {
		return convertBean().toBigDecimal(value, defaultValue);
	}

	public static Boolean toBoolean(Object value) {
		return convertBean().toBoolean(value);
	}

	public static Boolean toBoolean(Object value, Boolean defaultValue) {
		return convertBean().toBoolean(value, defaultValue);
	}

	public static boolean toBooleanValue(Object value, boolean defaultValue) {
		return convertBean().toBooleanValue(value, defaultValue);
	}

	public static boolean toBooleanValue(Object value) {
		return convertBean().toBooleanValue(value);
	}

	public static Integer toInteger(Object value) {
		return convertBean().toInteger(value);
	}

	public static Integer toInteger(Object value, Integer defaultValue) {
		return convertBean().toInteger(value, defaultValue);
	}

	public static int toIntValue(Object value, int defaultValue) {
		return convertBean().toIntValue(value, defaultValue);
	}

	public static int toIntValue(Object value) {
		return convertBean().toIntValue(value);
	}

	public static Long toLong(Object value) {
		return convertBean().toLong(value);
	}

	public static Long toLong(Object value, Long defaultValue) {
		return convertBean().toLong(value, defaultValue);
	}

	public static long toLongValue(Object value, long defaultValue) {
		return convertBean().toLongValue(value, defaultValue);
	}

	public static long toLongValue(Object value) {
		return convertBean().toLongValue(value);
	}

	public static Float toFloat(Object value) {
		return convertBean().toFloat(value);
	}

	public static Float toFloat(Object value, Float defaultValue) {
		return convertBean().toFloat(value, defaultValue);
	}

	public static float toFloatValue(Object value, float defaultValue) {
		return convertBean().toFloatValue(value, defaultValue);
	}

	public static float toFloatValue(Object value) {
		return convertBean().toFloatValue(value);
	}

	public static Double toDouble(Object value) {
		return convertBean().toDouble(value);
	}

	public static Double toDouble(Object value, Double defaultValue) {
		return convertBean().toDouble(value, defaultValue);
	}

	public static double toDoubleValue(Object value, double defaultValue) {
		return convertBean().toDoubleValue(value, defaultValue);
	}

	public static double toDoubleValue(Object value) {
		return convertBean().toDoubleValue(value);
	}

	public static Short toShort(Object value) {
		return convertBean().toShort(value);
	}

	public static Short toShort(Object value, Short defaultValue) {
		return convertBean().toShort(value, defaultValue);
	}

	public static short toShortValue(Object value, short defaultValue) {
		return convertBean().toShortValue(value, defaultValue);
	}

	public static short toShortValue(Object value) {
		return convertBean().toShortValue(value);
	}

	public static Character toCharacter(Object value) {
		return convertBean().toCharacter(value);
	}

	public static Character toCharacter(Object value, Character defaultValue) {
		return convertBean().toCharacter(value, defaultValue);
	}

	public static char toCharValue(Object value, char defaultValue) {
		return convertBean().toCharValue(value, defaultValue);
	}

	public static char toCharValue(Object value) {
		return convertBean().toCharValue(value);
	}

	public static Byte toByte(Object value) {
		return convertBean().toByte(value);
	}

	public static Byte toByte(Object value, Byte defaultValue) {
		return convertBean().toByte(value, defaultValue);
	}

	public static byte toByteValue(Object value, byte defaultValue) {
		return convertBean().toByteValue(value, defaultValue);
	}

	public static byte toByteValue(Object value) {
		return convertBean().toByteValue(value);
	}

	public static boolean[] toBooleanArray(Object value) {
		return convertBean().toBooleanArray(value);
	}

	public static int[] toIntegerArray(Object value) {
		return convertBean().toIntegerArray(value);
	}

	public static long[] toLongArray(Object value) {
		return convertBean().toLongArray(value);
	}

	public static float[] toFloatArray(Object value) {
		return convertBean().toFloatArray(value);
	}

	public static double[] toDoubleArray(Object value) {
		return convertBean().toDoubleArray(value);
	}

	public static short[] toShortArray(Object value) {
		return convertBean().toShortArray(value);
	}

	public static char[] toCharacterArray(Object value) {
		return convertBean().toCharacterArray(value);
	}

	public static String toString(Object value) {
		return convertBean().toString(value);
	}

	public static String toString(Object value, String defaultValue) {
		return convertBean().toString(value, defaultValue);
	}

	public static String[] toStringArray(Object value) {
		return convertBean().toStringArray(value);
	}

	public static Class toClass(Object value) {
		return convertBean().toClass(value);
	}

	public static Class[] toClassArray(Object value) {
		return convertBean().toClassArray(value);
	}

	public static JDateTime toJDateTime(Object value) {
		return convertBean().toJDateTime(value);
	}

	public static JDateTime toJDateTime(Object value, JDateTime defaultValue) {
		return convertBean().toJDateTime(value, defaultValue);
	}

	public static Date toDate(Object value) {
		return convertBean().toDate(value);
	}

	public static Date toDate(Object value, Date defaultValue) {
		return convertBean().toDate(value, defaultValue);
	}

	public static Calendar toCalendar(Object value) {
		return convertBean().toCalendar(value);
	}

	public static Calendar toCalendar(Object value, Calendar defaultValue) {
		return convertBean().toCalendar(value, defaultValue);
	}

	public static BigInteger toBigInteger(Object value) {
		return convertBean().toBigInteger(value);
	}

	public static BigInteger toBigInteger(Object value, BigInteger defaultValue) {
		return convertBean().toBigInteger(value, defaultValue);
	}

	public static BigDecimal toBigDecimal(Object value) {
		return convertBean().toBigDecimal(value);
	}
}