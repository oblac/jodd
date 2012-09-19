// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

/**
 * Different wiring modes.
 */
public enum WiringMode {

	DEFAULT(-1),	// wiring mode is set by container
	NONE(0),		// no wiring at all
	STRICT(1),		// throws an exception if injection failed
	OPTIONAL(2),	// ignores unsuccessful injections
	AUTOWIRE(3);	// auto-wire

	private final int value;

	WiringMode(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	@Override
	public String toString() {
		return name();
	}

}
