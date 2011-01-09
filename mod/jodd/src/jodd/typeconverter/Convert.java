
// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.*;
import jodd.mutable.*;
import jodd.datetime.JDateTime;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;

/**
 * One class for simplifier and direct conversions to destination types.
 * <b>DO NOT MODIFY: this source is generated.</b>
 */
public class Convert {



	/**
	 * Converts value to <code>BigDecimal</code>.
	 */
	public static BigDecimal toBigDecimal(Object value) {
		return toBigDecimal(value, null);
	}

	/**
	 * Converts value to <code>BigDecimal</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return BigDecimalConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>BigInteger</code>.
	 */
	public static BigInteger toBigInteger(Object value) {
		return toBigInteger(value, null);
	}

	/**
	 * Converts value to <code>BigInteger</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static BigInteger toBigInteger(Object value, BigInteger defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return BigIntegerConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>boolean</code>.
	 */
	public static boolean toBoolean(Object value) {
		return toBoolean(value, false);
	}

	/**
	 * Converts value to <code>Boolean</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static boolean toBoolean(Object value, boolean defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return BooleanConverter.valueOf(value).booleanValue();
	}

	/**
	 * Converts value to <code>Boolean</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Boolean toBoolean(Object value, Boolean defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return BooleanConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>boolean[]</code>.
	 */
	public static boolean[] toBooleanArray(Object value) {
		return toBooleanArray(value, null);
	}

	/**
	 * Converts value to <code>boolean[]</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static boolean[] toBooleanArray(Object value, boolean[] defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return BooleanArrayConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>byte</code>.
	 */
	public static byte toByte(Object value) {
		return toByte(value, (byte)0);
	}

	/**
	 * Converts value to <code>Byte</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static byte toByte(Object value, byte defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return ByteConverter.valueOf(value).byteValue();
	}

	/**
	 * Converts value to <code>Byte</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Byte toByte(Object value, Byte defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return ByteConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>byte[]</code>.
	 */
	public static byte[] toByteArray(Object value) {
		return toByteArray(value, null);
	}

	/**
	 * Converts value to <code>byte[]</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static byte[] toByteArray(Object value, byte[] defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return ByteArrayConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>char</code>.
	 */
	public static char toCharacter(Object value) {
		return toCharacter(value, (char)0);
	}

	/**
	 * Converts value to <code>Character</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static char toCharacter(Object value, char defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return CharacterConverter.valueOf(value).charValue();
	}

	/**
	 * Converts value to <code>Character</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Character toCharacter(Object value, Character defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return CharacterConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>Class</code>.
	 */
	public static Class toClass(Object value) {
		return toClass(value, null);
	}

	/**
	 * Converts value to <code>Class</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Class toClass(Object value, Class defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return ClassConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>Class[]</code>.
	 */
	public static Class[] toClassArray(Object value) {
		return toClassArray(value, null);
	}

	/**
	 * Converts value to <code>Class[]</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Class[] toClassArray(Object value, Class[] defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return ClassArrayConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>double</code>.
	 */
	public static double toDouble(Object value) {
		return toDouble(value, (double)0);
	}

	/**
	 * Converts value to <code>Double</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static double toDouble(Object value, double defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return DoubleConverter.valueOf(value).doubleValue();
	}

	/**
	 * Converts value to <code>Double</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Double toDouble(Object value, Double defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return DoubleConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>double[]</code>.
	 */
	public static double[] toDoubleArray(Object value) {
		return toDoubleArray(value, null);
	}

	/**
	 * Converts value to <code>double[]</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static double[] toDoubleArray(Object value, double[] defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return DoubleArrayConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>float</code>.
	 */
	public static float toFloat(Object value) {
		return toFloat(value, (float)0);
	}

	/**
	 * Converts value to <code>Float</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static float toFloat(Object value, float defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return FloatConverter.valueOf(value).floatValue();
	}

	/**
	 * Converts value to <code>Float</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Float toFloat(Object value, Float defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return FloatConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>float[]</code>.
	 */
	public static float[] toFloatArray(Object value) {
		return toFloatArray(value, null);
	}

	/**
	 * Converts value to <code>float[]</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static float[] toFloatArray(Object value, float[] defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return FloatArrayConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>int</code>.
	 */
	public static int toInteger(Object value) {
		return toInteger(value, 0);
	}

	/**
	 * Converts value to <code>Integer</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static int toInteger(Object value, int defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return IntegerConverter.valueOf(value).intValue();
	}

	/**
	 * Converts value to <code>Integer</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Integer toInteger(Object value, Integer defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return IntegerConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>int[]</code>.
	 */
	public static int[] toIntegerArray(Object value) {
		return toIntegerArray(value, null);
	}

