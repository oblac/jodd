// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.util.StringPool;
import jodd.util.StringUtil;

/**
 * Bean template is a string template with JSP-alike
 * markers that indicates where provided context values
 * will be injected.
 */
public class BeanTemplate {

	public static final String MACRO_START = "${";
	public static final String MACRO_END = "}";

	/**
	 * Replaces named macros with context values.
	 * Throws exception if macro name is missing.
	 */
	public static String parse(String template, Object context) {
		return parse(template, context, null);
	}

	public static String parse(String template, BeanTemplateResolver resolver) {
		return parse(template, resolver, null);
	}

	/**
	 * Replaces named macros with context values.
	 * Declared properties are considered during value lookup.
	 * If some macro name is missing, exception may be throw or the
	 * provided string will be used instead.
	 */
	public static String parse(String template, Object context, String missingKeyReplacement) {
		return parse(template, context, missingKeyReplacement, false);
	}

	public static String parse(String template, BeanTemplateResolver resolver, String missingKeyReplacement) {
		return parse(template, resolver, missingKeyReplacement, true);
	}

	protected static String parse(String template, Object context, String missingKeyReplacement, boolean isResolver) {
		BeanTemplateResolver resolver = null;
		if (isResolver) {
			resolver = (BeanTemplateResolver) context;
		}

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

			// find macros end
			ndx += 2;
			int ndx2 = template.indexOf(MACRO_END, ndx);
			if (ndx2 == -1) {
				throw new BeanException("Bad bean template format - unclosed macro at: " + (ndx - 2));
			}

			// detect inner macros, there is no escaping
			int ndx1 = ndx;
			while (ndx1 < ndx2) {
				int n = StringUtil.indexOf(template, MACRO_START, ndx1, ndx2);
				if (n == -1) {
					break;
				}
				ndx1 = n + 2;
			}

			String name = template.substring(ndx1, ndx2);

			// find value and append
			Object value;
			if (missingKeyReplacement == null) {
				if (resolver != null) {
					value = resolver.resolve(name);
				} else {
					value = BeanUtil.getDeclaredProperty(context, name);
				}
				if (value == null) {
					value = StringPool.EMPTY;
				}
			} else {
				if (resolver != null) {
					try {
						value = resolver.resolve(name);
					} catch (Exception ignore) {
						value = null;
					}
				} else {
					value = BeanUtil.getDeclaredPropertySilently(context, name);
				}
				if (value == null) {
					value = missingKeyReplacement;
				}
			}

			if (ndx == ndx1) {
				result.append(value.toString());
				i = ndx2 + 1;
			} else {
				// inner macro
				template = template.substring(0, ndx1 - 2) + value.toString() + template.substring(ndx2 + 1);
				len = template.length();
				i = ndx - 2;
			}
		}
		return result.toString();
	}
}
