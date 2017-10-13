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

package jodd.joy.i18n;

import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import jodd.util.HtmlEncoder;
import jodd.util.StringUtil;
import static jodd.joy.i18n.LocalizationUtil.findMessage;
import static jodd.joy.i18n.LocalizationUtil.findDefaultMessage;

/**
 * Renders text output. Text is key in the resource bundle. Tag supports variables.
 * If variable name starts with 'key' then variable represents another key that will
 * be lookup from the resources.
 */
public class TextTag extends SimpleTagSupport implements DynamicAttributes {

	private static final String UNKNOWN_PREFIX = "???";
	private static final String UNKNOWN_SUFFIX = "\u00BF\u00BF\u00BF";
	private static final String KEY_ATTR_NAME = "key";

	protected String key;
	public void setKey(String key) {
		this.key = key;
	}

	protected boolean defaultOnly;

	/**
	 * Sets only default resource bundles.
	 */
	public void setDefaultOnly(String defaultOnly) {
		this.defaultOnly = Boolean.parseBoolean(defaultOnly);
	}

	private final List<String[]> params = new ArrayList<>();

	public void setDynamicAttribute(String uri, String localName, Object value) {
		params.add(new String[] {localName, StringUtil.toSafeString(value)});
	}

	@Override
	public void doTag() throws JspException {
		PageContext pageContext = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

		key = key.trim();
		String message;
		if (StringUtil.isEmpty(key)) {
			return;
		}

		message = defaultOnly ? findDefaultMessage(request, key) : findMessage(request, key);
		if (message == null) {
			message = UNKNOWN_PREFIX + key + UNKNOWN_SUFFIX;
		} else {
			for (String[] param : params) {
				String paramName = param[0];
				String paramValue = param[1];
				String value = paramValue;
				if (paramName.startsWith(KEY_ATTR_NAME)) {
					value = defaultOnly ? findDefaultMessage(request, paramValue) : findMessage(request, paramValue);
					if (value == null) {
						value = UNKNOWN_PREFIX + paramValue + UNKNOWN_SUFFIX;
					}
				}
				message = StringUtil.replace(message, '{' + paramName + '}', value);
			}
		}

		JspWriter out = pageContext.getOut();
		try {
			out.print(HtmlEncoder.text(message));
		} catch (IOException ioex) {
			// ignore
		}
	}
}
