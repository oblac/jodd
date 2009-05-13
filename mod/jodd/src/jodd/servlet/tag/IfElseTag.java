// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import jodd.typeconverter.BooleanConverter;
import jodd.typeconverter.TypeConversionException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * Similar to {@link jodd.servlet.tag.IfTag}, provides full IF construct with
 * then and else block.
 */
public class IfElseTag extends SimpleTagSupport {

	private boolean testValue;

	public void setTest(String test) {
		try {
			this.testValue = BooleanConverter.valueOf(test).booleanValue();
		} catch (TypeConversionException tcex) {
			this.testValue = false;
		}
	}

	/**
	 * Returns test value
	 */
	public boolean getTestValue() {
		return testValue;
	}

	@Override
	public void doTag() throws JspException {
		TagUtil.invokeBody(getJspBody());
	}
}

