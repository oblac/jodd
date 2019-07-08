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
	public void setEnd(final int end) {
		super.setEnd(end);
		if (isEndSpecified) {
			throw new IllegalArgumentException("End boundary already specified");
		}
		isEndSpecified = true;
	}

	/**
	 * Sets loop count.
	 */
	public void setCount(final int count) {
		setEnd(count);
		isCount = true;
	}

	/**
	 * Sets TO loop value (exclusive).
	 */
	public void setTo(final int to) {
		setEnd(to);
		isExclusive = true;
	}

	protected boolean autoDirection;

	/**
	 * Specifies if direction should be detected from the start and end value.
	 */
	public void setAutoDirection(final boolean autoDirection) {
		this.autoDirection = autoDirection;
	}


	@Override
	public void doTag() throws JspException {
		if (!isEndSpecified) {
			throw new IllegalArgumentException("End boundary of the loop is not specified");
		}

		prepareStepDirection();
		if (isCount) {
			if (end < 0) {
				throw new IllegalArgumentException("Negative count value");
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
