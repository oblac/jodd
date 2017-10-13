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
import jodd.proxetta.Proxetta;
import jodd.proxetta.ProxyAspect;

/**
 * Proxetta that creates proxies.
 */
public class ProxyProxetta extends Proxetta<ProxyProxetta> {

	protected final ProxyAspect[] aspects;

	public ProxyProxetta(ProxyAspect... aspects) {
		this.aspects = aspects;
		classNameSuffix = JoddProxetta.proxyClassNameSuffix;
	}

	/**
	 * Specifies aspects for the target and creates this <code>Proxetta</code> instance.
	 */
	public static ProxyProxetta withAspects(ProxyAspect... aspects) {
		return new ProxyProxetta(aspects);
	}

	/**
	 * Returns aspects.
	 */
	public ProxyAspect[] getAspects() {
		return aspects;
	}

	// ---------------------------------------------------------------- implement

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProxyProxettaBuilder builder() {
		return new ProxyProxettaBuilder(this);
	}

	// ---------------------------------------------------------------- shortcuts

	public ProxyProxettaBuilder builder(Class target) {
		ProxyProxettaBuilder builder = builder();
		builder.setTarget(target);
		return builder;
	}

	public ProxyProxettaBuilder builder(Class target, String targetProxyClassName) {
		ProxyProxettaBuilder builder = builder();
		builder.setTarget(target);
		builder.setTargetProxyClassName(targetProxyClassName);
		return builder;
	}

}
