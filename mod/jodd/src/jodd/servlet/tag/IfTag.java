// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import jodd.typeconverter.BooleanConverter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * If tag execute body if test condition is <code>true</code>.
 *<p>
 * Test variable is a string, since {@link jodd.typeconverter.BooleanConverter} is used to
 * perform the conversation and it offers more friendly conversation then jsp. 
 */
public class IfTag extends SimpleTagSupport {

	private String test;
	public void setTest(String test) {
		this.test = test;
	}

	@Override
	public void doTag() throws JspException {
		Boolean value = BooleanConverter.valueOf(test);
		if (value.booleanValue() == true) {
			TagUtil.invokeBody(getJspBody());
		}
	}

}
