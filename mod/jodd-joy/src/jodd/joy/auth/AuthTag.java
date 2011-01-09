// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.auth;

import jodd.servlet.tag.TagUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * Execute tag body if user is (or is not) authenticated.
 */
public class AuthTag extends SimpleTagSupport {

	protected boolean auth = true;

	/**
	 * Defines if body should be invoked if user is authenticated.
	 */
	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	@Override
	public void doTag() throws JspException {
		PageContext pageContext = ((PageContext) getJspContext());
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpSession httpSession = request.getSession();

		Object userSession = AuthUtil.getActiveSession(httpSession);
		boolean invokeBody =  (userSession != null) ?  auth : !auth;
		if (invokeBody) {
			TagUtil.invokeBody(getJspBody());
		}
	}

}
