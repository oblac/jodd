// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.format;

/**
 * Printf.
 */
public class Printf {

	// ---------------------------------------------------------------- primitives

	public static String str(String format, byte value) {
		return new PrintfFormat(format).form(value);
	}
	public static void out(String format, byte value) {
		System.out.println(str(format, value));
	}


	public static String str(String format, char value) {
		return new PrintfFormat(format).form(value);
	}
	public static void out(String format, char value) {
		System.out.println(str(format, value));
	}


	public static String str(String format, short value) {
		return new PrintfFormat(format).form(value);
	}
	public static void out(String format, short value) {
		System.out.println(str(format, value));
	}


	public static String str(String format, int value) {
		return new PrintfFormat(format).form(value);
	}
	public static void out(String format, int value) {
		System.out.println(str(format, value));
	}


	public static String str(String format, long value) {
		return new PrintfFormat(format).form(value);
	}
	public static void out(String format, long value) {
		System.out.println(str(format, value));
	}


	public static String str(String format, float value) {
		return new PrintfFormat(format).form(value);
	}
	public static void out(String format, float value) {
		System.out.println(str(format, value));
	}


	public static String str(String format, double value) {
		return new PrintfFormat(format).form(value);
	}
	public static void out(String format, double value) {
		System.out.println(str(format, value));
	}


	public static String str(String format, boolean value) {
		return new PrintfFormat(format).form(value);
	}
	public static void out(String format, boolean value) {
		System.out.println(str(format, value));
	}


	public static String str(String format, String value) {
		return new PrintfFormat(format).form(value);
	}
	public static void out(String format, String value) {
		System.out.println(str(format, value));
	}



	// ---------------------------------------------------------------- wrappers

	public static String str(String format, Byte value) {
		return new PrintfFormat(format).form(value.byteValue());
	}
	public static void out(String format, Byte value) {
		System.out.println(str(format, value));
	}
	

	public static String str(String format, Character value) {
		return new PrintfFormat(format).form(value.charValue());
	}
	public static void out(String format, Character value) {
		System.out.println(str(format, value));
	}
	

	public static String str(String format, Short value) {
		return new PrintfFormat(format).form(value.shortValue());
	}
	public static void out(String format, Short value) {
		System.out.println(str(format, value));
	}
	

	public static String str(String format, Integer value) {
		return new PrintfFormat(format).form(value.intValue());
	}
	public static void out(String format, Integer value) {
		System.out.println(str(format, value));
	}
	

	public static String str(String format, Long value) {
		return new PrintfFormat(format).form(value.longValue());
	}
	public static void out(String format, Long value) {
		System.out.println(str(format, value));
	}
	

	public static String str(String format, Float value) {
		return new PrintfFormat(format).form(value.floatValue());
	}
	public static void out(String format, Float value) {
		System.out.println(str(format, value));
	}
	

	public static String str(String format, Double value) {
		return new PrintfFormat(format).form(value.doubleValue());
	}
	public static void out(String format, Double value) {
		System.out.println(str(format, value));
	}
	

	public static String str(String format, Boolean value) {
		return new PrintfFormat(format).form(value.booleanValue());
	}
	public static void out(String format, Boolean value) {
		System.out.println(str(format, value));
	}
	


	// ---------------------------------------------------------------- arrays

	public static String str(String format, byte[] params) {
		PrintfFormat pf = new PrintfFormat();
		for (byte param : params) {
			format = pf.reinit(format).form(param);
		}
	    return format;
	}
	public static void out(String format, byte[] params) {
		System.out.println(str(format, params));
	}


	public static String str(String format, char[] params) {
		PrintfFormat pf = new PrintfFormat();
		for (char param : params) {
			format = pf.reinit(format).form(param);
		}
	    return format;
	}
	public static void out(String format, char[] params) {
		System.out.println(str(format, params));
	}


	public static String str(String format, short[] params) {
		PrintfFormat pf = new PrintfFormat();
		for (short param : params) {
			format = pf.reinit(format).form(param);
		}
	    return format;
	}
	public static void out(String format, short[] params) {
		System.out.println(str(format, params));
	}


	public static String str(String format, int[] params) {
		PrintfFormat pf = new PrintfFormat();
		for (int param : params) {
			format = pf.reinit(format).form(param);
		}
	    return format;
	}
	public static void out(String format, int[] params) {
		System.out.println(str(format, params));
	}


