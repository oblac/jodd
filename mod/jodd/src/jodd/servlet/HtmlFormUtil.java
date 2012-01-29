// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import jodd.typeconverter.Convert;
import jodd.util.CharUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.util.List;
import java.util.Map;

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
		if (Convert.toBoolean(data)) {
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
		if (data.getClass().isArray()) {
			// checkbox group
			String vs[] = StringUtil.toStringArray(data);
			for (String vsk : vs) {
				if ((vsk != null) && (vsk.equals(value))) {
					return CHECKED;
				}
			}
		} else {
			if (data.toString().equals(value)) {
				return CHECKED;
			}
		}
		return EMPTY;
	}
	
	public static <T> String checked(T[] array, int index) {
		if ((array == null) || (index >= array.length) || (index < 0)) {
			return EMPTY;
		}
		return checked(array[index]);
	}

	public static String checked(boolean[] array, int index) {
		if ((array == null) || (index >= array.length) || (index < 0)) {
			return EMPTY;
		}
		return checked(array[index]);
	}

	public static String checked(byte[] array, int index) {
		if ((array == null) || (index >= array.length) || (index < 0)) {
			return EMPTY;
		}
		return (array[index] != 0) ? CHECKED : EMPTY;
	}

	public static String checked(short[] array, int index) {
		if ((array == null) || (index >= array.length) || (index < 0)) {
			return EMPTY;
		}
		return (array[index] != 0) ? CHECKED : EMPTY;
	}

	public static String array(int[] array, int index) {
		if ((array == null) || (index >= array.length) || (index < 0)) {
			return EMPTY;
		}
		return (array[index] != 0) ? CHECKED : EMPTY;
	}

	public static String array(long[] array, int index) {
		if ((array == null) || (index >= array.length) || (index < 0)) {
			return EMPTY;
		}
		return (array[index] != 0) ? CHECKED : EMPTY;
	}

	public static String array(float[] array, int index) {
		if ((array == null) || (index >= array.length) || (index < 0)) {
			return EMPTY;
		}
		return (array[index] != 0) ? CHECKED : EMPTY;
	}

	public static String array(double[] array, int index) {
		if ((array == null) || (index >= array.length) || (index < 0)) {
			return EMPTY;
		}
		return (array[index] != 0) ? CHECKED : EMPTY;
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

	public static String multiSelected(Object data, String value) {
		if (data == null) {
			return EMPTY;
		}
		Class arrayType = data.getClass().getComponentType();
		if (arrayType != null) {
			if (arrayType.equals(int.class)) {
				return multiSelected((int[]) data, value);
			}
			if (arrayType.equals(long.class)) {
				return multiSelected((long[]) data, value);
			}
			return multiSelected((Object[]) data, value);
		}
		if (data instanceof List) {
			return multiSelected((List) data, value);
		}
		throw new IllegalArgumentException("Invalid multi-selected field data.");
	}

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

	public static String multiSelectedValue(Object data, String value) {
		return value + ENDQUOTE + multiSelected(data, value) + IGNORE;
	}
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

	// ---------------------------------------------------------------- map/list

	public static Object list(List list, int index) {
		if ((list == null) || (index >= list.size()) || (index < 0)) {
			return null;
		}
		return list.get(index);
	}

	public static Object map(Map map, Object key) {
		if (map == null) {
			return null;
		}
		return map.get(key);
	}
	
	// ---------------------------------------------------------------- text
	
	public static String text(Object value) {
		if (value == null) {
			return StringPool.EMPTY;
		}
		return HtmlEncoder.text(value.toString());
	}

}
