// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.directaccess;

/**
 * Setter method invoker.
 */
public interface SetterMethodInvoker {

	/**
	 * Invokes method on given target with provided parameter.
	 */
	void invoke(Object target, Object parameter);

}