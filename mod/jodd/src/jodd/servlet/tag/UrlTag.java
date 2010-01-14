// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import jodd.servlet.UrlEncoder;
import jodd.servlet.UrlBuilder;
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
		UrlBuilder urlBuilder = UrlEncoder.buildUrl(baseUrl, pageContext);
		for (int i = 0; i < attrs.size(); i += 2) {
			urlBuilder.param(attrs.get(i), attrs.get(i + 1));
		}

		if (var == null) {
			JspWriter out = pageContext.getOut();
			try {
				out.print(urlBuilder.toString());
			} catch (IOException ioex) {
				// ignore
			}
		} else {
			pageContext.setAttribute(var, urlBuilder.toString());
		}
	}
}