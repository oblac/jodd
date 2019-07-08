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

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Just a convenient {@link TypeConverterManager} usage.
 */
public class Converter {

	private static final Converter CONVERTER = new Converter();

	/**
	 * Returns default instance.
	 */
	public static Converter get() {
		return CONVERTER;
	}

	// ---------------------------------------------------------------- boolean

	/**
	 * Converts value to <code>Boolean</code>.
	 */
	public Boolean toBoolean(final Object value) {
		final TypeConverter<Boolean> tc = TypeConverterManager.get().lookup(Boolean.class);
		return tc.convert(value);
	}

	/**
	 * Converts value to <code>Boolean</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Boolean toBoolean(final Object value, final Boolean defaultValue) {
		final Boolean result = toBoolean(value);
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
		final Boolean result = toBoolean(value);
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

	// ---------------------------------------------------------------- integer

	/**
	 * Converts value to <code>Integer</code>.
	 */
	public Integer toInteger(final Object value) {
		final TypeConverter<Integer> tc = TypeConverterManager.get().lookup(Integer.class);
		return tc.convert(value);
	}

	/**
	 * Converts value to <code>Integer</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Integer toInteger(final Object value, final Integer defaultValue) {
		final Integer result = toInteger(value);
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
		final Integer result = toInteger(value);
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

	// ---------------------------------------------------------------- long

	/**
	 * Converts value to <code>Long</code>.
	 */
	public Long toLong(final Object value) {
		final TypeConverter<Long> tc = TypeConverterManager.get().lookup(Long.class);
		return tc.convert(value);
	}

	/**
	 * Converts value to <code>Long</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Long toLong(final Object value, final Long defaultValue) {
		final Long result = toLong(value);
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
		final Long result = toLong(value);
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

	// ---------------------------------------------------------------- float

	/**
	 * Converts value to <code>Float</code>.
	 */
	public Float toFloat(final Object value) {
		final TypeConverter<Float> tc = TypeConverterManager.get().lookup(Float.class);
		return tc.convert(value);
	}

	/**
	 * Converts value to <code>Float</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Float toFloat(final Object value, final Float defaultValue) {
		final Float result = toFloat(value);
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
		final Float result = toFloat(value);
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

	// ---------------------------------------------------------------- double

	/**
	 * Converts value to <code>Double</code>.
	 */
	public Double toDouble(final Object value) {
		final TypeConverter<Double> tc = TypeConverterManager.get().lookup(Double.class);
		return tc.convert(value);
	}

	/**
	 * Converts value to <code>Double</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Double toDouble(final Object value, final Double defaultValue) {
		final Double result = toDouble(value);
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
		final Double result = toDouble(value);
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

	// ---------------------------------------------------------------- short

	/**
	 * Converts value to <code>Short</code>.
	 */
	public Short toShort(final Object value) {
		final TypeConverter<Short> tc = TypeConverterManager.get().lookup(Short.class);
		return tc.convert(value);
	}

	/**
	 * Converts value to <code>Short</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Short toShort(final Object value, final Short defaultValue) {
		final Short result = toShort(value);
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
		final Short result = toShort(value);
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

	// ---------------------------------------------------------------- character

	/**
	 * Converts value to <code>Character</code>.
	 */
	public Character toCharacter(final Object value) {
		final TypeConverter<Character> tc = TypeConverterManager.get().lookup(Character.class);
		return tc.convert(value);
	}

	/**
	 * Converts value to <code>Character</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Character toCharacter(final Object value, final Character defaultValue) {
		final Character result = toCharacter(value);
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
		final Character result = toCharacter(value);
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

	// ---------------------------------------------------------------- byte

	/**
	 * Converts value to <code>Byte</code>.
	 */
	public Byte toByte(final Object value) {
		final TypeConverter<Byte> tc = TypeConverterManager.get().lookup(Byte.class);
		return tc.convert(value);
	}

