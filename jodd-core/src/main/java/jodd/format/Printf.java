// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.format;

/**
 * Printf.
 * @see jodd.format.PrintfFormat
 */
public class Printf {

	// ---------------------------------------------------------------- primitives

	/**
	 * @see jodd.format.PrintfFormat#form(byte)
	 */
	public static String str(String format, byte value) {
		return new PrintfFormat(format).form(value);
	}

	/**
	 * @see jodd.format.PrintfFormat#form(char)
	 */
	public static String str(String format, char value) {
		return new PrintfFormat(format).form(value);
	}

	/**
	 * @see jodd.format.PrintfFormat#form(short)
	 */
	public static String str(String format, short value) {
		return new PrintfFormat(format).form(value);
	}

	/**
	 * @see jodd.format.PrintfFormat#form(int)
	 */
	public static String str(String format, int value) {
		return new PrintfFormat(format).form(value);
	}

	/**
	 * @see jodd.format.PrintfFormat#form(long)
	 */
	public static String str(String format, long value) {
		return new PrintfFormat(format).form(value);
	}

	/**
	 * @see jodd.format.PrintfFormat#form(double)
	 */
	public static String str(String format, float value) {
		return new PrintfFormat(format).form(value);
	}

	/**
	 * @see jodd.format.PrintfFormat#form(double)
	 */
	public static String str(String format, double value) {
		return new PrintfFormat(format).form(value);
	}

	/**
	 * @see jodd.format.PrintfFormat#form(boolean)
	 */
	public static String str(String format, boolean value) {
		return new PrintfFormat(format).form(value);
	}

	// ---------------------------------------------------------------- objects

	public static String str(String format, String value) {
		return new PrintfFormat(format).form(value);
	}

	public static String str(String format, Object param) {
		return new PrintfFormat(format).form(param);
	}

	// ---------------------------------------------------------------- multiple objects

	public static String str(String format, Object... params) {
		PrintfFormat pf = new PrintfFormat();
		for (Object param : params) {
			pf.reinit(format);
			format = pf.form(param);
		}
		return format;
	}

}