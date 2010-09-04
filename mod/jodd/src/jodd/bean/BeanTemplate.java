// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

/**
 * Bean template is a string template with JSP-alike
 * markers that indicates where provided context values
 * will be injected.
 */
public class BeanTemplate {

	private static final String MACRO_START = "${";

	/**
	 * Replaces named macros with context values.
	 * Declared properties are considered during value lookup.
	 */
	public static String parse(String template, Object context) {
		StringBuilder result = new StringBuilder(template.length());
		int i = 0;
		int len = template.length();
		while (i < len) {
			int ndx = template.indexOf(MACRO_START, i);
			if (ndx == -1) {
				result.append(i == 0 ? template : template.substring(i));
				break;
			}

			// check escaped
			int j = ndx - 1; boolean escape = false; int count = 0;
			while ((j >= 0) && (template.charAt(j) == '\\')) {
				escape = !escape;
				if (escape) {
					count++;
				}
				j--;
			}
			result.append(template.substring(i, ndx - count));
			if (escape == true) {
				result.append(MACRO_START);
				i = ndx + 2;
				continue;
			}

			// find macro end
			ndx += 2;
			int ndx2 = template.indexOf('}', ndx);
			if (ndx2 == -1) {
				throw new BeanException("Bad bean template format - unclosed macro at: " + (ndx - 2));
			}
			String name = template.substring(ndx, ndx2);
			Object value = BeanUtil.getDeclaredProperty(context, name);
			if (value != null) {
				result.append(value.toString());
			}
			i = ndx2 + 1;
		}
		return result.toString();
	}
}
