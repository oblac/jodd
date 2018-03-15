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

package jodd.joy;

import jodd.jtx.JoddJtx;
import jodd.jtx.proxy.AnnotationTxAdvice;
import jodd.proxetta.Proxetta;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.ProxyPointcut;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.pointcuts.MethodWithAnnotationPointcut;

import java.util.ArrayList;
import java.util.List;

public class JoyProxetta extends JoyBase {

	protected ProxyProxetta proxyProxetta;

	public JoyProxetta() {
		proxyAspects.add(createTxProxyAspects());
	}

	// ---------------------------------------------------------------- getters

	/**
	 * Returns proxetta once it is created.
	 * @return
	 */
	public ProxyProxetta getProxetta() {
		return proxyProxetta;
	}

	// ---------------------------------------------------------------- config

	private final List<ProxyAspect> proxyAspects = new ArrayList<>();

	public void addProxyAspect(final ProxyAspect proxyAspect) {
		this.proxyAspects.add(proxyAspect);
	}

	// ---------------------------------------------------------------- lifecycle

	/**
	 * Creates Proxetta with all aspects. The following aspects are created:
	 * <ul>
	 * <li>Transaction proxy - applied on all classes that contains public top-level methods
	 * annotated with <code>@Transaction</code> annotation. This is just one way how proxies
	 * can be applied - since base configuration is in Java, everything is possible.</li>
	 * </ul>
	 */
	@Override
	void start() {
		initLogger();

		log.info("PROXETTA start ----------");

		final ProxyAspect[] proxyAspectsArray = this.proxyAspects.toArray(new ProxyAspect[0]);

		log.debug("Total proxy aspects: " + proxyAspectsArray.length);

		proxyProxetta = Proxetta.proxyProxetta().withAspects(proxyAspectsArray);
	}

	protected ProxyAspect createTxProxyAspects() {
		return new ProxyAspect(
			AnnotationTxAdvice.class,
			((ProxyPointcut)
				methodInfo -> methodInfo.isPublicMethod() && methodInfo.isTopLevelMethod())
				.and(MethodWithAnnotationPointcut.of(JoddJtx.defaults().getTxAnnotations()))
		);
	}

	@Override
	void stop() {
	}

}
