// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import jodd.proxetta.impl.InvokeProxetta;
import org.objectweb.asm.ClassReader;

import java.io.InputStream;

/**
 * Invocation replacer class processor.
 */
public class ProxettaInvokeCreator extends ClassProcessor {

	protected final InvokeProxetta invokeProxetta;

	public ProxettaInvokeCreator(InvokeProxetta invokeProxetta) {
		super(invokeProxetta);
		this.invokeProxetta = invokeProxetta;
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

		InvokeClassBuilder icb = new InvokeClassBuilder(
				destClassWriter,
				invokeProxetta.getAspects(),
				classNameSuffix(),
				requestedProxyClassName,
				targetClassInfoReader);

		cr.accept(icb, 0);

		return icb.wd;
	}

}
