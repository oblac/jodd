// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

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

	private List<String> attrs = new ArrayList<String>();
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