// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * Switch tag provides string comparison of its value with inner {@link jodd.servlet.tag.CaseTag case} values.
 */
public class SwitchTag extends SimpleTagSupport {

	private String value;
	private boolean valueFounded;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
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
