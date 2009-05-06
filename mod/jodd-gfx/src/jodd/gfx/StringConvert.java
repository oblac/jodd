package jodd.gfx;

/**
 * Converts Strings to various primitives. Strings are used very often for
 * holding numbers. This class simplifies converting strings to numbers,
 * without need of exception handling.
 */
public final class StringConvert {

	/**
	 * Converts String to float.
	 * @return converted value, or default value if error
	 */
	public static float toFloat(String value, float defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException nfex) {
			return defaultValue;
		}
	}

	/**
	 * Converts String to double.
	 * @return converted value, or default value if error
	 */
	public static double toDouble(String value, double defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException nfex) {
			return defaultValue;
		}
	}

	/**
	 * Converts String to int.
	 * @return converted value, or default value if error
	 */
	public static int toInt(String value, int defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException nfex) {
			return defaultValue;
		}
	}

	/**
	 * Converts String to long.
	 * @return converted value, or default value if error
	 */
	public static long toLong(String value, long defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException nfex) {
			return defaultValue;
		}
	}

	/**
	 * Converts String to byte.
	 * @return converted value, or default value if error
	 */
	public static byte toByte(String value, byte defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		try {
			return Byte.parseByte(value);
		} catch (NumberFormatException nfex) {
			return defaultValue;
		}
	}

}
