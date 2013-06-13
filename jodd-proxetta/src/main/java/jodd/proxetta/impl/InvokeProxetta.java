// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.impl;

import jodd.JoddProxetta;
import jodd.proxetta.InvokeAspect;
import jodd.proxetta.Proxetta;

/**
 * Proxetta that does method (i.e. invocation) replacements.
 */
public class InvokeProxetta extends Proxetta {

	protected final InvokeAspect[] invokeAspects;

	public InvokeProxetta(InvokeAspect... aspects) {
		this.invokeAspects = aspects;
		classNameSuffix = JoddProxetta.invokeProxyClassNameSuffix;
	}

	/**
	 * Specifies invoke replacement aspects and creates this <code>Proxetta</code> instance.
	 */
	public static InvokeProxetta withAspects(InvokeAspect... aspects) {
		return new InvokeProxetta(aspects);
	}

	public InvokeAspect[] getAspects() {
		return invokeAspects;
	}

	// ---------------------------------------------------------------- implement

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InvokeProxettaBuilder builder() {
		return new InvokeProxettaBuilder(this);
	}

	// ---------------------------------------------------------------- shortcuts

	public InvokeProxettaBuilder builder(Class target) {
		InvokeProxettaBuilder builder = builder();
		builder.setTarget(target);
		return builder;
	}

	public InvokeProxettaBuilder builder(Class target, String targetProxyClassName) {
		InvokeProxettaBuilder builder = builder();
		builder.setTarget(target);
		builder.setTargetProxyClassName(targetProxyClassName);
		return builder;
	}

}
