// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

/**
 * Holder for various {@link jodd.proxetta.ProxyTarget} information.
 */
public final class ProxyTargetInfo {

	public int argumentCount;

	public Class[] argumentsClasses;

	public Object[] arguments;

	public Class returnType;

	public String targetMethodName;

	public String targetMethodSignature;

	public String targetMethodDescription;

	public Class targetClass;

}