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

import jodd.chalk.Chalk256;
import jodd.joy.madvoc.JoyActionConfig;
import jodd.joy.madvoc.JoyRestActionConfig;
import jodd.madvoc.AutomagicMadvocConfigurator;
import jodd.madvoc.WebApp;
import jodd.madvoc.component.ActionConfigManager;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.RestAction;
import jodd.madvoc.petite.PetiteWebApp;
import jodd.madvoc.proxetta.ProxettaAwareActionsManager;
import jodd.madvoc.proxetta.ProxettaSupplier;
import jodd.props.Props;
import jodd.proxetta.ProxettaUtil;
import jodd.util.ClassUtil;
import jodd.util.Consumers;

import javax.servlet.ServletContext;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Tiny JoyMadvoc kickstarter. It is a special component, as it runs last and performs
 * the classpath scanning.
 */
public class JoyMadvoc extends JoyBase {

	private final Supplier<String> appNameSupplier;
	private final Supplier<JoyProxetta> joyProxettaSupplier;
	private final Supplier<JoyPetite> joyPetiteSupplier;
	private final Supplier<JoyScanner> joyScannerSupplier;
	private final Supplier<JoyProps> joyPropsSupplier;
	private final Consumers<WebApp> webAppConsumers;

	private ServletContext servletContext;
	private PetiteWebApp webApp;
	private Supplier<PetiteWebApp> webAppSupplier;

	public JoyMadvoc(
			final Supplier<String> appNameSupplier,
			final Supplier<JoyPetite> joyPetiteSupplier,
			final Supplier<JoyProxetta> joyProxettaSupplier,
			final Supplier<JoyProps> joyPropsSupplier,
			final Supplier<JoyScanner> joyScannerSupplier) {
		this.appNameSupplier = appNameSupplier;
		this.joyProxettaSupplier = joyProxettaSupplier;
		this.joyPetiteSupplier = joyPetiteSupplier;
		this.joyScannerSupplier = joyScannerSupplier;
		this.joyPropsSupplier = joyPropsSupplier;
		this.webAppConsumers = Consumers.empty();
	}

	/**
	 * Defines a web app supplier that creates custom {@link PetiteWebApp}.
	 */
	public void setWebAppSupplier(final Supplier<PetiteWebApp> webAppSupplier) {
		this.webAppSupplier = webAppSupplier;
	}

	/**
	 * Defines optional servlet context.
	 */
	public void setServletContext(final ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void add(final Consumer<WebApp> webAppConsumer) {
		this.webAppConsumers.add(webAppConsumer);
	}

	// ---------------------------------------------------------------- runtime

	public WebApp getWebApp() {
		return webApp;
	}

	// ---------------------------------------------------------------- lifecycle

	@Override
	public void start() {
		initLogger();

		log.info("MADVOC start  ----------");

		webApp = webAppSupplier == null ? new PetiteWebApp(joyPetiteSupplier.get().getPetiteContainer()) : webAppSupplier.get();

		webApp.withRegisteredComponent(ActionConfigManager.class, acm -> {
			acm.bindAnnotationConfig(Action.class, JoyActionConfig.class);
			acm.bindAnnotationConfig(RestAction.class, JoyRestActionConfig.class);
		});

		if (servletContext != null) {
			webApp.bindServletContext(servletContext);
		}

		final Props allProps = joyPropsSupplier.get().getProps();

		webApp.withParams(allProps.innerMap(beanNamePrefix()));

		webApp.registerComponent(new ProxettaSupplier(joyProxettaSupplier.get().getProxetta()));
		webApp.registerComponent(ProxettaAwareActionsManager.class);

		// Automagic Madvoc configurator will scan and register ALL!
		// This way we reduce the startup time and have only one scanning.
		// Scanning happens in the INIT phase.
		final AutomagicMadvocConfigurator automagicMadvocConfigurator =
			new AutomagicMadvocConfigurator(joyScannerSupplier.get().getClassScanner()) {
				@Override
				protected String createInfoMessage() {
					return "Scanning completed in " + elapsed + "ms.";
				}
			};

		webApp.registerComponent(automagicMadvocConfigurator);

		webAppConsumers.accept(webApp);

		webApp.start();

		log.info("MADVOC OK!");
	}

	protected String beanNamePrefix() {
		final String appName = appNameSupplier.get();
		return appName + ".madvoc.";
	}

	@Override
	public void stop() {
		if (log != null) {
			log.info("MADVOC stop  ----------");
		}
		if (webApp != null) {
			webApp.shutdown();
		}
		webApp = null;
	}

	// ---------------------------------------------------------------- print

	/**
	 * Prints routes to console.
	 */
	protected void printRoutes(final int width) {
		final ActionsManager actionsManager = webApp.madvocContainer().lookupComponent(ActionsManager.class);
		final List<ActionRuntime> actions = actionsManager.getAllActionRuntimes();
		final Map<String, String> aliases = actionsManager.getAllAliases();

		if (actions.isEmpty()) {
			return;
		}

		final Print print = new Print();

		print.line("Routes", width);

		actions.stream()
			.sorted(Comparator.comparing(
				actionRuntime -> actionRuntime.getActionPath() + ' ' + actionRuntime.getActionMethod()))
			.forEach(ar -> {

				final String actionMethod = ar.getActionMethod();

				print.out(Chalk256.chalk().yellow(), actionMethod == null ? "*" : actionMethod, 7);
				print.space();

				final String signature =
					ClassUtil.getShortClassName(
						ProxettaUtil.resolveTargetClass(ar.getActionClass()), 2)
						+ '#' + ar.getActionClassMethod().getName();

				print.outLeftRightNewLine(
					Chalk256.chalk().green(), ar.getActionPath(),
					Chalk256.chalk().blue(), signature,
					width - 7 - 1
				);
			});

		if (!aliases.isEmpty()) {

			print.line("Aliases", width);

			actions.stream()
				.sorted(Comparator.comparing(
					actionRuntime -> actionRuntime.getActionPath() + ' ' + actionRuntime.getActionMethod()))
				.forEach(ar -> {

					final String actionPath = ar.getActionPath();

					for (final Map.Entry<String, String> entry : aliases.entrySet()) {
						if (entry.getValue().equals(actionPath)) {
							print.space(8);

							print.outLeftRightNewLine(
								Chalk256.chalk().green(), entry.getValue(),
								Chalk256.chalk().blue(), entry.getKey(),
								width - 8
							);
						}
					}
				});
		}

		print.line(width);
	}

}
