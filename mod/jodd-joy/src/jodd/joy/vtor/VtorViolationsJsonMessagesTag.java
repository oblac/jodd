// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.vtor;

import jodd.vtor.Violation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.List;

/**
 * Tag that renders all validation messages as JSON array.
 */
public class VtorViolationsJsonMessagesTag extends SimpleTagSupport {

	protected List<Violation> violations;

	public void setViolations(List<Violation> violations) {
		this.violations = violations;
	}

	@Override
	public void doTag() throws JspException, IOException {
		PageContext pageContext = ((PageContext) getJspContext());
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

		String output = VtorUtil.createViolationsJsonString(request, violations);
		pageContext.getOut().write(output);
	}

}
