// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import jodd.log.Log;
import org.objectweb.asm.ClassReader;
import jodd.proxetta.ProxyAspect;

/**
 * Creates the proxy subclass using ASM library.
 */
public class ProxettaCreator extends ClassProcessor {

	private static final Log log = Log.getLogger(ProxettaCreator.class);

	protected final ProxyAspect[] aspects;

	public ProxettaCreator(ProxyAspect... aspects) {
		this.aspects = aspects;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected WorkData process(ClassReader cr, String reqProxyClassName, TargetClassInfoReader targetClassInfoReader) {
		if (log.isDebugEnabled()) {
			log.debug("Creating proxy for " + cr.getClassName());
		}
		ProxettaClassBuilder pcb = new ProxettaClassBuilder(destClassWriter, aspects, classNameSuffix(), reqProxyClassName, targetClassInfoReader);
		cr.accept(pcb, 0);
		return pcb.wd;
	}

}