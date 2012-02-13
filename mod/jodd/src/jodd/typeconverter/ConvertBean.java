// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.datetime.JDateTime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

public class ConvertBean {

	/**
	 * List of common types. Used for faster lookup and when needed to be used
	 * by other converters.
	 */
	protected Class[] commonTypes = new Class[] {
			Boolean.class,		// 0
			boolean.class,
			Integer.class,		// 2
			int.class,
			Long.class,			// 4
			long.class,
			Float.class,		// 6
			float.class,
			Double.class,		// 8
			double.class,
			Short.class,		// 10
			short.class,
			Character.class,	// 12
			char.class,
			Byte.class,			// 14
			byte.class,

			boolean[].class,	// 16
			int[].class,		// 17
			long[].class,		// 18
			float[].class,		// 19
			double[].class,		// 20
			short[].class,		// 21
			char[].class,		// 22

			String.class,		// 23
			String[].class,		// 24
			Class.class,		// 25
			Class[].class,		// 26

			JDateTime.class,	// 27
			Date.class,			// 28
			Calendar.class,		// 29

			BigInteger.class,	// 30
			BigDecimal.class,	// 31
	};

	/**
	 * Common type converters, filled up during the registration.
	 */
	protected TypeConverter[] typeConverters = new TypeConverter[commonTypes.length];


	public void register(Class type, TypeConverter typeConverter) {
		for (int i = 0; i < commonTypes.length; i++) {
			Class commonType = commonTypes[i];
			if (type == commonType) {
				typeConverters[i] = typeConverter;
				break;
			}
		}
	}

	// ---------------------------------------------------------------- @@generated

	/**
	 * Converts value to <code>Boolean</code>.
	 */
	public Boolean toBoolean(Object value) {
		return (Boolean) typeConverters[0].convert(value);
	}

	/**
	 * Converts value to <code>boolean</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public boolean toBoolean(Object value, boolean defaultValue) {
		Boolean result = (Boolean) typeConverters[1].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.booleanValue();
	}

	/**
	 * Converts value to <code>boolean</code> with common default value.
	 */
	public boolean toBooleanValue(Object value) {
		return toBoolean(value, false);
	}


	/**
	 * Converts value to <code>Integer</code>.
	 */
	public Integer toInteger(Object value) {
		return (Integer) typeConverters[2].convert(value);
	}

	/**
	 * Converts value to <code>int</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public int toInteger(Object value, int defaultValue) {
		Integer result = (Integer) typeConverters[3].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.intValue();
	}

	/**
	 * Converts value to <code>int</code> with common default value.
	 */
	public int toIntegerValue(Object value) {
		return toInteger(value, 0);
	}


	/**
	 * Converts value to <code>Long</code>.
	 */
	public Long toLong(Object value) {
		return (Long) typeConverters[4].convert(value);
	}

	/**
	 * Converts value to <code>long</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public long toLong(Object value, long defaultValue) {
		Long result = (Long) typeConverters[5].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.longValue();
	}

	/**
	 * Converts value to <code>long</code> with common default value.
	 */
	public long toLongValue(Object value) {
		return toLong(value, 0);
	}


	/**
	 * Converts value to <code>Float</code>.
	 */
	public Float toFloat(Object value) {
		return (Float) typeConverters[6].convert(value);
	}

	/**
	 * Converts value to <code>float</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public float toFloat(Object value, float defaultValue) {
		Float result = (Float) typeConverters[7].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.floatValue();
	}

	/**
	 * Converts value to <code>float</code> with common default value.
	 */
	public float toFloatValue(Object value) {
		return toFloat(value, 0);
	}


	/**
	 * Converts value to <code>Double</code>.
	 */
	public Double toDouble(Object value) {
		return (Double) typeConverters[8].convert(value);
	}

	/**
	 * Converts value to <code>double</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public double toDouble(Object value, double defaultValue) {
		Double result = (Double) typeConverters[9].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.doubleValue();
	}

	/**
	 * Converts value to <code>double</code> with common default value.
	 */
	public double toDoubleValue(Object value) {
		return toDouble(value, 0);
	}


	/**
	 * Converts value to <code>Short</code>.
	 */
	public Short toShort(Object value) {
		return (Short) typeConverters[10].convert(value);
	}

