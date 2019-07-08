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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * Support for looping tags.
 */
public abstract class LoopingTagSupport extends SimpleTagSupport {

	protected int start;
	protected int end;
	protected int step = 1;
	protected String status;
	protected int modulus = 2;

	// ---------------------------------------------------------------- attributes

	/**
	 * Sets the first loop value (inclusive).
	 */
	public void setStart(final int start) {
		this.start = start;
	}

	/**
	 * Sets the end loop value (inclusive).
	 */
	public void setEnd(final int end) {
		this.end = end;
	}

	/**
	 * Specifies the loop step. If step is 0, it will be set to +1 or -1,
	 * depending on start and end values.
	 */
	public void setStep(final int step) {
		this.step = step;
	}

	/**
	 * Specifies {@link jodd.util.LoopIterator status} variable name. If omitted, status will not be used.
	 */
	public void setStatus(final String status) {
		this.status = status;
	}

	/**
	 * Specifies modulus value for the looping status.
	 */
	public void setModulus(final int modulus) {
		this.modulus = modulus;
	}


	// ---------------------------------------------------------------- helper methods

	/**
	 * Shorter variant of {@link #prepareStepDirection(boolean, boolean)}.
	 */
	protected void prepareStepDirection() {
		if (step == 0) {
			step = (start <= end) ? 1 : -1;
		}
	}

	/**
	 * Prepares step value. If step is 0, it will be set to +1 or -1, depending on start and end value.
	 * <p>
	 * If autoDirection flag is <code>true</code> then it is assumed that step is positive,
	 * and that direction (step sign) should be detected from start and end value.
	 * <p>
	 * If checkDirection flag is <code>true</code> than it checks loop direction (step sign) based on
	 * start and end value. Throws an exception if direction is invalid.
	 * If autoDirection is set, direction checking is skipped.
	 */
	protected void prepareStepDirection(final boolean autoDirection, final boolean checkDirection) {
		if (step == 0) {
			step = (start <= end) ? 1 : -1;
			return;
		}
		if (autoDirection) {
			if (step < 0) {
				throw new IllegalArgumentException("Step value can't be negative: " + step);
			}
			if (start > end) {
				step = -step;
			}
			return;
		}
		if (checkDirection) {
			if (start < end) {
				if (step < 0) {
					throw new IllegalArgumentException("Negative step value for increasing loop");
				}
				return;
			}
			if (start > end) {
				if (step > 0) {
					throw new IllegalArgumentException("Positive step value for decreasing loop");
				}
			}
		}
	}


	/**
	 * Loops body.
	 */
	protected void loopBody() throws JspException {
		JspFragment body = getJspBody();
		if (body == null) {
			return;
		}

		LoopIterator loopIterator = new LoopIterator(start, end, step, modulus);
		if (status != null) {
			getJspContext().setAttribute(status, loopIterator);
		}
		while (loopIterator.next()) {
			TagUtil.invokeBody(body);
		}
		if (status != null) {
			getJspContext().removeAttribute(status);
		}
	}
}