	public static String str(String format, long[] params) {
		PrintfFormat pf = new PrintfFormat();
		for (long param : params) {
			format = pf.reinit(format).form(param);
		}
	    return format;
	}
	public static void out(String format, long[] params) {
		System.out.println(str(format, params));
	}


	public static String str(String format, float[] params) {
		PrintfFormat pf = new PrintfFormat();
		for (float param : params) {
			format = pf.reinit(format).form(param);
		}
	    return format;
	}
	public static void out(String format, float[] params) {
		System.out.println(str(format, params));
	}


	public static String str(String format, double[] params) {
		PrintfFormat pf = new PrintfFormat();
		for (double param : params) {
			format = pf.reinit(format).form(param);
		}
	    return format;
	}
	public static void out(String format, double[] params) {
		System.out.println(str(format, params));
	}


	public static String str(String format, boolean[] params) {
		PrintfFormat pf = new PrintfFormat();
		for (boolean param : params) {
			format = pf.reinit(format).form(param);
		}
	    return format;
	}
	public static void out(String format, boolean[] params) {
		System.out.println(str(format, params));
	}


	public static String str(String format, String[] params) {
		PrintfFormat pf = new PrintfFormat();
		for (String param : params) {
			format = pf.reinit(format).form(param);
		}
	    return format;
	}
	public static void out(String format, String[] params) {
		System.out.println(str(format, params));
	}



	// ---------------------------------------------------------------- wrapper arrays

	public static String str(String format, Byte... params) {
		PrintfFormat pf = new PrintfFormat();
		for (Byte param : params) {
			format = pf.reinit(format).form(param.byteValue());
		}
	    return format;
	}
	public static void out(String format, Byte... params) {
		System.out.println(str(format, params));
	}

	public static String str(String format, Character... params) {
		PrintfFormat pf = new PrintfFormat();
		for (Character param : params) {
			format = pf.reinit(format).form(param.charValue());
		}
	    return format;
	}
	public static void out(String format, Character... params) {
		System.out.println(str(format, params));
	}

	public static String str(String format, Short... params) {
		PrintfFormat pf = new PrintfFormat();
		for (Short param : params) {
			format = pf.reinit(format).form(param.shortValue());
		}
	    return format;
	}
	public static void out(String format, Short... params) {
		System.out.println(str(format, params));
	}

	public static String str(String format, Integer... params) {
		PrintfFormat pf = new PrintfFormat();
		for (Integer param : params) {
			format = pf.reinit(format).form(param.intValue());
		}
	    return format;
	}
	public static void out(String format, Integer... params) {
		System.out.println(str(format, params));
	}

	public static String str(String format, Long... params) {
		PrintfFormat pf = new PrintfFormat();
		for (Long param : params) {
			format = pf.reinit(format).form(param.longValue());
		}
	    return format;
	}
	public static void out(String format, Long... params) {
		System.out.println(str(format, params));
	}

	public static String str(String format, Float... params) {
		PrintfFormat pf = new PrintfFormat();
		for (Float param : params) {
			format = pf.reinit(format).form(param.floatValue());
		}
	    return format;
	}
	public static void out(String format, Float... params) {
		System.out.println(str(format, params));
	}

	public static String str(String format, Double... params) {
		PrintfFormat pf = new PrintfFormat();
		for (Double param : params) {
			format = pf.reinit(format).form(param.doubleValue());
		}
	    return format;
	}
	public static void out(String format, Double... params) {
		System.out.println(str(format, params));
	}

	public static String str(String format, Boolean... params) {
		PrintfFormat pf = new PrintfFormat();
		for (Boolean param : params) {
			format = pf.reinit(format).form(param.booleanValue());
		}
	    return format;
	}
	public static void out(String format, Boolean... params) {
		System.out.println(str(format, params));
	}


	// ---------------------------------------------------------------- object array

	public static String str(String format, Object... params) {
		PrintfFormat pf = new PrintfFormat();
		for (Object param : params) {
			pf.reinit(format);
			if (param instanceof Integer) {
				format = pf.form(((Integer) param).intValue());
			} else if (param instanceof Long) {
				format = pf.form(((Long) param).longValue());
			} else if (param instanceof Character) {
				format = pf.form(((Character) param).charValue());
			} else if (param instanceof Double) {
				format = pf.form(((Double) param).doubleValue());
			} else if (param instanceof Float) {
				format = pf.form(((Float) param).floatValue());
			} else {
				format = pf.form(param.toString());
			}
		}
		return format;
	}
	public static void out(String format, Object... params) {
		System.out.println(str(format, params));
	}

}