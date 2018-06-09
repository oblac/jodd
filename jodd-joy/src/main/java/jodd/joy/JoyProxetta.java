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

import jodd.proxetta.Proxetta;
import jodd.proxetta.ProxyAspect;

import java.util.ArrayList;
import java.util.List;

/**
 * Tiny JoyProxetta kickstarter.
 */
public class JoyProxetta extends JoyBase implements JoyProxettaConfig {

	protected Proxetta proxetta;

	// ---------------------------------------------------------------- getters

	/**
	 * Returns proxetta once it is created.
	 */
	public Proxetta getProxetta() {
		return requireStarted(proxetta);
	}

	// ---------------------------------------------------------------- config

	private final List<ProxyAspect> proxyAspects = new ArrayList<>();

	/**
	 * Adds a proxy aspect.
	 */
	@Override
	public JoyProxetta addProxyAspect(final ProxyAspect proxyAspect) {
		requireNotStarted(proxetta);
		this.proxyAspects.add(proxyAspect);
		return this;
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
	public void start() {
		initLogger();

		log.info("PROXETTA start ----------");

		final ProxyAspect[] proxyAspectsArray = this.proxyAspects.toArray(new ProxyAspect[0]);

		log.debug("Total proxy aspects: " + proxyAspectsArray.length);

//		proxetta = Proxetta.wrapperProxetta().setCreateTargetInDefaultCtor(true).withAspects(proxyAspectsArray);

		proxetta = Proxetta.proxyProxetta().withAspects(proxyAspectsArray);

		log.info("PROXETTA OK!");
	}

	@Override
	public void stop() {
		proxetta = null;
	}

}
