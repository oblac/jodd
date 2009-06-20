// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

/**
 * Various target type informations
 */
public interface ClassInfo {

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

}
