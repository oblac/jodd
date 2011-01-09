// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import jodd.proxetta.pointcuts.AllMethodsPointcut;

/**
 * Proxy aspect contains advice and pointcut rules for applying advice.
 */
public class ProxyAspect {

	protected final Class<? extends ProxyAdvice> advice;
	protected final ProxyPointcut pointcut;

	/**
	 * Creates aspect defined with provided advice and pointcut for all class methods.
	 */
	public ProxyAspect(Class<? extends ProxyAdvice> advice) {
		this(advice, new AllMethodsPointcut());
	}

	/**
	 * Creates aspect defined with provided advice and pointcut.
	 */
	public ProxyAspect(Class<? extends ProxyAdvice> advice, ProxyPointcut pointcut) {
		this.advice = advice;
		this.pointcut = pointcut;
	}

	/**
	 * Returns proxy advice class.
	 */
	public Class<? extends ProxyAdvice> getAdvice() {
		return advice;
	}

	/**
	 * Returns proxy pointcut.
	 */
	public ProxyPointcut getPointcut() {
		return pointcut;
	}


	@Override
	public String toString() {
		return "ProxyAspect{" +
				"advice=" + advice.getName() +
				", pointcut=" + pointcut.getClass().getName() +
				'}';
	}
}
