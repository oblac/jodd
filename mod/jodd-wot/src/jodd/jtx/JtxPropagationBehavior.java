// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx;

/**
 * Transaction propagation behavior values.
 */
public enum JtxPropagationBehavior {

	/**
	 * Support a current transaction, create a new one if none exists.
	 */
	PROPAGATION_REQUIRED(1),

	/**
	 * Support a current transaction, execute non-transactionally if none exists.
	 */
	PROPAGATION_SUPPORTS(2),

	/**
	 * Support a current transaction, throw an exception if none exists.
	 */
	PROPAGATION_MANDATORY(3),

	/**
	 * Create a new transaction, suspend the current transaction if one exists.
	 */
	PROPAGATION_REQUIRES_NEW(4),

	/**
	 * Execute non-transactionally, suspend the current transaction if one exists.
	 */
	PROPAGATION_NOT_SUPPORTED(5),

	/**
	 * Execute non-transactionally, throw an exception if a transaction exists.
	 */
	PROPAGATION_NEVER(6);

	private int value;

	JtxPropagationBehavior(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	@Override
	public String toString() {
		switch(value) {
			case 1: return "Required";
			case 2: return "Supports";
			case 3: return "Mandatory";
			case 4: return "Requires New";
			case 5: return "Not Supported";
			case 6: return "Never";
			default: return "Undefined";
		}
	}
}
