// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.meta.InitMethodInvocationStrategy;

import java.lang.reflect.Method;

/**
 * Init method point.
 */
public class InitMethodPoint implements Comparable {

	public static final InitMethodPoint[] EMPTY = new InitMethodPoint[0];

	public final Method method;
	public final int order;
	public final InitMethodInvocationStrategy invocationStrategy;

	public InitMethodPoint(Method method, int order, InitMethodInvocationStrategy invocationStrategy) {
		this.method = method;
		this.order = order == 0 ? (Integer.MAX_VALUE >> 1) : (order < 0 ? Integer.MAX_VALUE + order: order);
		this.invocationStrategy = invocationStrategy;
	}

	public int compareTo(Object other) {
		InitMethodPoint that = (InitMethodPoint) other;
		return this.order == that.order ? 0 : (this.order > that.order ? 1 : -1);
	}

}
