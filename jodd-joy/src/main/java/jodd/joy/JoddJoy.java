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

import javax.servlet.ServletContext;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JoddJoy {

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
	public JoddJoy name(String name) {
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

	private JoyPath joyPath = new JoyPath();

	// ---------------------------------------------------------------- props

	private JoyProps joyProps = new JoyProps(() -> name);

	public JoddJoy withProps(JoyProps joyProps) {
		Objects.requireNonNull(joyProps);
		this.joyProps = joyProps;
		return this;
	}
	public JoddJoy withProps(Consumer<JoyProps.Config> propsConsumer) {
		propsConsumer.accept(joyProps.config);
		return this;
	}

	// ---------------------------------------------------------------- scanner

	private JoyScanner joyScanner = new JoyScanner();

	public JoddJoy withScanner(JoyScanner joyScanner) {
		Objects.requireNonNull(joyScanner);
		this.joyScanner = joyScanner;
		return this;
	}
	public JoddJoy withScanner(Consumer<JoyScanner> scannerConsumer) {
		scannerConsumer.accept(joyScanner);
		return this;
	}

	// ---------------------------------------------------------------- proxetta

	private JoyProxetta joyProxetta = new JoyProxetta();

	public JoddJoy withProxetta(JoyProxetta joyProxetta) {
		Objects.requireNonNull(joyProxetta);
		this.joyProxetta = joyProxetta;
		return this;
	}
	public JoddJoy withProxetta(Consumer<JoyProxetta.Config> proxettaConsumer) {
		proxettaConsumer.accept(joyProxetta.config);
		return this;
	}

	// ---------------------------------------------------------------- petite

	private JoyPetite joyPetite =
		new JoyPetite(() -> joyProxetta.proxetta(), () -> joyScanner, () -> joyProps.props());

	public JoddJoy withPetite(JoyPetite joyPetite) {
		Objects.requireNonNull(joyPetite);
		this.joyPetite = joyPetite;
		return this;
	}
	public JoddJoy withPetite(Consumer<JoyPetite.Config> petiteConsumer) {
		petiteConsumer.accept(joyPetite.config);
		return this;
	}

	// ---------------------------------------------------------------- db

	private JoyDb joyDb = new JoyDb(() -> joyScanner);

	public JoddJoy withDb(JoyDb joyDb) {
		Objects.requireNonNull(joyDb);
		this.joyDb = joyDb;
		return this;
	}
	public JoddJoy withDb(Consumer<JoyDb.Config> dbConsumer) {
		dbConsumer.accept(joyDb.config());
		return this;
	}

	// ---------------------------------------------------------------- madvoc

	private JoyMadvoc joyMadvoc = new JoyMadvoc(() -> joyProxetta.proxetta());

	// ---------------------------------------------------------------- start

	private Logger log;

	// starts everything
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

		joyPath.start();
		joyProps.start();
		joyScanner.start();
		joyProxetta.start();
		joyPetite.start();
		joyDb.start();

		joyMadvoc.setServletContext(servletContext);
		joyMadvoc.start();

		// cleanup
		joyScanner = null;
		joyProps = null;
	}

	public void stop() {
		joyDb.stop();
	}

	// ---------------------------------------------------------------- run

	public void run() {
		// start Tomcat/Jetty using server data, then letting the conext listener to finish the work.
	}

}