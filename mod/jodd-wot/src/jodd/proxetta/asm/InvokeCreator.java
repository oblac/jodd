// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import jodd.proxetta.InvokeAspect;
import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Invocation replacer class processor.
 */
public class InvokeCreator extends ClassProcessor {

	private static final Logger log = LoggerFactory.getLogger(InvokeCreator.class);

	protected final InvokeAspect[] aspects;

	public InvokeCreator(InvokeAspect... aspects) {
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

		InvokeClassBuilder icb = new InvokeClassBuilder(destClassWriter, aspects, classNameSuffix(), reqProxyClassName, targetClassInfoReader);
		cr.accept(icb, 0);

		return icb.wd;
	}

}
