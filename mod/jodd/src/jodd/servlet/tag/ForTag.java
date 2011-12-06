// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import javax.servlet.jsp.JspException;

/**
 * For tag simulates simple for loop. For more enhanced looping see {@link jodd.servlet.tag.LoopTag}.
 */
public class ForTag extends LoopingTagSupport {

	@Override
	public void doTag() throws JspException {

		prepareStepDirection();

		loopBody();
	}

}
