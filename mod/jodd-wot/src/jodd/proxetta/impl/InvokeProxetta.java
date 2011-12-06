// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.impl;

import jodd.proxetta.InvokeAspect;
import jodd.proxetta.Proxetta;
import jodd.proxetta.asm.ClassProcessor;
import jodd.proxetta.asm.InvokeCreator;
import jodd.proxetta.asm.ProxettaNaming;

/**
 * Proxetta that does method (i.e. invocation) replacements.
 */
public class InvokeProxetta extends Proxetta {

	protected final InvokeAspect[] invokeAspects;

	public InvokeProxetta(InvokeAspect... aspects) {
		this.invokeAspects = aspects;
		classNameSuffix = ProxettaNaming.INVOKE_PROXY_CLASS_NAME_SUFFIX;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ClassProcessor createClassProcessor() {
		return new InvokeCreator(this.invokeAspects);
	}

}
