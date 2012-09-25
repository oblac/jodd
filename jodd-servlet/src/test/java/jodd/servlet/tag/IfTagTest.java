// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import org.junit.Test;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import static org.junit.Assert.assertEquals;

public class IfTagTest {

	@Test
	public void testIfTag() throws JspException {

		final MockJspFragment jspFragment = new MockJspFragment();

		IfTag ifTag = new IfTag() {
			@Override
			protected JspFragment getJspBody() {
				return jspFragment;
			}
		};

		ifTag.setTest(String.valueOf(2 == 2));
		ifTag.doTag();

		assertEquals(1, jspFragment.count);

		ifTag.setTest(String.valueOf(2 == 3));
		ifTag.doTag();

		assertEquals(1, jspFragment.count);

		ifTag.setTest("true");
		ifTag.doTag();

		assertEquals(2, jspFragment.count);
	}
}
