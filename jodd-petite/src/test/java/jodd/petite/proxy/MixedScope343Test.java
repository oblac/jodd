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

package jodd.petite.proxy;

import jodd.petite.PetiteConfig;
import jodd.petite.PetiteContainer;
import jodd.petite.config.AutomagicPetiteConfigurator;
import jodd.petite.proxy.example1.ExternalBean;
import jodd.petite.proxetta.ProxettaAwarePetiteContainer;
import jodd.proxetta.impl.ProxyProxetta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MixedScope343Test {

	private PetiteContainer petiteContainer;

	@BeforeEach
	public void setupPetiteContainer() {
		PetiteConfig petiteConfig = PetiteHelper.createPetiteConfig();

		ProxyProxetta proxyProxetta = PetiteHelper.createProxyProxetta();
		petiteContainer = new ProxettaAwarePetiteContainer(proxyProxetta, petiteConfig);

		AutomagicPetiteConfigurator petiteConfigurator = new AutomagicPetiteConfigurator();
		petiteConfigurator.setIncludedEntries(this.getClass().getPackage().getName() + ".*");
		petiteConfigurator.configure(petiteContainer);
	}

	@AfterEach
	public void teardownPetiteContainer() {
		petiteContainer.shutdown();
	}

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	@BeforeEach
	public void setUpStreams() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
	}

	@AfterEach
	public void cleanUpStreams() {
		System.setOut(null);
		System.setErr(null);
	}

	@Test
	public void testWithMixingScopesSingletonAndProto(){
		ExternalBean externalBean = new ExternalBean();
		// --> inject

		petiteContainer.wire(externalBean);

		// <-- injection done

		System.out.println("RUN!");
		externalBean.execute();

		assertEquals("RUN!\n" +
			"execute now : jodd.petite.proxy.example1.impl.MainPetiteBean\n" +
			"execute now : jodd.petite.proxy.example1.impl.SubPetiteBean\n" +
			"Executing jodd.petite.proxy.example1.impl.SubPetiteBean$$Proxetta\n" +
			"executing jodd.petite.proxy.example1.impl.MainPetiteBean$$Proxetta\n" +
			"executing non jodd petite bean -> jodd.petite.proxy.example1.ExternalBean\n",
			outContent.toString());
	}
}
