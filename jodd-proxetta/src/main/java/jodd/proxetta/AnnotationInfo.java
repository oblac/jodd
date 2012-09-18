// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import java.util.Set;

/**
 * Annotation information.
 */
public interface AnnotationInfo {

	String getAnnotationClassname();

	String getAnnotationSignature();

	boolean isVisible();

	/**
	 * Lookups for annotation element. May return:
	 * <li>String - for simple values,
	 * <li>Object[] - for array values
	 * <li>String[2] - pair of two strings, representing description and value
	 * <li>AnnotationInfo - nested annotation.
	 */
	Object getElement(String name);

	/**
	 * Returns annotation element names.
	 */
	Set<String> getElementNames();

}
