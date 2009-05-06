// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * Switch tag.
 */
public class SwitchTag extends SimpleTagSupport {

	private Object value;
	private boolean valueFounded;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void valueFounded() {
		valueFounded = true;
	}

	public boolean isValueFounded() {
		return valueFounded;
	}

	@Override
	public void doTag() throws JspException {
		TagUtil.invokeBody(getJspBody());
	}


}
