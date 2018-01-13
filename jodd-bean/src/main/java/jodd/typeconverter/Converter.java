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
 * Simple and fast and direct conversion.
 */
public class Converter {

	/**
	 * Returns default instance.
	 */
	public static Converter get() {
		return JoddBean.get().converter();
	}

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

	public void register(final Class type, final TypeConverter typeConverter) {
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
	public Boolean toBoolean(final Object value) {
		return (Boolean) typeConverters[0].convert(value);
	}

	/**
	 * Converts value to <code>Boolean</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Boolean toBoolean(final Object value, final Boolean defaultValue) {
		Boolean result = (Boolean) typeConverters[0].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>boolean</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public boolean toBooleanValue(final Object value, final boolean defaultValue) {
		Boolean result = (Boolean) typeConverters[1].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.booleanValue();
	}

	/**
	 * Converts value to <code>boolean</code> with common default value.
	 */
	public boolean toBooleanValue(final Object value) {
		return toBooleanValue(value, false);
	}


	/**
	 * Converts value to <code>Integer</code>.
	 */
	public Integer toInteger(final Object value) {
		return (Integer) typeConverters[2].convert(value);
	}

	/**
	 * Converts value to <code>Integer</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Integer toInteger(final Object value, final Integer defaultValue) {
		Integer result = (Integer) typeConverters[2].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>int</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public int toIntValue(final Object value, final int defaultValue) {
		Integer result = (Integer) typeConverters[3].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.intValue();
	}

	/**
	 * Converts value to <code>int</code> with common default value.
	 */
	public int toIntValue(final Object value) {
		return toIntValue(value, 0);
	}


	/**
	 * Converts value to <code>Long</code>.
	 */
	public Long toLong(final Object value) {
		return (Long) typeConverters[4].convert(value);
	}

	/**
	 * Converts value to <code>Long</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Long toLong(final Object value, final Long defaultValue) {
		Long result = (Long) typeConverters[4].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>long</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public long toLongValue(final Object value, final long defaultValue) {
		Long result = (Long) typeConverters[5].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.longValue();
	}

	/**
	 * Converts value to <code>long</code> with common default value.
	 */
	public long toLongValue(final Object value) {
		return toLongValue(value, 0);
	}


	/**
	 * Converts value to <code>Float</code>.
	 */
	public Float toFloat(final Object value) {
		return (Float) typeConverters[6].convert(value);
	}

	/**
	 * Converts value to <code>Float</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Float toFloat(final Object value, final Float defaultValue) {
		Float result = (Float) typeConverters[6].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>float</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public float toFloatValue(final Object value, final float defaultValue) {
		Float result = (Float) typeConverters[7].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.floatValue();
	}

	/**
	 * Converts value to <code>float</code> with common default value.
	 */
	public float toFloatValue(final Object value) {
		return toFloatValue(value, 0);
	}


	/**
	 * Converts value to <code>Double</code>.
	 */
	public Double toDouble(final Object value) {
		return (Double) typeConverters[8].convert(value);
	}

	/**
	 * Converts value to <code>Double</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Double toDouble(final Object value, final Double defaultValue) {
		Double result = (Double) typeConverters[8].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>double</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public double toDoubleValue(final Object value, final double defaultValue) {
		Double result = (Double) typeConverters[9].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.doubleValue();
	}

	/**
	 * Converts value to <code>double</code> with common default value.
	 */
	public double toDoubleValue(final Object value) {
		return toDoubleValue(value, 0);
	}


	/**
	 * Converts value to <code>Short</code>.
	 */
	public Short toShort(final Object value) {
		return (Short) typeConverters[10].convert(value);
	}

	/**
	 * Converts value to <code>Short</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Short toShort(final Object value, final Short defaultValue) {
		Short result = (Short) typeConverters[10].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>short</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public short toShortValue(final Object value, final short defaultValue) {
		Short result = (Short) typeConverters[11].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.shortValue();
	}

	/**
	 * Converts value to <code>short</code> with common default value.
	 */
	public short toShortValue(final Object value) {
		return toShortValue(value, (short) 0);
	}


	/**
	 * Converts value to <code>Character</code>.
	 */
	public Character toCharacter(final Object value) {
		return (Character) typeConverters[12].convert(value);
	}

	/**
	 * Converts value to <code>Character</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Character toCharacter(final Object value, final Character defaultValue) {
		Character result = (Character) typeConverters[12].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>char</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public char toCharValue(final Object value, final char defaultValue) {
		Character result = (Character) typeConverters[13].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.charValue();
	}

	/**
	 * Converts value to <code>char</code> with common default value.
	 */
	public char toCharValue(final Object value) {
		return toCharValue(value, (char) 0);
	}


	/**
	 * Converts value to <code>Byte</code>.
	 */
	public Byte toByte(final Object value) {
		return (Byte) typeConverters[14].convert(value);
	}

	/**
	 * Converts value to <code>Byte</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Byte toByte(final Object value, final Byte defaultValue) {
		Byte result = (Byte) typeConverters[14].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>byte</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public byte toByteValue(final Object value, final byte defaultValue) {
		Byte result = (Byte) typeConverters[15].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.byteValue();
	}

	/**
	 * Converts value to <code>byte</code> with common default value.
	 */
	public byte toByteValue(final Object value) {
		return toByteValue(value, (byte) 0);
	}

	/**
	 * Converts value to <code>boolean[]</code>.
	 */
	public boolean[] toBooleanArray(final Object value) {
		return (boolean[]) typeConverters[16].convert(value);
	}

	/**
	 * Converts value to <code>int[]</code>.
	 */
	public int[] toIntegerArray(final Object value) {
		return (int[]) typeConverters[17].convert(value);
	}

	/**
	 * Converts value to <code>long[]</code>.
	 */
	public long[] toLongArray(final Object value) {
		return (long[]) typeConverters[18].convert(value);
	}

	/**
	 * Converts value to <code>float[]</code>.
	 */
	public float[] toFloatArray(final Object value) {
		return (float[]) typeConverters[19].convert(value);
	}

	/**
	 * Converts value to <code>double[]</code>.
	 */
	public double[] toDoubleArray(final Object value) {
		return (double[]) typeConverters[20].convert(value);
	}

	/**
	 * Converts value to <code>short[]</code>.
	 */
	public short[] toShortArray(final Object value) {
		return (short[]) typeConverters[21].convert(value);
	}

	/**
	 * Converts value to <code>char[]</code>.
	 */
	public char[] toCharacterArray(final Object value) {
		return (char[]) typeConverters[22].convert(value);
	}

	/**
	 * Converts value to <code>String</code>.
	 */
	public String toString(final Object value) {
		return (String) typeConverters[23].convert(value);
	}

	/**
	 * Converts value to <code>String</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public String toString(final Object value, final String defaultValue) {
		String result = (String) typeConverters[23].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>String[]</code>.
	 */
	public String[] toStringArray(final Object value) {
		return (String[]) typeConverters[24].convert(value);
	}

	/**
	 * Converts value to <code>Class</code>.
	 */
	public Class toClass(final Object value) {
		return (Class) typeConverters[25].convert(value);
	}

	/**
	 * Converts value to <code>Class[]</code>.
	 */
	public Class[] toClassArray(final Object value) {
		return (Class[]) typeConverters[26].convert(value);
	}

	/**
	 * Converts value to <code>JDateTime</code>.
	 */
	public JDateTime toJDateTime(final Object value) {
		return (JDateTime) typeConverters[27].convert(value);
	}

	/**
	 * Converts value to <code>JDateTime</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public JDateTime toJDateTime(final Object value, final JDateTime defaultValue) {
		JDateTime result = (JDateTime) typeConverters[27].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>Date</code>.
	 */
	public Date toDate(final Object value) {
		return (Date) typeConverters[28].convert(value);
	}

	/**
	 * Converts value to <code>Date</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Date toDate(final Object value, final Date defaultValue) {
		Date result = (Date) typeConverters[28].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>Calendar</code>.
	 */
	public Calendar toCalendar(final Object value) {
		return (Calendar) typeConverters[29].convert(value);
	}

	/**
	 * Converts value to <code>Calendar</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Calendar toCalendar(final Object value, final Calendar defaultValue) {
		Calendar result = (Calendar) typeConverters[29].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>BigInteger</code>.
	 */
	public BigInteger toBigInteger(final Object value) {
		return (BigInteger) typeConverters[30].convert(value);
	}

	/**
	 * Converts value to <code>BigInteger</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public BigInteger toBigInteger(final Object value, final BigInteger defaultValue) {
		BigInteger result = (BigInteger) typeConverters[30].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>BigDecimal</code>.
	 */
	public BigDecimal toBigDecimal(final Object value) {
		return (BigDecimal) typeConverters[31].convert(value);
	}

	/**
	 * Converts value to <code>BigDecimal</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public BigDecimal toBigDecimal(final Object value, final BigDecimal defaultValue) {
		BigDecimal result = (BigDecimal) typeConverters[31].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}
}