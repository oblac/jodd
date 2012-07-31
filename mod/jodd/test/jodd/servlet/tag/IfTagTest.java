// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import junit.framework.TestCase;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

public class IfTagTest extends TestCase {

	public void testIfTag() throws JspException {

		final EmptyJspFragment jspFragment = new EmptyJspFragment();

		IfTag ifTag = new IfTag() {
			@Override
			protected JspFragment getJspBody() {
				return jspFragment;
			}
		};

		ifTag.setTest(String.valueOf(2 == 2));
		ifTag.doTag();

		assertEquals(1, jspFragment.getCount());

		ifTag.setTest(String.valueOf(2 == 3));
		ifTag.doTag();

		assertEquals(1, jspFragment.getCount());

		ifTag.setTest("true");
		ifTag.doTag();

		assertEquals(2, jspFragment.getCount());
	}
}