	/**
	 * Converts value to <code>short</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public short toShort(Object value, short defaultValue) {
		Short result = (Short) typeConverters[11].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.shortValue();
	}

	/**
	 * Converts value to <code>short</code> with common default value.
	 */
	public short toShortValue(Object value) {
		return toShort(value, (short) 0);
	}


	/**
	 * Converts value to <code>Character</code>.
	 */
	public Character toCharacter(Object value) {
		return (Character) typeConverters[12].convert(value);
	}

	/**
	 * Converts value to <code>char</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public char toCharacter(Object value, char defaultValue) {
		Character result = (Character) typeConverters[13].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.charValue();
	}

	/**
	 * Converts value to <code>char</code> with common default value.
	 */
	public char toCharacterValue(Object value) {
		return toCharacter(value, (char) 0);
	}


	/**
	 * Converts value to <code>Byte</code>.
	 */
	public Byte toByte(Object value) {
		return (Byte) typeConverters[14].convert(value);
	}

	/**
	 * Converts value to <code>byte</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public byte toByte(Object value, byte defaultValue) {
		Byte result = (Byte) typeConverters[15].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.byteValue();
	}

	/**
	 * Converts value to <code>byte</code> with common default value.
	 */
	public byte toByteValue(Object value) {
		return toByte(value, (byte) 0);
	}


	/**
	 * Converts value to <code>boolean[]</code>.
	 */
	public boolean[] toBooleanArray(Object value) {
		return (boolean[]) typeConverters[16].convert(value);
	}


	/**
	 * Converts value to <code>int[]</code>.
	 */
	public int[] toIntegerArray(Object value) {
		return (int[]) typeConverters[17].convert(value);
	}


	/**
	 * Converts value to <code>long[]</code>.
	 */
	public long[] toLongArray(Object value) {
		return (long[]) typeConverters[18].convert(value);
	}


	/**
	 * Converts value to <code>float[]</code>.
	 */
	public float[] toFloatArray(Object value) {
		return (float[]) typeConverters[19].convert(value);
	}


	/**
	 * Converts value to <code>double[]</code>.
	 */
	public double[] toDoubleArray(Object value) {
		return (double[]) typeConverters[20].convert(value);
	}


	/**
	 * Converts value to <code>short[]</code>.
	 */
	public short[] toShortArray(Object value) {
		return (short[]) typeConverters[21].convert(value);
	}


	/**
	 * Converts value to <code>char[]</code>.
	 */
	public char[] toCharacterArray(Object value) {
		return (char[]) typeConverters[22].convert(value);
	}


	/**
	 * Converts value to <code>String</code>.
	 */
	public String toString(Object value) {
		return (String) typeConverters[23].convert(value);
	}


	/**
	 * Converts value to <code>String[]</code>.
	 */
	public String[] toStringArray(Object value) {
		return (String[]) typeConverters[24].convert(value);
	}


	/**
	 * Converts value to <code>Class</code>.
	 */
	public Class toClass(Object value) {
		return (Class) typeConverters[25].convert(value);
	}


	/**
	 * Converts value to <code>Class[]</code>.
	 */
	public Class[] toClassArray(Object value) {
		return (Class[]) typeConverters[26].convert(value);
	}


	/**
	 * Converts value to <code>JDateTime</code>.
	 */
	public JDateTime toJDateTime(Object value) {
		return (JDateTime) typeConverters[27].convert(value);
	}


	/**
	 * Converts value to <code>Date</code>.
	 */
	public Date toDate(Object value) {
		return (Date) typeConverters[28].convert(value);
	}


	/**
	 * Converts value to <code>Calendar</code>.
	 */
	public Calendar toCalendar(Object value) {
		return (Calendar) typeConverters[29].convert(value);
	}


	/**
	 * Converts value to <code>BigInteger</code>.
	 */
	public BigInteger toBigInteger(Object value) {
		return (BigInteger) typeConverters[30].convert(value);
	}


	/**
	 * Converts value to <code>BigDecimal</code>.
	 */
	public BigDecimal toBigDecimal(Object value) {
		return (BigDecimal) typeConverters[31].convert(value);
	}
}