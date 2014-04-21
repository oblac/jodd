// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

/**
 * Method info provides various information about the method.
 * Used in {@link jodd.proxetta.ProxyPointcut pointcut} definitions.
 */
public interface MethodInfo {

	/**
	 * Returns full java-like method arguments declaration.
	 * @see #getSignature()
	 */
	String getDeclaration();

	/**
	 * Returns java-like return type.
	 */
	String getReturnType();

	String getReturnTypeName();

	/**
	 * Returns list of exceptions.
	 */
	String getExceptions();

	/**
	 * Returns java-like method signature of @{link #getDescription description}.
	 * Does not contain any generic information.
	 */
	String getSignature();

	/**
	 * Returns raw bytecode signature or <code>null</code> if not present.
	 * @see #getDescription()
	 */
	public String getRawSignature();

	/**
	 * Returns method name.
	 */
	String getMethodName();

	/**
	 * Returns number of method arguments.
	 */
	int getArgumentsCount();

	char getArgumentOpcodeType(int index);

	String getArgumentTypeName(int index);

	int getArgumentOffset(int index);

	public int getAllArgumentsSize();

	/**
	 * Returns return type opcode.
	 * For example, returns 'V' for void etc.
	 */
	char getReturnOpcodeType();

	int getAccessFlags();

	/**
	 * Returns bytecode-like class name.
	 */
	String getClassname();

	/**
	 * Returns bytecode-like method description.
	 * @see #getSignature()
	 * @see #getRawSignature()
	 */
	String getDescription();

	/**
	 * Returns annotation infos, if there is any.
	 */
	AnnotationInfo[] getAnnotations();

	/**
	 * Returns declared class name for inner methods or {@link #getClassname() classname} for top-level methods.
	 */
	String getDeclaredClassName();

	/**
	 * Returns <code>true</code> if method is declared in top-level class.
	 */
	boolean isTopLevelMethod();

	/**
	 * Returns target {@link jodd.proxetta.ClassInfo class informations}.
	 */
	ClassInfo getClassInfo();

	/**
	 * Returns hierarchy level, starting from top class as 1.
	 */
	int getHierarchyLevel();
}
