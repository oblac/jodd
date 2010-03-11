package jodd.joy.vtor;

import jodd.vtor.Violation;
import jodd.vtor.VtorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.List;

public class VtorViolationsTag extends SimpleTagSupport {

	protected List<Violation> violations;

	public void setViolations(List<Violation> violations) {
		this.violations = violations;
	}

	@Override
	public void doTag() throws JspException {
		PageContext pageContext = ((PageContext) getJspContext());
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

		String output = VtorUtil.createViolationsJsonString(request, violations);
		try {
			pageContext.getOut().write(output);
		} catch (IOException ioex) {
			throw new VtorException(ioex);
		}
	}

}
