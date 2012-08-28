// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import jodd.proxetta.impl.ProxyProxetta;
import org.objectweb.asm.ClassReader;

import java.io.InputStream;

/**
 * Creates the proxy subclass using ASM library.
 */
public class ProxettaProxyCreator extends ClassProcessor {

	protected final ProxyProxetta proxyProxetta;

	public ProxettaProxyCreator(ProxyProxetta proxyProxetta) {
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
		return pcb.wd;
	}

}