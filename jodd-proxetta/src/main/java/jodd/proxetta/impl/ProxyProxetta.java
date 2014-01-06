// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.impl;

import jodd.JoddProxetta;
import jodd.proxetta.Proxetta;
import jodd.proxetta.ProxyAspect;

/**
 * Proxetta that creates proxies.
 */
public class ProxyProxetta extends Proxetta<ProxyProxetta> {

	protected final ProxyAspect[] aspects;

	public ProxyProxetta(ProxyAspect... aspects) {
		this.aspects = aspects;
		classNameSuffix = JoddProxetta.proxyClassNameSuffix;
	}

	/**
	 * Specifies aspects for the target and creates this <code>Proxetta</code> instance.
	 */
	public static ProxyProxetta withAspects(ProxyAspect... aspects) {
		return new ProxyProxetta(aspects);
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
	public ProxyProxettaBuilder builder() {
		return new ProxyProxettaBuilder(this);
	}

	// ---------------------------------------------------------------- shortcuts

	public ProxyProxettaBuilder builder(Class target) {
		ProxyProxettaBuilder builder = builder();
		builder.setTarget(target);
		return builder;
	}

	public ProxyProxettaBuilder builder(Class target, String targetProxyClassName) {
		ProxyProxettaBuilder builder = builder();
		builder.setTarget(target);
		builder.setTargetProxyClassName(targetProxyClassName);
		return builder;
	}

}
