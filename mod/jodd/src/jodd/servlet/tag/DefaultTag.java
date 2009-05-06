// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class DefaultTag extends SimpleTagSupport {

	@Override
	public void doTag() throws JspException {
		JspTag parent = getParent();
		if (parent == null || !(parent instanceof SwitchTag)) {
			throw new JspException("Parent Switch tag is required.", null);
		}

		SwitchTag switchTag = (SwitchTag) parent;
		if (switchTag.isValueFounded() == false) {
			TagUtil.invokeBody(getJspBody());
		}
	}
}