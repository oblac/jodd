// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

/**
 * Parameters scope for injection ({@link jodd.madvoc.meta.In}) and outjection ({@link jodd.madvoc.meta.Out}).
 */
public enum ScopeType {

	REQUEST(0),
	SESSION(1),
	APPLICATION(2),
	CONTEXT(3);

	private int value;

	ScopeType(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	@Override
	public String toString() {
		switch(value) {
			case 0: return "Request";
			case 1: return "Session";
			case 2: return "Application";
			case 3: return "Context";
			default: return "Undefined";
		}
	}
}
