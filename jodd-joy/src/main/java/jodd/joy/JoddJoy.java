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
import jodd.joy.server.Server;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.log.LoggerProvider;
import jodd.log.impl.SimpleLogger;
import jodd.madvoc.WebApp;
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

	// ---------------------------------------------------------------- name

	private String name = "joy";

	/**
	 * Default name used for various components.
	 */
	public JoddJoy setAppName(String name) {
		Objects.requireNonNull(name);
		this.name = name;
		return this;
	}

	// ---------------------------------------------------------------- logger

	private Supplier<LoggerProvider> loggerProviderSupplier;

	public JoddJoy withLoggerProvider(Supplier<LoggerProvider> loggerProviderSupplier) {
		this.loggerProviderSupplier = loggerProviderSupplier;
		return this;
	}

	// ---------------------------------------------------------------- server

	private Server server = new Server();

	/**
	 * Configures the server using a consumer.
	 */
	public JoddJoy withServer(Consumer<Server> serverConsumer) {
		serverConsumer.accept(server);
		return this;
	}

	// ---------------------------------------------------------------- paths

	private JoyPaths joyPaths = new JoyPaths();

	// ---------------------------------------------------------------- props

	private JoyProps joyProps = new JoyProps(() -> name);

	public JoddJoy withProps(Consumer<JoyProps.Config> propsConsumer) {
		propsConsumer.accept(joyProps.config);
		return this;
	}

	// ---------------------------------------------------------------- scanner

	private JoyScanner joyScanner = new JoyScanner();

	public JoddJoy withScanner(Consumer<JoyScanner> scannerConsumer) {
		scannerConsumer.accept(joyScanner);
		return this;
	}

	// ---------------------------------------------------------------- proxetta

	private JoyProxetta joyProxetta = new JoyProxetta();

	public JoddJoy withProxetta(Consumer<JoyProxetta.Config> proxettaConsumer) {
		proxettaConsumer.accept(joyProxetta.config);
		return this;
	}

	// ---------------------------------------------------------------- petite

	private JoyPetite joyPetite =
		new JoyPetite(
			() -> joyProxetta.proxetta(),
			() -> joyScanner,
			() -> joyProps.props());

	public JoddJoy withPetite(Consumer<JoyPetite.Config> petiteConsumer) {
		petiteConsumer.accept(joyPetite.config);
		return this;
	}

	// ---------------------------------------------------------------- db

	private JoyDb joyDb =
		new JoyDb(
			() -> joyPetite.petiteContainer(),
			() -> joyScanner);

	public JoddJoy withDb(Consumer<JoyDb.Config> dbConsumer) {
		dbConsumer.accept(joyDb.config());
		return this;
	}

	// ---------------------------------------------------------------- madvoc

	private JoyMadvoc joyMadvoc =
		new JoyMadvoc(
			() -> joyPetite.petiteContainer(),
			() -> joyProxetta.proxetta(),
			() -> joyScanner,
			() -> joyProps.props());

	public JoddJoy withWebApp(Consumer<WebApp> webAppConsumer) {
		joyMadvoc.add(webAppConsumer);
		return this;
	}

	// ---------------------------------------------------------------- start

	private Logger log;

	/**
	 * Starts the Joy.
	 */
	public void start(ServletContext servletContext) {
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

			joyScanner = null;
			joyProps = null;
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
	}

	protected void runJoyInitBeans() {
		final PetiteContainer pc = joyPetite.petiteContainer();
		pc.forEachBeanType(JoyInit.class, beanName -> {
			JoyInit joyInit = pc.getBean(beanName);
			if (joyInit != null) {
				joyInit.joy();
			}
		});
	}

	// ---------------------------------------------------------------- run

	public void run() {
		// start Tomcat/Jetty using server data, then letting the conext listener to finish the work.
	}

}