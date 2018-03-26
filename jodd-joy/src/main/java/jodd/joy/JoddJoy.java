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
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.log.LoggerProvider;
import jodd.log.impl.SimpleLogger;
import jodd.madvoc.WebApp;
import jodd.madvoc.petite.PetiteWebApp;
import jodd.petite.PetiteContainer;

import javax.servlet.ServletContext;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JoddJoy {

	/**
	 * System property: application folder.
	 */
	public static final String APP_DIR = "app.dir";
	/**
	 * Petite bean name for AppCore (this instance).
	 */
	public static final String PETITE_CORE = "core";
	/**
	 * Petite bean name for database pool.
	 */
	public static final String PETITE_DBPOOL = "dbpool";
	/**
	 * Petite bean name for database configuration.
	 */
	public static final String PETITE_DB = "db";
	/**
	 * Petite bean name for {@link JoyScanner} bean.
	 */
	public static final String PETITE_SCAN = "scan";

	private static JoddJoy joddJoy;

	public synchronized static JoddJoy get() {
		if (joddJoy == null) {
			joddJoy = new JoddJoy();
		}
		return joddJoy;
	}

	public JoddJoy() {
		joyPaths.start();
	}

	// ---------------------------------------------------------------- name

	private String name = "joy";

	/**
	 * Default name used for various components.
	 */
	public JoddJoy setAppName(final String name) {
		Objects.requireNonNull(name);
		this.name = name;
		return this;
	}

	// ---------------------------------------------------------------- logger

	private Supplier<LoggerProvider> loggerProviderSupplier;

	public JoddJoy withLoggerProvider(final Supplier<LoggerProvider> loggerProviderSupplier) {
		this.loggerProviderSupplier = loggerProviderSupplier;
		return this;
	}

	// ---------------------------------------------------------------- paths

	private final JoyPaths joyPaths = new JoyPaths();

	// ---------------------------------------------------------------- props

	private final JoyProps joyProps = new JoyProps(() -> name);

	public JoddJoy withProps(final Consumer<JoyProps> propsConsumer) {
		propsConsumer.accept(joyProps);
		return this;
	}

	// ---------------------------------------------------------------- scanner

	private final JoyScanner joyScanner = new JoyScanner();

	public JoddJoy withScanner(final Consumer<JoyScanner> scannerConsumer) {
		scannerConsumer.accept(joyScanner);
		return this;
	}

	// ---------------------------------------------------------------- proxetta

	private final JoyProxetta joyProxetta = new JoyProxetta();

	public JoddJoy withProxetta(final Consumer<JoyProxetta> proxettaConsumer) {
		proxettaConsumer.accept(joyProxetta);
		return this;
	}

	// ---------------------------------------------------------------- petite

	private final JoyPetite joyPetite =
		new JoyPetite(
			joyProxetta::getProxetta,
			joyProps::getProps,
			() -> joyScanner
		);

	public JoddJoy withPetite(final Consumer<JoyPetite> petiteConsumer) {
		petiteConsumer.accept(joyPetite);
		return this;
	}

	// ---------------------------------------------------------------- db

	private JoyDb joyDb =
		new JoyDb(
			joyPetite::getPetiteContainer,
			() -> joyScanner);

	public JoddJoy withDb(final Consumer<JoyDb> dbConsumer) {
		dbConsumer.accept(joyDb);
		return this;
	}

	// ---------------------------------------------------------------- madvoc

	private JoyMadvoc joyMadvoc =
		new JoyMadvoc(
			joyPetite::getPetiteContainer,
			joyProxetta::getProxetta,
			joyProps::getProps,
			() -> joyScanner
		);

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
	 * Starts the Joy.
	 */
	public void start(final ServletContext servletContext) {
		LoggerProvider loggerProvider = null;

		if (loggerProviderSupplier != null) {
			loggerProvider = loggerProviderSupplier.get();
		}
		if (loggerProvider == null) {
			loggerProvider = SimpleLogger.PROVIDER;
		}

		LoggerFactory.setLoggerProvider(loggerProvider);
		log = LoggerFactory.getLogger(JoddJoy.class);

		log.info(Jodd.JODD);
		log.info("Ah, Joy!");
		log.info("Logging using: " + loggerProvider.getClass().getSimpleName());

		try {
			joyPaths.start();
			joyProps.start();
			joyScanner.start();
			joyProxetta.start();
			joyPetite.start();
			joyDb.start();

			joyMadvoc.setServletContext(servletContext);
			joyMadvoc.start();

			runJoyInitBeans();

			// cleanup things we will not use

			// todo optimization
			joyScanner.stop();
			joyProps.stop();
		}
		catch (Exception ex) {
			if (log != null) {
				log.error(ex.toString(), ex);
			} else {
				System.out.println(ex.toString());
				ex.printStackTrace();
			}
			stop();
			throw ex;
		}

		log.info("Joy is up. Enjoy Joy!");
	}

	/**
	 * Stops the Joy.
	 */
	public void stop() {
		try {
			joyDb.stop();
			joyPetite.stop();
		}
		catch (Exception ignore) {
		}

		if (log != null) {
			log.info("Joy is stopped! Bye, bye!");
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