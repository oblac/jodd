// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.vtor;

import jodd.vtor.Violation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * Tag that renders message of single message.
 */
public class VtorViolationMessageTag extends SimpleTagSupport {

	protected Violation violation;

	public void setViolation(Violation violation) {
		this.violation = violation;
	}

	@Override
	public void doTag() throws JspException, IOException {
		PageContext pageContext = ((PageContext) getJspContext());
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

		String output = VtorUtil.resolveValidationMessage(request, violation);

		pageContext.getOut().write(output);
	}

}
