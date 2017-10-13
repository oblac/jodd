// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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
				Object value = BeanUtil.declaredSilent.getProperty(context, macroName);

				if (value == null) {
					return null;
				}
				return value.toString();
			}
		};
	}
}
