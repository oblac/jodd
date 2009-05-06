// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import java.lang.reflect.Method;

/**
 * Init method point.
 */
public class InitMethodPoint {

	public static final InitMethodPoint[] EMPTY = new InitMethodPoint[0]; 

	public final Method method;

	public InitMethodPoint(Method method) {
		this.method = method;
	}
}
