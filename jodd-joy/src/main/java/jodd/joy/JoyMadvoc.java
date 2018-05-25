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
import jodd.madvoc.proxetta.ProxettaSupplier;
import jodd.petite.PetiteContainer;
import jodd.props.Props;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.util.Chalk256;
import jodd.util.ClassUtil;
import jodd.util.Consumers;

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

	public JoyMadvoc(
			final Supplier<PetiteContainer> petiteSupplier,
			final Supplier<ProxyProxetta> proxettaSupplier,
			final Supplier<Props> propsSupplier,
			final Supplier<JoyScanner> scannerSupplier) {
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

		webApp.registerComponent(new ProxettaSupplier(proxettaSupplier.get()));
		webApp.registerComponent(ProxettaAwareActionsManager.class);

		// Automagic Madvoc configurator will scan and register ALL!
		// This way we reduce the startup time and have only one scanning.
		// Scanning happens in the INIT phase.
		final AutomagicMadvocConfigurator automagicMadvocConfigurator =
			new AutomagicMadvocConfigurator(scannerSupplier.get().classScanner()) {
				@Override
				protected String createInfoMessage() {
					return "Scanning completed in " + elapsed + "ms.";
				}
			};

		webApp.registerComponent(automagicMadvocConfigurator);

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
	protected void printRoutes(final int width) {
		final Print print = new Print();

		print.line("Routes", width);

		final ActionsManager actionsManager = webApp.madvocContainer().lookupComponent(ActionsManager.class);

		final List<ActionRuntime> actions = actionsManager.getAllActionRuntimes();
		actions.stream()
			.sorted(Comparator.comparing(
				actionRuntime -> actionRuntime.getActionPath() + ' ' + actionRuntime.getActionMethod()))
			.forEach(ar -> {

				final String actionMethod = ar.getActionMethod();

				print.out(Chalk256.chalk().yellow(), actionMethod == null ? "*" : actionMethod, 7);
				print.space();

				print.out(Chalk256.chalk().green(), ar.getActionPath(), 30);
				print.space();

				final String signature = ClassUtil.getShortClassName(
						ar.getActionClass(), 2) + '#' + ar.getActionClassMethod().getName();

				final int remaining = width - 7 - 1 - 30 - 1;
				print.outRight(Chalk256.chalk().blue(), signature, remaining);

				print.newLine();
			});

		print.line(width);
	}
}
