// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import jodd.servlet.CsrfShield;

import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Renders the CSRF (Cross-site request forgery) token in the form.
 * <p>
 * http://en.wikipedia.org/wiki/Cross-site_request_forgery
 */
public class CsrfTokenTag extends SimpleTagSupport {

	protected String name;
	/**
	 * Specifies token name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void doTag() throws IOException {
		JspContext jspContext = this.getJspContext();

		// generate token
		HttpServletRequest request = (HttpServletRequest) ((PageContext) jspContext).getRequest();
		HttpSession session = request.getSession();
		String value = CsrfShield.prepareCsrfToken(session);
		if (name == null) {
			name = CsrfShield.CSRF_TOKEN_NAME;
		}
		jspContext.getOut().write("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
	}
}
