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

import jodd.madvoc.WebApp;
import jodd.madvoc.config.AutomagicMadvocConfigurator;
import jodd.madvoc.petite.PetiteWebApp;
import jodd.madvoc.proxetta.ProxettaAwareActionsManager;
import jodd.madvoc.proxetta.ProxettaProvider;
import jodd.petite.PetiteContainer;
import jodd.props.Props;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.util.Consumers;

import javax.servlet.ServletContext;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JoyMadvoc extends JoyBase {

	private final Supplier<ProxyProxetta> proxettaSupplier;
	private final Supplier<PetiteContainer> petiteSupplier;
	private final Supplier<JoyScanner> scannerSupplier;
	private final Consumers<WebApp> webAppConsumers;
	private final Supplier<Props> propsSupplier;
	private ServletContext servletContext;
	private PetiteWebApp webApp;

	public JoyMadvoc(Supplier<PetiteContainer> petiteSupplier, Supplier<ProxyProxetta> proxettaSupplier, Supplier<JoyScanner> scannerSupplier, Supplier<Props> propsSupplier) {
		this.proxettaSupplier = proxettaSupplier;
		this.petiteSupplier = petiteSupplier;
		this.scannerSupplier = scannerSupplier;
		this.propsSupplier = propsSupplier;
		this.webAppConsumers = Consumers.empty();
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void add(Consumer<WebApp> webAppConsumer) {
		this.webAppConsumers.add(webAppConsumer);
	}

	@Override
	public void start() {
		initLogger();

		log.info("MADVOC start  ----------");

		webApp = new PetiteWebApp();

		webApp.withPetiteContainer(petiteSupplier);

		webApp.bindServletContext(servletContext);
		webApp.withParams(propsSupplier.get());

		webApp.registerComponent(new ProxettaProvider(proxettaSupplier.get()));
		webApp.registerComponent(ProxettaAwareActionsManager.class);

		webApp.registerComponent(AutomagicMadvocConfigurator.class, amc -> scannerSupplier.get().applyTo(amc));

		webAppConsumers.accept(webApp);

		webApp.start();
	}

	@Override
	public void stop() {
		if (log != null) {
			log.info("MADVOC stop");
		}
		if (webApp != null) {
			webApp.shutdown();
		}
	}

}
