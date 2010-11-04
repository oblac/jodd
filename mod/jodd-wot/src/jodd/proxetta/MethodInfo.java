// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

/**
 * Method info provides various information about the method.
 * Used in {@link jodd.proxetta.ProxyPointcut pointcut} definitions.
 */
public interface MethodInfo extends AsmConsts {

	/**
	 * Returns java-like method arguments declaration.
	 */
	String getDeclaration();

	/**
	 * Returns java-like return type.
	 */
	String getReturnType();

	String getExceptions();

	/**
	 * Returns java-like method signature.
	 */
	String getSignature();

	/**
	 * Returns method name.
	 */
	String getMethodName();

	/**
	 * Returns number of method arguments.
	 */
	int getArgumentsCount();

	int getArgumentOpcodeType(int index);

	int getReturnOpcodeType();

	int getAccessFlags();

	/**
	 * Returns bytecode-like class name.
	 */
	String getClassname();

	/**
	 * Returns bytecode-like method description.
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
