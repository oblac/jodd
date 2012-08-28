// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.impl;

import jodd.proxetta.ProxettaBuilder;
import jodd.proxetta.asm.ProxettaClassBuilder;
import jodd.proxetta.asm.TargetClassInfoReader;
import jodd.proxetta.asm.WorkData;
import org.objectweb.asm.ClassReader;

import java.io.InputStream;

/**
 * Creates the proxy subclass using ASM library.
 */
public class ProxyProxettaBuilder extends ProxettaBuilder {

	protected final ProxyProxetta proxyProxetta;

	public ProxyProxettaBuilder(ProxyProxetta proxyProxetta) {
		super(proxyProxetta);
		this.proxyProxetta = proxyProxetta;
	}

	@Override
	public void setTarget(InputStream target) {
		super.setTarget(target);
	}

	@Override
	public void setTarget(String targetName) {
		super.setTarget(targetName);
	}

	@Override
	public void setTarget(Class target) {
		super.setTarget(target);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected WorkData process(ClassReader cr, TargetClassInfoReader targetClassInfoReader) {

		ProxettaClassBuilder pcb = new ProxettaClassBuilder(
				destClassWriter,
				proxyProxetta.getAspects(),
				classNameSuffix(),
				requestedProxyClassName,
				targetClassInfoReader);

		cr.accept(pcb, 0);

		return pcb.getWorkData();
	}

}