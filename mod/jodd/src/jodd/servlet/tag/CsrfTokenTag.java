// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

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

	@Override
	public void doTag() throws IOException {
		JspContext jspContext = this.getJspContext();

		// generate token
		HttpServletRequest request = (HttpServletRequest) ((PageContext) jspContext).getRequest();
		HttpSession session = request.getSession();
		String value = CsrfShield.prepareCsrfToken(session);
		jspContext.getOut().write("<input type=\"hidden\" name=\"" + CsrfShield.CSRF_TOKEN_NAME + "\" value=\"" + value + "\"/>");
	}
}
