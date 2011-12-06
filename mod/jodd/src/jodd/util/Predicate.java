// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Defines a functor interface implemented by classes that perform a predicate test on an object.
 */
public interface Predicate<T> {
	/**
	 * Uses the specified parameter to perform a test that returns boolean.
	 */
	boolean predicate(T arg);
}