	/**
	 * Converts value to <code>Byte</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public Byte toByte(final Object value, final Byte defaultValue) {
		final Byte result = toByte(value);
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
		final Byte result = toByte(value);
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

	// ---------------------------------------------------------------- array

	/**
	 * Converts value to <code>boolean[]</code>.
	 */
	public boolean[] toBooleanArray(final Object value) {
		final TypeConverter<boolean[]> tc = TypeConverterManager.get().lookup(boolean[].class);
		return tc.convert(value);
	}

	/**
	 * Converts value to <code>int[]</code>.
	 */
	public int[] toIntegerArray(final Object value) {
		final TypeConverter<int[]> tc = TypeConverterManager.get().lookup(int[].class);
		return tc.convert(value);

	}

	/**
	 * Converts value to <code>long[]</code>.
	 */
	public long[] toLongArray(final Object value) {
		final TypeConverter<long[]> tc = TypeConverterManager.get().lookup(long[].class);
		return tc.convert(value);

	}

	/**
	 * Converts value to <code>float[]</code>.
	 */
	public float[] toFloatArray(final Object value) {
		final TypeConverter<float[]> tc = TypeConverterManager.get().lookup(float[].class);
		return tc.convert(value);
	}

	/**
	 * Converts value to <code>double[]</code>.
	 */
	public double[] toDoubleArray(final Object value) {
		final TypeConverter<double[]> tc = TypeConverterManager.get().lookup(double[].class);
		return tc.convert(value);
	}

	/**
	 * Converts value to <code>short[]</code>.
	 */
	public short[] toShortArray(final Object value) {
		final TypeConverter<short[]> tc = TypeConverterManager.get().lookup(short[].class);
		return tc.convert(value);
	}

	/**
	 * Converts value to <code>char[]</code>.
	 */
	public char[] toCharacterArray(final Object value) {
		final TypeConverter<char[]> tc = TypeConverterManager.get().lookup(char[].class);
		return tc.convert(value);
	}

	// ---------------------------------------------------------------- string

	/**
	 * Converts value to <code>String</code>.
	 */
	public String toString(final Object value) {
		final TypeConverter<String> tc = TypeConverterManager.get().lookup(String.class);
		return tc.convert(value);
	}

	/**
	 * Converts value to <code>String</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public String toString(final Object value, final String defaultValue) {
		final String result = toString(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>String[]</code>.
	 */
	public String[] toStringArray(final Object value) {
		final TypeConverter<String[]> tc = TypeConverterManager.get().lookup(String[].class);
		return tc.convert(value);
	}

	// ---------------------------------------------------------------- class

	/**
	 * Converts value to <code>Class</code>.
	 */
	public Class toClass(final Object value) {
		final TypeConverter<Class> tc = TypeConverterManager.get().lookup(Class.class);
		return tc.convert(value);
	}

	/**
	 * Converts value to <code>Class[]</code>.
	 */
	public Class[] toClassArray(final Object value) {
		final TypeConverter<Class[]> tc = TypeConverterManager.get().lookup(Class[].class);
		return tc.convert(value);
	}

	// ---------------------------------------------------------------- bigs

	/**
	 * Converts value to <code>BigInteger</code>.
	 */
	public BigInteger toBigInteger(final Object value) {
		final TypeConverter<BigInteger> tc = TypeConverterManager.get().lookup(BigInteger.class);
		return tc.convert(value);
	}

	/**
	 * Converts value to <code>BigInteger</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public BigInteger toBigInteger(final Object value, final BigInteger defaultValue) {
		final BigInteger result = toBigInteger(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>BigDecimal</code>.
	 */
	public BigDecimal toBigDecimal(final Object value) {
		final TypeConverter<BigDecimal> tc = TypeConverterManager.get().lookup(BigDecimal.class);
		return tc.convert(value);
	}

	/**
	 * Converts value to <code>BigDecimal</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public BigDecimal toBigDecimal(final Object value, final BigDecimal defaultValue) {
		final BigDecimal result = toBigDecimal(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}
}