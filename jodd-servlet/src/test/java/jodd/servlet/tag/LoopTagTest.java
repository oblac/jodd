// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import jodd.util.LoopIterator;
import junit.framework.TestCase;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

public class LoopTagTest extends TestCase {

	public void testLoopTag() throws JspException {

		final MockJspFragment jspFragment = new MockJspFragment();
		final MockJspContext jspContext= new MockJspContext();

		LoopTag loopTag = new LoopTag() {
			@Override
			protected JspContext getJspContext() {
				return jspContext;
			}

			@Override
			protected JspFragment getJspBody() {
				return jspFragment;
			}
		};

		loopTag.setStart(0);
		loopTag.setEnd(2);
		loopTag.doTag();

		assertEquals(3, jspFragment.count);

		jspFragment.count = 0;
		loopTag.setStep(2);
		loopTag.doTag();
		assertEquals(2, jspFragment.count);

		jspFragment.count = 0;
		loopTag.isEndSpecified = false;
		loopTag.setEnd(5);
		loopTag.setStep(1);
		loopTag.doTag();
		assertEquals(6, jspFragment.count);

		jspFragment.count = 0;
		jspFragment.invokeCallback = new MockJspFragment.InvokeCallback() {
			public void onInvoke(int count) {
				LoopIterator s = (LoopIterator) jspContext.getAttribute("s");
				assertNotNull(s);

				assertEquals(count, s.getCount());
				assertEquals(count - 1, s.getValue());
				assertEquals(count - 1, s.getIndex());
				assertEquals(count % s.getModulusValue(), s.getModulus());
			}
		};
		loopTag.setStatus("s");
		loopTag.doTag();
		assertNull(jspContext.getAttribute("s"));	// attribute was cleared

		jspFragment.count = 0;
		loopTag.setModulus(3);
		loopTag.doTag();
	}

}