// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.tag;

import javax.servlet.jsp.JspException;

/**
 * Loop tag performs enhanced looping in both directions.
 */
public class LoopTag extends LoopingTagSupport {

	protected boolean isEndSpecified;
	protected boolean isExclusive;
	protected boolean isCount;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEnd(int end) {
		super.setEnd(end);
		if (isEndSpecified == true) {
			throw new IllegalArgumentException("End boundary already specified.");
		}
		isEndSpecified = true;
	}

	/**
	 * Sets loop count.
	 */
	public void setCount(int count) {
		setEnd(count);
		isCount = true;
	}

	/**
	 * Sets TO loop value (exclusive).
	 */
	public void setTo(int to) {
		setEnd(to);
		isExclusive = true;
	}

	protected boolean autoDirection;

	/**
	 * Specifies if direction should be detected from the start and end value.
	 */
	public void setAutoDirection(boolean autoDirection) {
		this.autoDirection = autoDirection;
	}


	@Override
	public void doTag() throws JspException {
		if (isEndSpecified == false) {
			throw new IllegalArgumentException("End boundary of the loop is not specified."); 
		}

		prepareStepDirection();
		if (isCount) {
			if (end < 0) {
				throw new IllegalArgumentException("Negative count value.");
			}
			end = start + step * (end - 1);
		}
		prepareStepDirection(autoDirection, false);


		// exclusive
		if (isExclusive) {
			if (step > 0) {
				this.end--;
			} else if (step < 0) {
				this.end++;
			}
		}

		loopBody();
	}
}
