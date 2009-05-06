// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import jodd.util.LoopIterator;

import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.JspException;

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
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * Sets the end loop value (inclusive).
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * Specifies the loop step. If step is 0, it will be set to +1 or -1,
	 * depending on start and end values.
	 */
	public void setStep(int step) {
		this.step = step;
	}

	/**
	 * Specifies {@link jodd.util.LoopIterator status} variable name. If omitted, status will not be used.
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Specifies modulus value for the looping status.
	 */
	public void setModulus(int modulus) {
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
	 * If checkDirection flas is <code>true</code> than it checks loop direction (step sign) based on
	 * start and end value. Throws an exception if direction is invalid.
	 * If autoDirection is set, direction checking is skipped.
	 */
	protected void prepareStepDirection(boolean autoDirection, boolean checkDirection) {
		if (step == 0) {
			step = (start <= end) ? 1 : -1;
			return;
		}
		if (autoDirection == true) {
			if (step < 0) {
				throw new IllegalArgumentException("Step value can't be negative: '" + step + "'.");
			}
			if (start > end) {
				step = -step;
			}
			return;
		}
		if (checkDirection == true) {
			if (start < end) {
				if (step < 0) {
					throw new IllegalArgumentException("Negative step value for increasing loop.");
				}
				return;
			}
			if (start > end) {
				if (step > 0) {
					throw new IllegalArgumentException("Positive step value for decreasing loop.");
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
