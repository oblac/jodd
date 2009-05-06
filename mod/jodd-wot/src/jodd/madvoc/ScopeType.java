// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

/**
 * Parameters scope for injection ({@link jodd.madvoc.meta.In}) and outjection ({@link jodd.madvoc.meta.Out}).
 */
public enum ScopeType {

	REQUEST(1),
	SESSION(2),
	APPLICATION(3),
	CONTEXT(4);

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
			case 1: return "Request";
			case 2: return "Session";
			case 3: return "Application";
			case 4: return "Context";
			default: return "Undefined";
		}
	}
}
