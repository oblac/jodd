// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx;

/**
 * Transaction propagation behavior values.
 */
public enum JtxPropagationBehavior {

	PROPAGATION_REQUIRED(1),
	PROPAGATION_SUPPORTS(2),
	PROPAGATION_MANDATORY(3),
	PROPAGATION_REQUIRES_NEW(4),
	PROPAGATION_NOT_SUPPORTED(5),
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
