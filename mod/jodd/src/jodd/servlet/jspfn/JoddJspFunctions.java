// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.jspfn;

import jodd.datetime.JDateTime;
import jodd.servlet.HtmlEncoder;
import jodd.servlet.JspValueResolver;
import jodd.util.StringUtil;
import jodd.util.ObjectUtil;
import static jodd.util.StringPool.EMPTY;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;

/**
 * Nice big set of JSP functions.
 */
public class JoddJspFunctions {

	public static String valueEnc(String name, PageContext pageContext) {
		return HtmlEncoder.text(JspValueResolver.resolveValue(name, pageContext));
	}

	public static String attributeEnc(String name, PageContext pageContext) {
		return HtmlEncoder.text(JspValueResolver.resolveAttribute(name, pageContext));
	}

	public static String propertyEnc(String name, PageContext pageContext) {
		return HtmlEncoder.text(JspValueResolver.resolveProperty(name, pageContext));
	}

	/**
	 * Returns default value if provided value is <code>null</code>.
	 */
	public static Object defaultValue(Object value, Object defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	/**
	 * Returns default string if value is <code>null</code> or empty.
	 */
	public static String defaultString(String value, String defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (StringUtil.isEmpty(value)) {
			return defaultValue;
		}
		return value;
	}


	// ---------------------------------------------------------------- string manipulation

	/**
	 * Joins strings.
	 */
	public static String join(String[] array, String separator) {
		if (array == null) {
			return EMPTY;
		}
		if (separator == null) {
			separator = EMPTY;
		}
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				result.append(separator);
			}
			String value = array[i];
			result.append(value != null ? value : EMPTY);
		}
		return result.toString();
	}

	public static String join2(String one, String two) {
		if (one == null) {
			one = EMPTY;
		}
		if (two == null) {
			return one;
		}
		return one + two;
	}

	/**
	 * Converts all of the characters of the input string to upper case.
	 */
	public static String toUpperCase(String input) {
		if (input == null) {
			return EMPTY;
		}
		return input.toUpperCase();
	}

	/**
	 * Converts all of the characters of the input string to lower case.
	 */
	public static String toLowerCase(String input) {
		if (input == null) {
			return EMPTY;
		}
		return input.toLowerCase();
	}

	public static String capitalize(String input) {
		if (input == null) {
			return EMPTY;
		}
		return StringUtil.capitalize(input);
	}

	public static String uncapitalize(String input) {
		if (input == null) {
			return EMPTY;
		}
		return StringUtil.uncapitalize(input);
	}

	public static String trim(String input) {
		if (input == null) {
			return EMPTY;
		}
		return input.trim();
	}

	public static String replace(String input, String substringBefore, String substringAfter) {
		if (input == null) {
			input = EMPTY;
		}
		if (input.length() == 0) {
			return EMPTY;
		}
		if (substringBefore == null) {
			substringBefore = EMPTY;
		}
		if (substringBefore.length() == 0) {
			return input;
		}
		return StringUtil.replace(input, substringBefore, substringAfter);
	}

	// ---------------------------------------------------------------- substrings

	public static int indexOf(String input, String substring) {
		if (input == null) {
			input = EMPTY;
		}
		if (substring == null) {
			substring = EMPTY;
		}
		return input.indexOf(substring);
	}

	public static boolean contains(String input, String substring) {
		return indexOf(input, substring) != -1;
	}

	public static boolean containsIgnoreCase(String input, String substring) {
		if (input == null) {
			input = EMPTY;
		}
		if (substring == null) {
			substring = EMPTY;
		}
		return StringUtil.indexOfIgnoreCase(input, substring) != -1;
	}

	public static boolean startsWith(String input, String substring) {
		if (input == null) {
			input = EMPTY;
		}
		if (substring == null) {
			substring = EMPTY;
		}
		return input.startsWith(substring);
	}

	public static boolean startsWithIgnoreCase(String input, String substring) {
		if (input == null) {
			input = EMPTY;
		}
		if (substring == null) {
			substring = EMPTY;
		}
		return StringUtil.startsWithIgnoreCase(input, substring);
	}

	public static boolean endsWith(String input, String substring) {
		if (input == null) {
			input = EMPTY;
		}
		if (substring == null) {
			substring = EMPTY;
		}
		return input.endsWith(substring);
	}

	public static boolean endsWithIgnoreCase(String input, String substring) {
		if (input == null) {
			input = EMPTY;
		}
		if (substring == null) {
			substring = EMPTY;
		}
		return StringUtil.endsWithIgnoreCase(input, substring);
	}



	public static String substring(String input, int beginIndex, int endIndex) {
		if (input == null) {
			input = EMPTY;
		}
		if (beginIndex >= input.length()) {
			return EMPTY;
		}
		if (beginIndex < 0) {
			beginIndex = 0;
		}
		if (endIndex < 0 || endIndex > input.length()) {
			endIndex = input.length();
		}
		if (endIndex < beginIndex) {
			return EMPTY;
		}
		return input.substring(beginIndex, endIndex);
	}

	public static String substringAfter(String input, String substring) {
		if (input == null) {
			input = EMPTY;
		}
		if (input.length() == 0) {
			return EMPTY;
		}
		if (substring == null) {
			substring = EMPTY;
		}
		if (substring.length() == 0) {
			return input;
		}

		int index = input.indexOf(substring);
		if (index == -1) {
			return EMPTY;
		} else {
			return input.substring(index + substring.length());
		}
	}

	public static String substringBefore(String input, String substring) {
		if (input == null) {
			input = EMPTY;
		}
		if (input.length() == 0) {
			return EMPTY;
		}
		if (substring == null) {
			substring = EMPTY;
		}
		if (substring.length() == 0) {
			return EMPTY;
		}

		int index = input.indexOf(substring);
		if (index == -1) {
			return EMPTY;
		} else {
			return input.substring(0, index);
		}
	}

	// ---------------------------------------------------------------- collections

	/**
	 * Returns the length of provided object (collection, array and so on).
* If object doesn't have a length, exception is thrown.
	 */
	public static int length(Object obj) throws JspTagException {
		int result = ObjectUtil.length(obj);
		if (result == -1) {
			throw new JspTagException("Provided object does not have length.");
		}
		return result;
	}

	public static boolean containsElement(Object obj, Object element) {
		return ObjectUtil.containsElement(obj, element);
	}

	// ---------------------------------------------------------------- test

	/**
	 * Tests condition and returns <code>true</code> of <code>false</code> value.
	 * Works like ternary operator.
	 */
	public static Object test(boolean condition, Object trueValue, Object falseValue) {
		return condition ? trueValue : falseValue;
	}


	// ---------------------------------------------------------------- datetime

	/**
	 * Formats jdatetime.
	 */
	public static String fmtTime(JDateTime jdt, String format) {
		return jdt.toString(format);
	}


}
