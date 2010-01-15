// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import jodd.typeconverter.BooleanConverter;
import jodd.util.CharUtil;

import java.util.List;

/**
 * Various utilities for raw population of HTML forms.
 * Text encoding is provided by {@link HtmlEncoder} class.
 */
public class HtmlFormUtil {

	private static final String EMPTY		= "";
	private static final String CHECKED 	= "checked";
	private static final String IGNORE		= " _i=\"";
	private static final String TRUE		= "true";
	private static final String ENDQUOTE	= "\" ";
	private static final String SELECTED	= "selected";

	// ---------------------------------------------------------------- checked

	/**
	 * Renders checked string.
	 */
	public static String checked(boolean data) {
		return data == true ? CHECKED : EMPTY;
	}

	/**
	 * Renders checked string.
	 */
	public static String checked(Object data) {
		if (data == null) {
			return EMPTY;
		}
		if (BooleanConverter.valueOf(data).booleanValue()) {
			return CHECKED;
		}
		return EMPTY;
	}

	/**
	 * Renders checked string if object exists. Since non-checked checkboxes are not sent through request,
	 * it is assumed that existence of an object means that check box is checked.
	 */
	public static String checkedExist(Object data) {
		return data != null ? CHECKED : EMPTY;
	}
	/**
	 * Renders checked string if its representation equals to specified value.
	 * <p>
	 * May be used for CHECKBOX, RADIO form elements.
	 */
	public static String checked(Object data, String value) {
		if (data == null) {
			return EMPTY;
		}
		if (data.toString().equals(value)) {
			return CHECKED;
		}
		return EMPTY;
	}


	// ---------------------------------------------------------------- checked value

	/**
	 * Shortcut for {@link #checked(Object, String)}. Allows user to write value only once in HTML form.
	 */
	public static String checkedValue(Object data, String value) {
		return value + ENDQUOTE + checked(data, value) + IGNORE;
	}

	/**
	 * Shortcut for {@link #checked(boolean)}.
	 */
	public static String checkedValue(boolean data, String value) {
		return value + ENDQUOTE + checked(data) + IGNORE;
	}

	/**
	 * Shortcut for {@link #checked(boolean)}.
	 */
	public static String checkedValue(Boolean data, String value) {
		return value + ENDQUOTE + checked(data) + IGNORE;
	}

	/**
	 * Shortcut for {@link #checked(Object)} assuming that value equals to "true".
	 */
	public static String checkedValue(Object data) {
		return TRUE + ENDQUOTE + checked(data) + IGNORE;
	}

	public static String checkedValueExist(Object data) {
		return TRUE + ENDQUOTE + checkedExist(data) + IGNORE;
	}


	// ---------------------------------------------------------------- selected

	/**
	 * Checks if objects string representation equals to specified value.
	 * If it does, 'selected' is returned, otherwise an empty string.
	 * <p>
	 *
	 * Usage:
	 * <code>
	 * &lt;option value="option1" &lt;%=FormUtil.selected(value, "option1")%&gt;&gt;option #1&lt;/option&gt;
	 * </code>
	 * <p>
	 *
	 * May be used for OPTION form elements.
	 */
	public static String selected(Object data, String value) {
		if (data == null) {
			return EMPTY;
		}
		if (data.toString().equals(value)) {
			return SELECTED;
		}
		return EMPTY;
	}

	// ---------------------------------------------------------------- selected value

	/**
	 * Shortcut for {@link #selected(Object, String)}. Allows user to write value only once in
	 * HTML form.
	 * <p>
	 *
	 * Usage:
	 * <code>
	 * &lt;option value="&lt;%=FormUtil.selectedValue(value, "option1")%&gt;"&gt;option #1&lt;/option&gt;
	 * </code>
	 */
	public static String selectedValue(Object data, String value) {
		return value + ENDQUOTE + selected(data, value) + IGNORE;
	}

	// ---------------------------------------------------------------- multiple selected

	public static String multiSelected(Object[] data, String value) {
		if (data == null) {
			return EMPTY;
		}
		for (Object obj : data) {
			if (obj.toString().equals(value)) {
				return SELECTED;
			}
		}
		return EMPTY;
	}
	public static String multiSelected(int[] data, String value) {
		if (data == null) {
			return EMPTY;
		}
		for (int i : data) {
			if (Integer.toString(i).equals(value)) {
				return SELECTED;
			}
		}
		return EMPTY;
	}
	public static String multiSelected(long[] data, String value) {
		if (data == null) {
			return EMPTY;
		}
		for (long l : data) {
			if (Long.toString(l).equals(value)) {
				return SELECTED;
			}
		}
		return EMPTY;
	}

	public static String multiSelected(List data, String value) {
		if (data == null) {
			return EMPTY;
		}
		if (data.contains(value)) {
			return SELECTED;
		}
		return EMPTY;
	}

	// ---------------------------------------------------------------- multiple selected value

	public static String multiSelectedValue(Object[] data, String value) {
		return value + ENDQUOTE + multiSelected(data, value) + IGNORE;
	}
	public static String multiSelectedValue(int[] data, String value) {
		return value + ENDQUOTE + multiSelected(data, value) + IGNORE;
	}
	public static String multiSelectedValue(long[] data, String value) {
		return value + ENDQUOTE + multiSelected(data, value) + IGNORE;
	}
	public static String multiSelectedValue(List data, String value) {
		return value + ENDQUOTE + multiSelected(data, value) + IGNORE;
	}

	// ---------------------------------------------------------------- id

	/**
	 * Converts name to safe id value by replacing all non-letter and non-digits characters to '_'.
	 */
	public static String name2id(String name) {
		char[] str = name.toCharArray();
		for (int i = 0; i < str.length; i++) {
			if (CharUtil.isLetterOrDigit(str[i]) == false) {
				str[i] = '_';
			}
		}
		return new String(str);
	}

}
