// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.util.StringPool;
import jodd.util.StringUtil;

/**
 * Bean template is a string template with JSP-alike
 * macros for injecting context values.
 * This is a parser for such bean templates.
 * <p>
 * Once set, <code>BeanTemplateParser</code> instance is reusable
 * as it doesn't store any parsing state.
 */
public class BeanTemplateParser {

	public static final String MACRO_START = "${";
	public static final String MACRO_END = "}";

	// ---------------------------------------------------------------- properties

	protected boolean replaceMissingKey = true;
	protected String missingKeyReplacement;
	protected boolean resolveEscapes = true;
	protected String macroStart = MACRO_START;
	protected String macroEnd = MACRO_END;
	protected char escapeChar = '\\';

	public boolean isReplaceMissingKey() {
		return replaceMissingKey;
	}

	/**
	 * Specifies if missing keys should be resolved at all,
	 * <code>true</code> by default.
	 * If <code>false</code> missing keys will be left as it were, i.e.
	 * they will not be replaced.
	 */
	public void setReplaceMissingKey(boolean replaceMissingKey) {
		this.replaceMissingKey = replaceMissingKey;
	}

	public String getMissingKeyReplacement() {
		return missingKeyReplacement;
	}

	/**
	 * Specifies replacement for missing keys. If <code>null</code>
	 * exception will be thrown.
	 */
	public void setMissingKeyReplacement(String missingKeyReplacement) {
		this.missingKeyReplacement = missingKeyReplacement;
	}

	public boolean isResolveEscapes() {
		return resolveEscapes;
	}

	/**
	 * Specifies if escaped values should be resolved. In special usecases,
	 * when the same string has to be processed more then once,
	 * this may be set to <code>false</code> so escaped values
	 * remains.
	 */
	public void setResolveEscapes(boolean resolveEscapes) {
		this.resolveEscapes = resolveEscapes;
	}

	public String getMacroStart() {
		return macroStart;
	}

	/**
	 * Defines macro start string.
	 */
	public void setMacroStart(String macroStart) {
		this.macroStart = macroStart;
	}

	public String getMacroEnd() {
		return macroEnd;
	}

	/**
	 * Defines macro end string.
	 */
	public void setMacroEnd(String macroEnd) {
		this.macroEnd = macroEnd;
	}

	public char getEscapeChar() {
		return escapeChar;
	}

	/**
	 * Defines escape character.
	 */
	public void setEscapeChar(char escapeChar) {
		this.escapeChar = escapeChar;
	}

	// ---------------------------------------------------------------- parse

	/**
	 * Replaces named macros with context values.
	 * All declared properties are considered during value lookup.
	 */
	public String parse(String template, Object context) {
		return parse(template, context, false);
	}

	/**
	 * Replaces named macros using {@link BeanTemplateMacroResolver}.
	 */
	public String parse(String template, BeanTemplateMacroResolver macroResolver) {
		return parse(template, macroResolver, true);
	}

	protected String parse(String template, Object context, boolean isResolver) {
		BeanTemplateMacroResolver macroResolver = null;
		if (isResolver) {
			macroResolver = (BeanTemplateMacroResolver) context;
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
			int j = ndx - 1;
			boolean escape = false;
			int count = 0;

			while ((j >= 0) && (template.charAt(j) == escapeChar)) {
				escape = !escape;
				if (escape) {
					count++;
				}
				j--;
			}
			if (resolveEscapes) {
				result.append(template.substring(i, ndx - count));
			} else {
				result.append(template.substring(i, ndx));
			}
			if (escape == true) {
				result.append(MACRO_START);
				i = ndx + 2;
				continue;
			}

			// find macros end
			ndx += 2;
			int ndx2 = template.indexOf(MACRO_END, ndx);
			if (ndx2 == -1) {
				throw new BeanException("Bad template, unclosed macro at: " + (ndx - 2));
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
			if (missingKeyReplacement != null || replaceMissingKey == false) {
				if (macroResolver != null) {
					try {
						value = macroResolver.resolve(name);
					} catch (Exception ignore) {
						value = null;
					}
				} else {
					value = BeanUtil.getDeclaredPropertySilently(context, name);
				}
				if (value == null) {
					if (replaceMissingKey == true) {
						value = missingKeyReplacement;
					} else {
						value = template.substring(ndx1 - 2, ndx2 + 1);
					}
				}
			} else {
				if (macroResolver != null) {
					value = macroResolver.resolve(name);
				} else {
					value = BeanUtil.getDeclaredProperty(context, name);
				}
				if (value == null) {
					value = StringPool.EMPTY;
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
