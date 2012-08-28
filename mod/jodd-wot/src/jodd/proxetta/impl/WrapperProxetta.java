// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.impl;

import jodd.proxetta.Proxetta;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.asm.ProxettaNaming;

/**
 * Proxetta that creates wrappers.
 */
public class WrapperProxetta extends Proxetta {

	protected final ProxyAspect[] aspects;

	public WrapperProxetta(ProxyAspect... aspects) {
		this.aspects = aspects;
		classNameSuffix = ProxettaNaming.WRAPPER_CLASS_NAME_SUFFIX;
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
