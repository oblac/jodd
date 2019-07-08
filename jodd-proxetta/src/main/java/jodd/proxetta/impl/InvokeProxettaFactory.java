// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.proxetta.impl;

import jodd.asm7.ClassReader;
import jodd.proxetta.InvokeAspect;
import jodd.proxetta.ProxettaFactory;
import jodd.proxetta.asm.InvokeClassBuilder;
import jodd.proxetta.asm.TargetClassInfoReader;
import jodd.proxetta.asm.WorkData;

import java.io.InputStream;

/**
 * Invocation replacer class processor.
 */
public class InvokeProxettaFactory extends ProxettaFactory<InvokeProxettaFactory, InvokeProxetta> {

	public InvokeProxettaFactory(final InvokeProxetta invokeProxetta) {
		super(invokeProxetta);
	}

	@Override
	public InvokeProxettaFactory setTarget(final InputStream target) {
		return super.setTarget(target);
	}

	@Override
	public InvokeProxettaFactory setTarget(final String targetName) {
		return super.setTarget(targetName);
	}

	@Override
	public InvokeProxettaFactory setTarget(final Class target) {
		return super.setTarget(target);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected WorkData process(final ClassReader cr, final TargetClassInfoReader targetClassInfoReader) {

		InvokeClassBuilder icb = new InvokeClassBuilder(
				destClassWriter,
				proxetta.getAspects(new InvokeAspect[0]),
				resolveClassNameSuffix(),
				requestedProxyClassName,
				targetClassInfoReader);

		cr.accept(icb, 0);

		return icb.getWorkData();
	}

}
