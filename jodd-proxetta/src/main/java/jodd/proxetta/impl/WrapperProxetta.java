// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.impl;

import jodd.proxetta.JoddProxetta;
import jodd.proxetta.Proxetta;
import jodd.proxetta.ProxyAspect;

/**
 * Proxetta that creates wrappers.
 */
public class WrapperProxetta extends Proxetta<WrapperProxetta> {

	protected final ProxyAspect[] aspects;

	public WrapperProxetta(ProxyAspect... aspects) {
		this.aspects = aspects;
		classNameSuffix = JoddProxetta.wrapperClassNameSuffix;
	}

	public static WrapperProxetta withAspects(ProxyAspect... aspects) {
		return new WrapperProxetta(aspects);
	}

	/**
	 * Returns aspects.
	 */
	public ProxyAspect[] getAspects() {
		return aspects;
	}

	// ---------------------------------------------------------------- implement

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WrapperProxettaBuilder builder() {
		return new WrapperProxettaBuilder(this);
	}

	// ---------------------------------------------------------------- shortcuts

	public WrapperProxettaBuilder builder(Class targetClassOrInterface) {
		WrapperProxettaBuilder builder = builder();

		builder.setTarget(targetClassOrInterface);

		return builder;
	}

	public WrapperProxettaBuilder builder(Class targetClassOrInterface, String targetProxyClassName) {
		WrapperProxettaBuilder builder = builder();

		builder.setTarget(targetClassOrInterface);
		builder.setTargetProxyClassName(targetProxyClassName);

		return builder;
	}


	public WrapperProxettaBuilder builder(Class targetClassOrInterface, Class targetInterface) {
		WrapperProxettaBuilder builder = builder();

		builder.setTarget(targetClassOrInterface);
		builder.setTargetInterface(targetInterface);

		return builder;
	}

	public WrapperProxettaBuilder builder(Class targetClassOrInterface, Class targetInterface, String targetProxyClassName) {
		WrapperProxettaBuilder builder = builder();

		builder.setTarget(targetClassOrInterface);
		builder.setTargetInterface(targetInterface);
		builder.setTargetProxyClassName(targetProxyClassName);

		return builder;
	}
}
