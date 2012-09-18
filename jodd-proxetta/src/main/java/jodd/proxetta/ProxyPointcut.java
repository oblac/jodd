// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;


/**
 * Pointcut is a set of points in the application where advice should be applied, i.e.
 * which methods will be wrapped by proxy.
 */
public interface ProxyPointcut {

	/**
	 * Returns <code>true</code> if method should be wrapped with the proxy.
	 */
	boolean apply(MethodInfo methodInfo);

}