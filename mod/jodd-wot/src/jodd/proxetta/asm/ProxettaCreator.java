// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.ClassReader;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import jodd.proxetta.ProxyAspect;

/**
 * Creates the proxy subclass using ASM library.
 */
public class ProxettaCreator extends ClassProcessor {

	private static final Logger log = LoggerFactory.getLogger(ProxettaCreator.class);

	protected final ProxyAspect[] aspects;

	public ProxettaCreator(ProxyAspect... aspects) {
		this.aspects = aspects;
	}


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