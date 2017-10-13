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

package jodd.util;

/**
 * Loop iterator that provides various counting data about the loop cycles.
 * Should be used as in <code>while</code> construct:
 *
 * <pre><code>
 * while (loopIterator.next()) {
 * 		// do something
 * }
 * </code></pre>
 */
public class LoopIterator {

	protected final int start;
	protected final int end;
	protected final int step;
	protected final int modulus;

	protected boolean first;
	protected boolean last;
	protected int value;
	protected int count;
	protected final boolean looping;

	public LoopIterator(int start, int end) {
		this(start, end, 1, 2);
	}

	public LoopIterator(int start, int end, int step) {
		this(start, end, step, 2);
	}

	public LoopIterator(int start, int end, int step, int modulus) {
		this.start = start;
		this.end = end;
		this.step = step;
		this.modulus = modulus;
		this.looping = step > 0 ? start <= end : start >= end;
	}

	/**
	 * Returns current item count (1-based).
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Returns current item index (0-based).
	 */
	public int getIndex() {
		return count - 1;
	}

	/**
	 * Returns current loop value.
	 */
	public int getValue() {
		return value;
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
	public int modulus(int operand) {
		return count % operand;
	}

	/**
	 * Returns modulus of current count.
	 */
	public int getModulus() {
		return count % modulus;
	}

	/**
	 * Returns modulus value.
	 */
	public int getModulusValue() {
		return modulus;
	}

	/**
	 * Returns modulus of current index.
	 */
	public int getIndexModulus() {
		return (count - 1) % modulus;
	}

	/**
	 * Returns <code>true</code> if current item is the first one.
	 */
	public boolean isFirst() {
		return first;
	}

	/**
	 * Returns <code>true</code> if current item is the last one.
	 */
	public boolean isLast() {
		return last;
	}


	// ---------------------------------------------------------------- iterate

	/**
	 * Iterates to next value at the beginning of the loop.
	 */
	public boolean next() {
		if (!looping) {
			return false;
		}
		if (last) {
			return false;
		}
		if (count == 0) {
			value = start;
			first = true;
		} else {
			value += step;
			first = false;
		}
		count++;
		last = isLastIteration(value + step);
		return true;
	}

	/**
	 * Resets the loop from the beginning.
	 */
	public void reset() {
		count = 0;
		last = false;
	}

	protected boolean isLastIteration(int value) {
		return step > 0 ? value > end : value < end;
	}


	@Override
	public String toString() {
		return looping ?
				value + ":" + count + ':' + (first ? 'F':'_') + ':' + (last ? 'L':'_') + ':' + getModulus()
				:
				"N.A.";
	}

}
