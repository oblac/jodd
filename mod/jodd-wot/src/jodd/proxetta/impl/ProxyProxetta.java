// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.impl;

import jodd.proxetta.Proxetta;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.asm.ClassProcessor;
import jodd.proxetta.asm.ProxettaCreator;
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
	 * {@inheritDoc}
	 */
	@Override
	protected ClassProcessor createClassProcessor() {
		return new ProxettaCreator(aspects);
	}
}
