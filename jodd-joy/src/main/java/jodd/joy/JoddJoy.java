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

import jodd.Jodd;
import jodd.chalk.Chalk256;
import jodd.madvoc.WebApp;
import jodd.madvoc.petite.PetiteWebApp;
import jodd.petite.PetiteContainer;
import jodd.util.Consumers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JoddJoy {

	/**
	 * System property: application folder.
	 */
	public static final String APP_DIR = "app.dir";

	private static JoddJoy joddJoy;

	public synchronized static JoddJoy get() {
		if (joddJoy == null) {
			joddJoy = new JoddJoy();
		}
		return joddJoy;
	}

	public JoddJoy() {
		appName = "joy";
		joyPaths = new JoyPaths();
		joyPaths.start();

		joyScanner = new JoyScanner();
		joyProps = new JoyProps(() -> appName);

		joyProxetta = new JoyProxetta();
		joyPetite =
			new JoyPetite(
				() -> appName,
				() -> joyProxetta,
				() -> joyProps,
				() -> joyScanner
			);

		joyDb = new JoyDb(
			() -> appName,
			() -> joyPetite,
			() -> joyProxetta,
			() -> joyScanner);

		joyMadvoc = new JoyMadvoc(
			() -> appName,
			() -> joyPetite,
			() -> joyProxetta,
			() -> joyProps,
			() -> joyScanner
		);
	}

	// ---------------------------------------------------------------- name

	private String appName;

	/**
	 * Sets default Joy application name used for various components.
	 */
	public JoddJoy setApplicationName(final String name) {
		Objects.requireNonNull(name);
		this.appName = name;
		return this;
	}

	// ---------------------------------------------------------------- paths

	private final JoyPaths joyPaths;

	// ---------------------------------------------------------------- props

	private final JoyProps joyProps;

	private final Consumers<JoyPropsConfig> joyPropsConsumers = Consumers.empty();

	/**
	 * Configures Joy props before Joy is started.
	 */
	public JoddJoy withProps(final Consumer<JoyPropsConfig> propsConsumer) {
		joyPropsConsumers.add(propsConsumer);
		return this;
	}

	// ---------------------------------------------------------------- scanner

	private final JoyScanner joyScanner;

	/**
	 * Configures the Joy scanner.
	 */
	public JoddJoy withScanner(final Consumer<JoyScannerConfig> scannerConsumer) {
		scannerConsumer.accept(joyScanner);
		return this;
	}

	// ---------------------------------------------------------------- proxetta

	private final JoyProxetta joyProxetta;
	private final Consumers<JoyProxettaConfig> joyProxettaConsumers = Consumers.empty();

	/**
	 * Configures the Joy proxetta.
	 */
	public JoddJoy withProxetta(final Consumer<JoyProxettaConfig> proxettaConsumer) {
		joyProxettaConsumers.add(proxettaConsumer);
		return this;
	}

	// ---------------------------------------------------------------- petite

	private final JoyPetite joyPetite;

	private final Consumers<JoyPetiteConfig> joyPetiteConsumers = Consumers.empty();

	/**
	 * Configures the Joy petite.
	 */
	public JoddJoy withPetite(final Consumer<JoyPetiteConfig> petiteConsumer) {
		joyPetiteConsumers.add(petiteConsumer);
		return this;
	}

	// ---------------------------------------------------------------- db

	private final JoyDb joyDb;

	private final Consumers<JoyDbConfig> joyDbConsumers = Consumers.empty();

	/**
	 * Configures the Joy db.
	 */
	public JoddJoy withDb(final Consumer<JoyDbConfig> dbConsumer) {
		joyDbConsumers.add(dbConsumer);
		return this;
	}

	// ---------------------------------------------------------------- madvoc

	private final JoyMadvoc joyMadvoc;

	public JoddJoy withWebApp(final Consumer<WebApp> webAppConsumer) {
		joyMadvoc.add(webAppConsumer);
		return this;
	}

	/**
	 * Defines custom {@link PetiteWebApp} implementation.
	 */
	public JoddJoy useWebApp(final Supplier<PetiteWebApp> webAppSupplier) {
		joyMadvoc.setWebAppSupplier(webAppSupplier);
		return this;
	}

	// ---------------------------------------------------------------- start

	private Logger log;

	/**
	 * Starts the Joy without the web application.
	 */
	public JoddJoyRuntime startOnlyBackend() {
		return start(null);
	}

	/**
	 * Starts the Joy. Returns the {@link JoddJoyRuntime runtime}, set of running
	 * Joy components.
	 */
	public JoddJoyRuntime start(final ServletContext servletContext) {
		log = LoggerFactory.getLogger(JoddJoy.class);

		printLogo();

		log.info("Ah, Joy!");

		joyPropsConsumers.accept(joyProps);
		joyProxettaConsumers.accept(joyProxetta);
		joyDbConsumers.accept(joyDb);
		joyPetiteConsumers.accept(joyPetite);


		try {
			joyPaths.start();
			joyProps.start();
			joyProxetta.start();
			joyScanner.start();

			joyPetite.start();
			joyPetite.getPetiteContainer().addBean(appName + ".core",  this);
			joyPetite.getPetiteContainer().addBean(appName + ".scanner", joyScanner);

			joyDb.start();

			joyMadvoc.setServletContext(servletContext);
			joyMadvoc.start();

			printJoyConfiguration();

			runJoyInitBeans();

			// cleanup things we will not use

			joyScanner.stop();
		}
		catch (final Exception ex) {
			if (log != null) {
				log.error(ex.toString(), ex);
			} else {
				System.out.println(ex.toString());
				ex.printStackTrace();
			}
			stop();
			throw ex;
		}

		if (joyDb.isDatabaseEnabled()) {
			return new JoddJoyRuntime(
				appName,
				joyPaths.getAppDir(),
				joyProps.getProps(),
				joyProxetta.getProxetta(),
				joyPetite.getPetiteContainer(),
				joyMadvoc.getWebApp(),
				joyDb.isDatabaseEnabled(),
				joyDb.getConnectionProvider(),
				joyDb.getJtxManager()
			);
		}
		else {
			return new JoddJoyRuntime(
				appName,
				joyPaths.getAppDir(),
				joyProps.getProps(),
				joyProxetta.getProxetta(),
				joyPetite.getPetiteContainer(),
				joyMadvoc.getWebApp()
			);
		}
	}

	private void printJoyConfiguration() {
		joyPetite.printBeans(100);
		joyDb.printEntities(100);
		joyMadvoc.printRoutes(100);

		System.out.println(Chalk256.chalk().yellow().on("Joy") + " is up. Enjoy!");

		log.info("Joy is up. Enjoy!");
	}

	/**
	 * Prints a logo.
	 */
	private void printLogo() {
		System.out.println(Chalk256.chalk().yellow().on(Jodd.JODD));
	}

	/**
	 * Stops the Joy.
	 */
	public void stop() {
		joyProps.stop();
		try {
			joyDb.stop();
			joyPetite.stop();
		}
		catch (final Exception ignore) {
		}

		if (log != null) {
			log.info("Joy is down. Bye, bye!");
		}
	}

	protected void runJoyInitBeans() {
		final PetiteContainer pc = joyPetite.getPetiteContainer();
		pc.forEachBeanType(JoyInit.class, beanName -> {
			final JoyInit joyInit = pc.getBean(beanName);

			if (joyInit != null) {
				joyInit.onJoy();
			}
		});
	}

}
