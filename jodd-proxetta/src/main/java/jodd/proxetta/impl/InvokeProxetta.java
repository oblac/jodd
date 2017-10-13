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

import jodd.proxetta.JoddProxetta;
import jodd.proxetta.InvokeAspect;
import jodd.proxetta.Proxetta;

/**
 * Proxetta that does method (i.e. invocation) replacements.
 */
public class InvokeProxetta extends Proxetta<InvokeProxetta> {

	protected final InvokeAspect[] invokeAspects;

	public InvokeProxetta(InvokeAspect... aspects) {
		this.invokeAspects = aspects;
		classNameSuffix = JoddProxetta.invokeProxyClassNameSuffix;
	}

	/**
	 * Specifies invoke replacement aspects and creates this <code>Proxetta</code> instance.
	 */
	public static InvokeProxetta withAspects(InvokeAspect... aspects) {
		return new InvokeProxetta(aspects);
	}

	public InvokeAspect[] getAspects() {
		return invokeAspects;
	}

	// ---------------------------------------------------------------- implement

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InvokeProxettaBuilder builder() {
		return new InvokeProxettaBuilder(this);
	}

	// ---------------------------------------------------------------- shortcuts

	public InvokeProxettaBuilder builder(Class target) {
		InvokeProxettaBuilder builder = builder();
		builder.setTarget(target);
		return builder;
	}

	public InvokeProxettaBuilder builder(Class target, String targetProxyClassName) {
		InvokeProxettaBuilder builder = builder();
		builder.setTarget(target);
		builder.setTargetProxyClassName(targetProxyClassName);
		return builder;
	}

}
