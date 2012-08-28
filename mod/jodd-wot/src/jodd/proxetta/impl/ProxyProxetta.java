// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.impl;

import jodd.proxetta.Proxetta;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.asm.ProxettaProxyCreator;
import jodd.proxetta.asm.ProxettaNaming;

/**
 * Proxetta that creates proxies.
 */
public class ProxyProxetta extends Proxetta {

	protected final ProxyAspect[] aspects;

	public ProxyProxetta(ProxyAspect... aspects) {
		this.aspects = aspects;
		classNameSuffix = ProxettaNaming.PROXY_CLASS_NAME_SUFFIX;
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
	public ProxettaProxyCreator builder() {
		return new ProxettaProxyCreator(this);
	}

	// ---------------------------------------------------------------- shortcuts

	public ProxettaProxyCreator builder(Class target) {
		ProxettaProxyCreator builder = builder();
		builder.setTarget(target);
		return builder;
	}

	public ProxettaProxyCreator builder(Class target, String targetProxyClassName) {
		ProxettaProxyCreator builder = builder();
		builder.setTarget(target);
		builder.setTargetProxyClassName(targetProxyClassName);
		return builder;
	}

}
