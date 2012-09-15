// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.util.StringTemplateParser;

/**
 * Bean template is a string template with JSP-alike
 * macros for injecting context values.
 * This is a parser for such bean templates.
 * <p>
 * Once set, <code>BeanTemplateParser</code> instance is reusable
 * as it doesn't store any parsing state.
 * <p>
 * Based on {@link StringTemplateParser}.
 */
public class BeanTemplateParser extends StringTemplateParser {

	/**
	 * Replaces named macros with context values.
	 * All declared properties are considered during value lookup.
	 */
	public String parse(String template, Object context) {
		return parse(template, createBeanMacroResolver(context));
	}

	/**
	 * Creates bean-backed <code>MacroResolver</code>.
	 */
	public static MacroResolver createBeanMacroResolver(final Object context) {
		return new MacroResolver() {
			public String resolve(String macroName) {
				Object value = BeanUtil.getDeclaredProperty(context, macroName);

				if (value == null) {
					return null;
				}
				return value.toString();
			}
		};
	}
}
