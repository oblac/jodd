// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import jodd.typeconverter.Convert;
import jodd.typeconverter.TypeConversionException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * If tag execute body if test condition is <code>true</code>.
 *<p>
 * Test variable is a string. 
 */
public class IfTag extends SimpleTagSupport {

	private String test;
	public void setTest(String test) {
		this.test = test;
	}

	@Override
	public void doTag() throws JspException {
		boolean testValue;
		try {
			testValue = Convert.toBooleanValue(test, false);
		} catch (TypeConversionException ignore) {
			testValue = false;
		}
		if (testValue == true) {
			TagUtil.invokeBody(getJspBody());
		}
	}

}
