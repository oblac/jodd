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

/**
 * Status of iteration of some looping tag.
 * The {@link LoopTag iterator tag} can export an IteratorStatus object so that
 * one can get information about the status of the iteration, such as:
 * <ul>
 * <li>index: current iteration index, starts on 0 and increments in one on every iteration</li>
 * <li>count: iterations so far, starts on 1. count is always index + 1</li>
 * <li>first: true if index == 0</li>
 * <li>even: true if (index + 1) % 2 == 0</li>
 * <li>last: true if current iteration is the last iteration</li>
 * <li>odd: true if (index + 1) % 2 == 1</li>
 * </ul>
 * <p>Example</p>
 * <pre>{@code
 *   <re:iter status="status" value='%{0, 1}'>
 *      Index: <s:property value="%{#status.index}" /> <br>
 *      Count: <s:property value="%{#status.count}" /> <br>
 *   </re:iter>
 * }</pre>
 * <p>will print</p>
 * <pre>
 *      Index: 0
 *      Count: 1
 *      Index: 1
 *      Count: 2
 * </pre>
 */
public class IteratorStatus {

	protected final int modulus;
	protected boolean last;
	protected int count;

	public IteratorStatus() {
		this.modulus = 2;
	}
	public IteratorStatus(final int modulus) {
		this.modulus = modulus;
	}

	/**
	 * Returns current item count (1-based).
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Returns current index (zero-based).
	 */
	public int getIndex() {
		return count - 1;
	}

	/**
	 * Returns <code>true</code> if current count is even.
	 */
	public boolean isEven() {
		return (count % 2) == 0;
	}

	/**
	 * Returns <code>true</code> if current count is odd.
	 */
	public boolean isOdd() {
		return (count % 2) == 1;
	}

	/**
	 * Calculates modulus of current count.
	 */
	public int modulus(final int operand) {
		return count % operand;
	}

	/**
	 * Returns modulus of current count. 
	 */
	public int getModulus() {
		return count % modulus;
	}

	/**
	 * Returns <code>true</code> if current item is the first one.
	 */
	public boolean isFirst() {
		return count == 1;
	}

	/**
	 * Returns <code>true</code> if current item is the last one.
	 */
	public boolean isLast() {
		return last;
	}


	// ---------------------------------------------------------------- iterate

	/**
	 * Performs the iterations to the next item and specifies if this is the last iteration.
	 */
	protected void next(final boolean isLast) {
		count++;
		last = isLast;
	}


	@Override
	public String toString() {
		return count + ":" + (isFirst() ? 'F':'_') + ':' + (last ? 'L':'_') + ':' + getModulus();
	}
}
