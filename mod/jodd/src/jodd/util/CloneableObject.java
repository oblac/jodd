// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Since {@link Cloneable} is just a marker interface, it is not possible
 * to clone different type of objects at once. This interface helps
 * for user objects, but, obviously, it can't change JDK classes.
 */
public interface CloneableObject extends Cloneable {

	/**
	 * Performs instance cloning.
	 *
	 * @see Object#clone()
	 */
	Object clone() throws CloneNotSupportedException;

}