	/**
	 * Converts value to <code>int[]</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static int[] toIntegerArray(Object value, int[] defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return IntegerArrayConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>JDateTime</code>.
	 */
	public static JDateTime toJDateTime(Object value) {
		return toJDateTime(value, null);
	}

	/**
	 * Converts value to <code>JDateTime</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static JDateTime toJDateTime(Object value, JDateTime defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return JDateTimeConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>long</code>.
	 */
	public static long toLong(Object value) {
		return toLong(value, 0);
	}

	/**
	 * Converts value to <code>Long</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static long toLong(Object value, long defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return LongConverter.valueOf(value).longValue();
	}

	/**
	 * Converts value to <code>Long</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Long toLong(Object value, Long defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return LongConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>long[]</code>.
	 */
	public static long[] toLongArray(Object value) {
		return toLongArray(value, null);
	}

	/**
	 * Converts value to <code>long[]</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static long[] toLongArray(Object value, long[] defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return LongArrayConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>MutableByte</code>.
	 */
	public static MutableByte toMutableByte(Object value) {
		return toMutableByte(value, null);
	}

	/**
	 * Converts value to <code>MutableByte</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static MutableByte toMutableByte(Object value, MutableByte defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return MutableByteConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>MutableDouble</code>.
	 */
	public static MutableDouble toMutableDouble(Object value) {
		return toMutableDouble(value, null);
	}

	/**
	 * Converts value to <code>MutableDouble</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static MutableDouble toMutableDouble(Object value, MutableDouble defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return MutableDoubleConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>MutableFloat</code>.
	 */
	public static MutableFloat toMutableFloat(Object value) {
		return toMutableFloat(value, null);
	}

	/**
	 * Converts value to <code>MutableFloat</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static MutableFloat toMutableFloat(Object value, MutableFloat defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return MutableFloatConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>MutableInteger</code>.
	 */
	public static MutableInteger toMutableInteger(Object value) {
		return toMutableInteger(value, null);
	}

	/**
	 * Converts value to <code>MutableInteger</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static MutableInteger toMutableInteger(Object value, MutableInteger defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return MutableIntegerConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>MutableLong</code>.
	 */
	public static MutableLong toMutableLong(Object value) {
		return toMutableLong(value, null);
	}

	/**
	 * Converts value to <code>MutableLong</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static MutableLong toMutableLong(Object value, MutableLong defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return MutableLongConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>MutableShort</code>.
	 */
	public static MutableShort toMutableShort(Object value) {
		return toMutableShort(value, null);
	}

	/**
	 * Converts value to <code>MutableShort</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static MutableShort toMutableShort(Object value, MutableShort defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return MutableShortConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>short</code>.
	 */
	public static short toShort(Object value) {
		return toShort(value, (short)0);
	}

	/**
	 * Converts value to <code>Short</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static short toShort(Object value, short defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return ShortConverter.valueOf(value).shortValue();
	}

	/**
	 * Converts value to <code>Short</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Short toShort(Object value, Short defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return ShortConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>short[]</code>.
	 */
	public static short[] toShortArray(Object value) {
		return toShortArray(value, null);
	}

	/**
	 * Converts value to <code>short[]</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static short[] toShortArray(Object value, short[] defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return ShortArrayConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>String</code>.
	 */
	public static String toString(Object value) {
		return toString(value, null);
	}

	/**
	 * Converts value to <code>String</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static String toString(Object value, String defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return StringConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>String[]</code>.
	 */
	public static String[] toStringArray(Object value) {
		return toStringArray(value, null);
	}

	/**
	 * Converts value to <code>String[]</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static String[] toStringArray(Object value, String[] defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return StringArrayConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>URI</code>.
	 */
	public static URI toURI(Object value) {
		return toURI(value, null);
	}

	/**
	 * Converts value to <code>URI</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static URI toURI(Object value, URI defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return URIConverter.valueOf(value);
	}


	/**
	 * Converts value to <code>URL</code>.
	 */
	public static URL toURL(Object value) {
		return toURL(value, null);
	}

	/**
	 * Converts value to <code>URL</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static URL toURL(Object value, URL defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return URLConverter.valueOf(value);
	}


	public static Object toObject(Object value, Class destinationType) {
		if (value == null) {
			return null;
		}
		TypeConverter converter = TypeConverterManager.lookup(destinationType);
		if (converter == null) {
			throw new TypeConversionException("Unable to convert value to type: '" + destinationType.getName() + "'.");
		}
		return converter.convert(value);
	}


}