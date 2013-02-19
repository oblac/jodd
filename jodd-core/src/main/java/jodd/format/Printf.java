// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.format;

/**
 * Printf.
 */
public class Printf {

	// ---------------------------------------------------------------- primitives

	public static String str(String format, byte value) {
		return new PrintfFormat(format).form(value);
	}

	public static String str(String format, char value) {
		return new PrintfFormat(format).form(value);
	}

	public static String str(String format, short value) {
		return new PrintfFormat(format).form(value);
	}

	public static String str(String format, int value) {
		return new PrintfFormat(format).form(value);
	}

	public static String str(String format, long value) {
		return new PrintfFormat(format).form(value);
	}

	public static String str(String format, float value) {
		return new PrintfFormat(format).form(value);
	}

	public static String str(String format, double value) {
		return new PrintfFormat(format).form(value);
	}

	public static String str(String format, boolean value) {
		return new PrintfFormat(format).form(value);
	}

	public static String str(String format, String value) {
		return new PrintfFormat(format).form(value);
	}

	// ---------------------------------------------------------------- objects

	public static String str(String format, Object param) {
		PrintfFormat pf = new PrintfFormat();
		format = print(pf, format, param);
		return format;
	}

	public static String str(String format, Object... params) {
		PrintfFormat pf = new PrintfFormat();
		for (Object param : params) {
			format = print(pf, format, param);
		}
		return format;
	}

	private static String print(PrintfFormat pf, String format, Object param) {
		pf.reinit(format);
		if (param instanceof Number) {
			if (param instanceof Integer) {
				format = pf.form(((Integer) param).intValue());
			} else if (param instanceof Long) {
				format = pf.form(((Long) param).longValue());
			} else if (param instanceof Double) {
				format = pf.form(((Double) param).doubleValue());
			} else if (param instanceof Float) {
				format = pf.form(((Float) param).floatValue());
			} else {
				format = pf.form(((Number)param).intValue());
			}

		} else if (param instanceof Character) {
				format = pf.form(((Character) param).charValue());
		} else {
			format = pf.form(param.toString());
		}
		return format;
	}

}