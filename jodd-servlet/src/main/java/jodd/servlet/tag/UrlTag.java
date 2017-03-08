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

package jodd.servlet.tag;

import jodd.util.URLCoder;
import jodd.util.StringPool;

import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Url tag creates full URL.
 */
public class UrlTag extends SimpleTagSupport implements DynamicAttributes {

	protected String baseUrl;
	/**
	 * Sets base url value.
	 */
	public void set_(String value) {
		this.baseUrl = value;
	}

	protected String var;
	/**
	 * Sets optional variable name.
	 */
	public void set_var(String value) {
		this.var = value;
	}

	private final List<String> attrs = new ArrayList<>();
	public void setDynamicAttribute(String uri, String localName, Object value) {
		attrs.add(localName);
		attrs.add(value == null ? StringPool.EMPTY : value.toString());
	}

	@Override
	public void doTag() throws JspException {
		PageContext pageContext = (PageContext) getJspContext();
		URLCoder.Builder builder = URLCoder.build(baseUrl);

		for (int i = 0; i < attrs.size(); i += 2) {
			builder.queryParam(attrs.get(i), attrs.get(i + 1));
		}

		if (var == null) {
			JspWriter out = pageContext.getOut();
			try {
				out.print(builder.toString());
			} catch (IOException ioex) {
				// ignore
			}
		} else {
			pageContext.setAttribute(var, builder.toString());
		}
	}
}