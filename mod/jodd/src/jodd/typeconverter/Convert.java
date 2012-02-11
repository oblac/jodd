package jodd.typeconverter;

import jodd.typeconverter.impl.*;
import jodd.mutable.*;
import jodd.datetime.JDateTime;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Calendar;

/**
 * One class for simplifier and direct conversions to destination types.
 * <b>DO NOT MODIFY: this source is generated.</b>
 */
public class Convert {


	// ---------------------------------------------------------------- BigDecimal

	public static BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();

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
		return bigDecimalConverter.convert(value);
	}


	// ---------------------------------------------------------------- BigInteger

	public static BigIntegerConverter bigIntegerConverter = new BigIntegerConverter();

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
		return bigIntegerConverter.convert(value);
	}


	// ---------------------------------------------------------------- Boolean

	public static BooleanConverter booleanConverter = new BooleanConverter();

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
		return booleanConverter.convert(value).booleanValue();
	}

	/**
	 * Converts value to <code>Boolean</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Boolean toBoolean(Object value, Boolean defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return booleanConverter.convert(value);
	}


	// ---------------------------------------------------------------- boolean[]

	public static BooleanArrayConverter booleanArrayConverter = new BooleanArrayConverter();

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
		return booleanArrayConverter.convert(value);
	}


	// ---------------------------------------------------------------- Byte

	public static ByteConverter byteConverter = new ByteConverter();

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
		return byteConverter.convert(value).byteValue();
	}

	/**
	 * Converts value to <code>Byte</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Byte toByte(Object value, Byte defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return byteConverter.convert(value);
	}


	// ---------------------------------------------------------------- byte[]

	public static ByteArrayConverter byteArrayConverter = new ByteArrayConverter();

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
		return byteArrayConverter.convert(value);
	}


	// ---------------------------------------------------------------- Character

	public static CharacterConverter characterConverter = new CharacterConverter();

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
		return characterConverter.convert(value).charValue();
	}

	/**
	 * Converts value to <code>Character</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Character toCharacter(Object value, Character defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return characterConverter.convert(value);
	}


	// ---------------------------------------------------------------- Class

	public static ClassConverter classConverter = new ClassConverter();

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
		return classConverter.convert(value);
	}


	// ---------------------------------------------------------------- Class[]

	public static ClassArrayConverter classArrayConverter = new ClassArrayConverter();

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
		return classArrayConverter.convert(value);
	}


	// ---------------------------------------------------------------- Double

	public static DoubleConverter doubleConverter = new DoubleConverter();

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
		return doubleConverter.convert(value).doubleValue();
	}

	/**
	 * Converts value to <code>Double</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Double toDouble(Object value, Double defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return doubleConverter.convert(value);
	}


	// ---------------------------------------------------------------- double[]

	public static DoubleArrayConverter doubleArrayConverter = new DoubleArrayConverter();

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
		return doubleArrayConverter.convert(value);
	}


	// ---------------------------------------------------------------- Float

	public static FloatConverter floatConverter = new FloatConverter();

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
		return floatConverter.convert(value).floatValue();
	}

	/**
	 * Converts value to <code>Float</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Float toFloat(Object value, Float defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return floatConverter.convert(value);
	}


	// ---------------------------------------------------------------- float[]

	public static FloatArrayConverter floatArrayConverter = new FloatArrayConverter();

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
		return floatArrayConverter.convert(value);
	}


	// ---------------------------------------------------------------- Integer

	public static IntegerConverter integerConverter = new IntegerConverter();

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
		return integerConverter.convert(value).intValue();
	}

	/**
	 * Converts value to <code>Integer</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Integer toInteger(Object value, Integer defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return integerConverter.convert(value);
	}


	// ---------------------------------------------------------------- int[]

	public static IntegerArrayConverter integerArrayConverter = new IntegerArrayConverter();

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
		return integerArrayConverter.convert(value);
	}


	// ---------------------------------------------------------------- JDateTime

	public static JDateTimeConverter jDateTimeConverter = new JDateTimeConverter();

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
		return jDateTimeConverter.convert(value);
	}


	// ---------------------------------------------------------------- Long

	public static LongConverter longConverter = new LongConverter();

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
		return longConverter.convert(value).longValue();
	}

	/**
	 * Converts value to <code>Long</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Long toLong(Object value, Long defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return longConverter.convert(value);
	}


	// ---------------------------------------------------------------- long[]

	public static LongArrayConverter longArrayConverter = new LongArrayConverter();

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
		return longArrayConverter.convert(value);
	}


	// ---------------------------------------------------------------- MutableByte

	public static MutableByteConverter mutableByteConverter = new MutableByteConverter();

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
		return mutableByteConverter.convert(value);
	}


	// ---------------------------------------------------------------- MutableDouble

	public static MutableDoubleConverter mutableDoubleConverter = new MutableDoubleConverter();

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
		return mutableDoubleConverter.convert(value);
	}


	// ---------------------------------------------------------------- MutableFloat

	public static MutableFloatConverter mutableFloatConverter = new MutableFloatConverter();

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
		return mutableFloatConverter.convert(value);
	}


	// ---------------------------------------------------------------- MutableInteger

	public static MutableIntegerConverter mutableIntegerConverter = new MutableIntegerConverter();

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
		return mutableIntegerConverter.convert(value);
	}


	// ---------------------------------------------------------------- MutableLong

	public static MutableLongConverter mutableLongConverter = new MutableLongConverter();

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
		return mutableLongConverter.convert(value);
	}


	// ---------------------------------------------------------------- MutableShort

	public static MutableShortConverter mutableShortConverter = new MutableShortConverter();

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
		return mutableShortConverter.convert(value);
	}


	// ---------------------------------------------------------------- Short

	public static ShortConverter shortConverter = new ShortConverter();

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
		return shortConverter.convert(value).shortValue();
	}

	/**
	 * Converts value to <code>Short</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Short toShort(Object value, Short defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return shortConverter.convert(value);
	}


	// ---------------------------------------------------------------- short[]

	public static ShortArrayConverter shortArrayConverter = new ShortArrayConverter();

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
		return shortArrayConverter.convert(value);
	}


	// ---------------------------------------------------------------- String

	public static StringConverter stringConverter = new StringConverter();

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
		return stringConverter.convert(value);
	}


	// ---------------------------------------------------------------- String[]

	public static StringArrayConverter stringArrayConverter = new StringArrayConverter();

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
		return stringArrayConverter.convert(value);
	}


	// ---------------------------------------------------------------- URI

	public static URIConverter uRIConverter = new URIConverter();

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
		return uRIConverter.convert(value);
	}


	// ---------------------------------------------------------------- URL

	public static URLConverter uRLConverter = new URLConverter();

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
		return uRLConverter.convert(value);
	}


	// ---------------------------------------------------------------- Date

	public static DateConverter dateConverter = new DateConverter();

	/**
	 * Converts value to <code>Date</code>.
	 */
	public static Date toDate(Object value) {
		return toDate(value, null);
	}

	/**
	 * Converts value to <code>Date</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Date toDate(Object value, Date defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return dateConverter.convert(value);
	}


	// ---------------------------------------------------------------- Calendar

	public static CalendarConverter calendarConverter = new CalendarConverter();

	/**
	 * Converts value to <code>Calendar</code>.
	 */
	public static Calendar toCalendar(Object value) {
		return toCalendar(value, null);
	}

	/**
	 * Converts value to <code>Calendar</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static Calendar toCalendar(Object value, Calendar defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return calendarConverter.convert(value);
	}
}