// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import java.lang.reflect.Method;

/**
 * Destroy method point.
 */
public class DestroyMethodPoint {

	public static final DestroyMethodPoint[] EMPTY = new DestroyMethodPoint[0];

	public final Method method;

	public DestroyMethodPoint(Method method) {
		this.method = method;
	}

}