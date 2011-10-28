// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.paramo;

/**
 * Method parameter. It consist of a name and a generic signature.
 */
public class MethodParameter {

	public static final MethodParameter[] EMPTY_ARRAY = new MethodParameter[0];

	protected final String name;

	protected final String signature;

	public MethodParameter(String name, String signature) {
		this.name = name;
		this.signature = signature;
	}

	/**
	 * Returns method parameter name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns method parameter signature when parameter type uses generics.
	 */
	public String getSignature() {
		return signature;
	}

}
