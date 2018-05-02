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

import jodd.joy.madvoc.interceptor.DefaultInterceptorStack;
import jodd.madvoc.AutomagicMadvocConfigurator;
import jodd.madvoc.WebApp;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.petite.PetiteWebApp;
import jodd.madvoc.proxetta.ProxettaAwareActionsManager;
import jodd.madvoc.proxetta.ProxettaProvider;
import jodd.petite.PetiteContainer;
import jodd.props.Props;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.util.Chalk256;
import jodd.util.Consumers;
import jodd.util.StringUtil;

import javax.servlet.ServletContext;
import java.util.Comparator;
import java.util.List;
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
	private Supplier<PetiteWebApp> webAppSupplier;

	public JoyMadvoc(final Supplier<PetiteContainer> petiteSupplier, final Supplier<ProxyProxetta> proxettaSupplier, final Supplier<Props> propsSupplier, final Supplier<JoyScanner> scannerSupplier) {
		this.proxettaSupplier = proxettaSupplier;
		this.petiteSupplier = petiteSupplier;
		this.scannerSupplier = scannerSupplier;
		this.propsSupplier = propsSupplier;
		this.webAppConsumers = Consumers.empty();
	}

	/**
	 * Defines optional servlet context.
	 */
	public void setServletContext(final ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * Defines optional web app supplier that creates custom {@link PetiteWebApp}.
	 */
	public void setWebAppSupplier(final Supplier<PetiteWebApp> webAppSupplier) {
		this.webAppSupplier = webAppSupplier;
	}

	public void add(final Consumer<WebApp> webAppConsumer) {
		this.webAppConsumers.add(webAppConsumer);
	}

	@Override
	public void start() {
		initLogger();

		log.info("MADVOC start  ----------");

		webApp = webAppSupplier == null ? new PetiteWebApp() : webAppSupplier.get();

		webApp.configure(madvocConfig -> madvocConfig.getActionConfig().setInterceptors(DefaultInterceptorStack.class));
		webApp.withPetiteContainer(petiteSupplier);

		if (servletContext != null) {
			webApp.bindServletContext(servletContext);
		}
		webApp.withParams(propsSupplier.get());

		webApp.registerComponent(new ProxettaProvider(proxettaSupplier.get()));
		webApp.registerComponent(ProxettaAwareActionsManager.class);

		webApp.registerComponent(AutomagicMadvocConfigurator.class, amc -> amc.withScanner(scannerSupplier.get()));

		webAppConsumers.accept(webApp);

		webApp.start();
	}

	@Override
	public void stop() {
		if (log != null) {
			log.info("MADVOC stop  ----------");
		}
		if (webApp != null) {
			webApp.shutdown();
		}
	}

	/**
	 * Prints routes to console.
	 */
	protected void printRoutes() {
		final ActionsManager actionsManager = webApp.madvocContainer().lookupComponent(ActionsManager.class);

		System.out.println(StringUtil.repeat('-', 80));

		final List<ActionRuntime> actions = actionsManager.getAllActionRuntimes();
		actions.stream()
			.sorted(Comparator.comparing(
				actionRuntime -> actionRuntime.getActionMethod() + ' ' + actionRuntime.getActionPath()))
			.forEach(ar -> {

				System.out.print(Chalk256.chalk().yellow().on(val(ar.getActionMethod(), 6)));
				System.out.print(" ");
				System.out.print(Chalk256.chalk().green().on(val(ar.getActionPath(), 24)));
				System.out.print(" ");
				final String signature = ar.getActionClass().getName() + '#' + ar.getActionClassMethod().getName();
				System.out.print(Chalk256.chalk().blue().on(valRight(signature, 48)));
				System.out.println();
			});

		System.out.println(StringUtil.repeat('-', 80));
	}

	protected String val(final String value, final int len) {
		if (value.length() > len) {
			return value.substring(value.length() - len);
		}

		if (value.length() == len) {
			return value;
		}

		return value + StringUtil.repeat(' ', len - value.length());
	}
	protected String valRight(final String value, final int len) {
		if (value.length() > len) {
			return value.substring(value.length() - len);
		}

		if (value.length() == len) {
			return value;
		}

		return StringUtil.repeat(' ', len - value.length()) + value;
	}
}
