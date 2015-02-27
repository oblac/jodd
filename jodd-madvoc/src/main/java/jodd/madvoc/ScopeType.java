// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

/**
 * Parameters scope for injection ({@link jodd.madvoc.meta.In})
 * and outjection ({@link jodd.madvoc.meta.Out}).
 */
public enum ScopeType {
	/**
	 * Request attributes and parameters.
	 */
	REQUEST(0),
	/**
	 * Session attributes.
	 */
	SESSION(1),
	/**
	 * Servlet context attributes.
	 */
	APPLICATION(2),
	/**
	 * Madvoc context.
	 */
	CONTEXT(3),
	/**
	 * Servlet-related stuff.
	 */
	SERVLET(4);

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
			case 3: return "MadvocContext";
			case 4: return "Servlet";
			default: return "Undefined";
		}
	}
}
