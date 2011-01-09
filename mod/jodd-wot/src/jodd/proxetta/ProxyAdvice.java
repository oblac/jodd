// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

/**
 * ProxyAdvice is the code portion of an aspect, i.e. the logic that replaces crosscutting concern.
 */
public interface ProxyAdvice {

	/**
	 * Intercepts wrapped method.
	 */
	Object execute() throws Exception;
}
