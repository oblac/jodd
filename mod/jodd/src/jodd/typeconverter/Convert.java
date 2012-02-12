// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.datetime.JDateTime;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Calendar;

/**
 * Static version of {@link ConvertBean} of default {@link TypeConverterManagerBean}.
 */
public class Convert {

	/**
	 * Returns default {@link ConvertBean}. We should not store this instance
	 * in a static variable as default {@link TypeConverterManagerBean} may be changed.
	 */
	protected static ConvertBean getConvertBean() {
		return TypeConverterManager.getDefaultTypeConverterManager().getConvertBean();
	}

	// ---------------------------------------------------------------- @@generated

	public static Boolean toBoolean(Object value) {
		return getConvertBean().toBoolean(value);
	}

	public static boolean toBoolean(Object value, boolean defaultValue) {
		return getConvertBean().toBoolean(value, defaultValue);
	}

	public static boolean toBooleanValue(Object value) {
		return getConvertBean().toBooleanValue(value);
	}

	public static Integer toInteger(Object value) {
		return getConvertBean().toInteger(value);
	}

	public static int toInteger(Object value, int defaultValue) {
		return getConvertBean().toInteger(value, defaultValue);
	}

	public static int toIntegerValue(Object value) {
		return getConvertBean().toIntegerValue(value);
	}

	public static Long toLong(Object value) {
		return getConvertBean().toLong(value);
	}

	public static long toLong(Object value, long defaultValue) {
		return getConvertBean().toLong(value, defaultValue);
	}

	public static long toLongValue(Object value) {
		return getConvertBean().toLongValue(value);
	}

	public static Float toFloat(Object value) {
		return getConvertBean().toFloat(value);
	}

	public static float toFloat(Object value, float defaultValue) {
		return getConvertBean().toFloat(value, defaultValue);
	}

	public static float toFloatValue(Object value) {
		return getConvertBean().toFloatValue(value);
	}

	public static Double toDouble(Object value) {
		return getConvertBean().toDouble(value);
	}

	public static double toDouble(Object value, double defaultValue) {
		return getConvertBean().toDouble(value, defaultValue);
	}

	public static double toDoubleValue(Object value) {
		return getConvertBean().toDoubleValue(value);
	}

	public static Short toShort(Object value) {
		return getConvertBean().toShort(value);
	}

	public static short toShort(Object value, short defaultValue) {
		return getConvertBean().toShort(value, defaultValue);
	}

	public static short toShortValue(Object value) {
		return getConvertBean().toShortValue(value);
	}

	public static Character toCharacter(Object value) {
		return getConvertBean().toCharacter(value);
	}

	public static char toCharacter(Object value, char defaultValue) {
		return getConvertBean().toCharacter(value, defaultValue);
	}

	public static char toCharacterValue(Object value) {
		return getConvertBean().toCharacterValue(value);
	}

	public static boolean[] toBooleanArray(Object value) {
		return getConvertBean().toBooleanArray(value);
	}

	public static int[] toIntegerArray(Object value) {
		return getConvertBean().toIntegerArray(value);
	}

	public static long[] toLongArray(Object value) {
		return getConvertBean().toLongArray(value);
	}

	public static float[] toFloatArray(Object value) {
		return getConvertBean().toFloatArray(value);
	}

	public static double[] toDoubleArray(Object value) {
		return getConvertBean().toDoubleArray(value);
	}

	public static short[] toShortArray(Object value) {
		return getConvertBean().toShortArray(value);
	}

	public static char[] toCharacterArray(Object value) {
		return getConvertBean().toCharacterArray(value);
	}

	public static String toString(Object value) {
		return getConvertBean().toString(value);
	}

	public static String[] toStringArray(Object value) {
		return getConvertBean().toStringArray(value);
	}

	public static Class toClass(Object value) {
		return getConvertBean().toClass(value);
	}

	public static Class[] toClassArray(Object value) {
		return getConvertBean().toClassArray(value);
	}

	public static JDateTime toJDateTime(Object value) {
		return getConvertBean().toJDateTime(value);
	}

	public static Date toDate(Object value) {
		return getConvertBean().toDate(value);
	}

	public static Calendar toCalendar(Object value) {
		return getConvertBean().toCalendar(value);
	}

	public static BigInteger toBigInteger(Object value) {
		return getConvertBean().toBigInteger(value);
	}

	public static BigDecimal toBigDecimal(Object value) {
		return getConvertBean().toBigDecimal(value);
	}

}