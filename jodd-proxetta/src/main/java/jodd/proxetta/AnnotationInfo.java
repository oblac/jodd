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
	 * <ul>
	 * <li>String - for simple values,</li>
	 * <li>Object[] - for array values</li>
	 * <li>String[2] - pair of two strings, representing description and value</li>
	 * <li>AnnotationInfo - nested annotation.</li>
	 * </ul>
	 */
	Object getElement(String name);

	/**
	 * Returns annotation element names.
	 */
	Set<String> getElementNames();

}
