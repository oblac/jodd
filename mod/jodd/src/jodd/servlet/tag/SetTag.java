// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Sets some value to scope. Default scope is 'page'.
 */
public class SetTag extends SimpleTagSupport {

	protected String name;

	public void setName(String name) {
		this.name = name;
	}

	protected String scope;

	public void setScope(String scope) {
		this.scope = scope;
	}

	protected Object value;

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public void doTag() throws JspException {
		PageContext pageContext = (PageContext) getJspContext();
		TagUtil.setScopeAttribute(name, value, scope, pageContext);
	}

}
