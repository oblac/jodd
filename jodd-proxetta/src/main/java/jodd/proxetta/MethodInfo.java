// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

/**
 * Method info provides various information about a method.
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

	/**
	 * Returns type name for return type.
	 */
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

	/**
	 * Returns type name of given argument.
	 */
	String getArgumentTypeName(int index);

	/**
	 * Returns offset of an argument in local variables.
	 */
	int getArgumentOffset(int index);

	/**
	 * Returns annotations for given argument.
	 */
	AnnotationInfo[] getArgumentAnnotations(int index);

	/**
	 * Returns size of all arguments on stack.
	 * It is not equal to argument count, as some types
	 * takes 2 places, like <code>long</code>.
	 */
	int getAllArgumentsSize();

	/**
	 * Returns return type opcode.
	 * For example, returns 'V' for void etc.
	 */
	char getReturnOpcodeType();

	/**
	 * Returns method access flags.
	 */
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