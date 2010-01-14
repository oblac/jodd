// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Removes attribute from the scope.
 */
public class UnsetTag extends SimpleTagSupport {

	protected String name;
	private static final String SCOPE_APPLICATION = "application";
	private static final String SCOPE_SESSION = "session";
	private static final String SCOPE_REQUEST = "request";
	private static final String SCOPE_PAGE = "page";

	public void setName(String name) {
		this.name = name;
	}

	protected String scope;

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public void doTag() throws JspException {
		PageContext pageContext = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

		String scopeValue = scope != null ? scope.toLowerCase() : SCOPE_PAGE;
		if (scopeValue.equals(SCOPE_APPLICATION)) {
            request.getSession().getServletContext().removeAttribute(name);
        } else if (scopeValue.equals(SCOPE_SESSION)) {
            request.getSession().removeAttribute(name);
        } else if (scopeValue.equals(SCOPE_REQUEST)) {
            request.removeAttribute(name);
        } else if (scopeValue.equals(SCOPE_PAGE)) {
            pageContext.removeAttribute(name);
        } else {
			throw new JspException("Invalid scope: '" + scope + "'.");
        }
	}
}
