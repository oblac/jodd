// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.servlet.tag;

import jodd.util.LoopIterator;
import org.junit.jupiter.api.Test;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import static org.junit.jupiter.api.Assertions.*;

public class LoopTagTest {

	@Test
	public void testLoopTag() throws JspException {

		final MockJspFragment jspFragment = new MockJspFragment();
		final MockJspContext jspContext = new MockJspContext();

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
		assertNull(jspContext.getAttribute("s"));    // attribute was cleared

		jspFragment.count = 0;
		loopTag.setModulus(3);
		loopTag.doTag();
	}

}
