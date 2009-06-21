// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import java.util.List;

/**
 * Method info provides various information about the method.
 * Used in {@link jodd.proxetta.ProxyPointcut pointcut} definitions.
 */
public interface MethodInfo extends AsmConsts {

	String getDeclaration();

	String getReturnType();

	String getExceptions();

	String getSignature();

	String getMethodName();

	int getArgumentsCount();

	int getArgumentOpcodeType(int index);

	int getReturnOpcodeType();

	int getAccessFlags();

	String getClassname();

	String getDescription();

	List<AnnotationData> getAnnotations();

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
