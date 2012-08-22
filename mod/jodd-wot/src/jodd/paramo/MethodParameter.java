// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.paramo;

/**
 * Resolved method parameter from bytecode.
 * It consist of parameter name and parameter bytecode signature
 * (including generics info).
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
	 * Returns method parameter signature.
	 * Generics information is available, too.
	 */
	public String getSignature() {
		return signature;
	}

}
