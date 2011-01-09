// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

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
 * <pre>
 *   &lt;re:iter status="status" value='%{0, 1}'&gt;
 *      Index: &lt;s:property value="%{#status.index}" /&gt; &lt;br /&gt;
 *      Count: &lt;s:property value="%{#status.count}" /&gt; &lt;br /&gt;
 *   &lt;/re:iter>
 * </pre>
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
	public IteratorStatus(int modulus) {
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
	protected void next(boolean isLast) {
		count++;
		last = isLast;
	}


	@Override
	public String toString() {
		return count + ":" + (isFirst() ? 'F':'_') + ':' + (last ? 'L':'_') + ':' + getModulus();
	}
}
