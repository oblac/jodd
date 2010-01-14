// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

/**
 * Various target class information.
 */
public interface ClassInfo extends AsmConsts {

	/**
	 * Returns package name.
	 */
	String getPackage();

	/**
	 * Returns simple class name.
	 */
	String getClassname();

	/**
	 * Returns super class reference. 
	 */
	String getSuperName();

	/**
	 * Returns class reference.
	 */
	String getReference();

	/**
	 * Returns annotation information or <code>null</code> if target class has no annotations.
	 */
	AnnotationInfo[] getAnnotations();

}
