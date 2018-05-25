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

import jodd.cache.TypeCache;
import jodd.petite.AutomagicPetiteConfigurator;
import jodd.petite.BeanDefinition;
import jodd.petite.PetiteContainer;
import jodd.petite.proxetta.ProxettaAwarePetiteContainer;
import jodd.petite.scope.SessionScope;
import jodd.petite.scope.SingletonScope;
import jodd.props.Props;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.util.Chalk256;
import jodd.util.ClassUtil;
import jodd.util.Consumers;
import jodd.util.StringUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static jodd.joy.JoddJoy.PETITE_CORE;
import static jodd.joy.JoddJoy.PETITE_DBPOOL;
import static jodd.joy.JoddJoy.PETITE_SCANNER;

public class JoyPetite extends JoyBase {

	protected final Supplier<JoyScanner> joyScannerSupplier;
	protected final Supplier<JoyDb> joyDbSupplier;
	protected final Supplier<Props> propsSupplier;
	protected final Supplier<ProxyProxetta> proxettaSupplier;

	protected PetiteContainer petiteContainer;
	protected boolean isWebApplication = true;

	public JoyPetite(
			final Supplier<ProxyProxetta> proxettaSupplier,
			final Supplier<Props> propsSupplier,
			final Supplier<JoyDb> joyDbSupplier,
			final Supplier<JoyScanner> joyScannerSupplier) {
		this.proxettaSupplier = proxettaSupplier;
		this.joyScannerSupplier = joyScannerSupplier;
		this.propsSupplier = propsSupplier;
		this.joyDbSupplier = joyDbSupplier;
	}

	// ---------------------------------------------------------------- getters

	/**
	 * Returns PetiteContainer once when it is created.
	 */
	public PetiteContainer getPetiteContainer() {
		return petiteContainer;
	}

	// ---------------------------------------------------------------- config

	private boolean autoConfiguration = true;
	private boolean externalsCache = true;
	private Consumers<PetiteContainer> petiteContainerConsumers = Consumers.empty();

	public JoyPetite disableAutoConfiguration() {
		autoConfiguration = false;
		return this;
	}

	public JoyPetite disableExternalsCache() {
		externalsCache = true;
		return this;
	}

	public JoyPetite withPetite(final Consumer<PetiteContainer> petiteContainerConsumer) {
		petiteContainerConsumers.add(petiteContainerConsumer);
		return this;
	}

	// ---------------------------------------------------------------- lifecycle

	/**
	 * Creates and initializes Petite container.
	 * It will be auto-magically configured by scanning the classpath.
	 * Also, all 'app*.prop*' will be loaded and values will
	 * be injected in the matched beans. At the end it registers
	 * this instance of core into the container.
	 */
	@Override
	void start() {
		initLogger();

		log.info("PETITE start  ----------");

		petiteContainer = createPetiteContainer();

		if (externalsCache) {
			petiteContainer.setExternalsCache(TypeCache.createDefault());
		}

		log.info("Is web application: " + isWebApplication);

		if (!isWebApplication) {
			// make session scope to act as singleton scope
			// if this is not a web application (and http session is not available).
			petiteContainer.registerScope(SessionScope.class, new SingletonScope(petiteContainer));
		}

		// load parameters from properties files
		petiteContainer.defineParameters(propsSupplier.get());

		petiteContainer.addBean(PETITE_SCANNER, joyScannerSupplier.get());

		// automagic configuration
		if (autoConfiguration) {
			final AutomagicPetiteConfigurator automagicPetiteConfigurator =
				new AutomagicPetiteConfigurator(petiteContainer);

			automagicPetiteConfigurator.registerAsConsumer(joyScannerSupplier.get().classScanner());
		}

		log.debug("Petite manual configuration started...");
		petiteContainerConsumers.accept(this.petiteContainer);

		// add AppCore instance to Petite
		petiteContainer.addBean(PETITE_CORE, petiteContainer);

		// add DB beans
		petiteContainer.addBean(PETITE_DBPOOL, joyDbSupplier.get().connectionProvider);
	}

	protected ProxettaAwarePetiteContainer createPetiteContainer() {
		return new ProxettaAwarePetiteContainer(proxettaSupplier.get());
	}

	/**
	 * Stops Petite container.
	 */
	@Override
	void stop() {
		if (log != null) {
			log.info("PETITE stop");
		}
		if (petiteContainer != null) {
			petiteContainer.shutdown();
		}
	}

	public void printBeans(final int width) {
		final Print print = new Print();

		print.line("Beans", width);

		final List<BeanDefinition> beanDefinitionList = new ArrayList<>();
		petiteContainer.forEachBean(beanDefinitionList::add);

		beanDefinitionList.stream()
			.sorted(Comparator.comparing(BeanDefinition::name))
			.forEach(beanDefinition -> {
				print.out(Chalk256.chalk().yellow(), scopeName(beanDefinition), 10);
				print.space();

				print.out(Chalk256.chalk().green(), beanDefinition.name(), 30);
				print.space();

				final int remaining = width - 10 - 1 - 30 - 1;
				print.outRight(Chalk256.chalk().blue(), ClassUtil.getShortClassName(beanDefinition.type(), 2), remaining);

				print.newLine();
			});

		print.line(width);
	}

	private String scopeName(final BeanDefinition beanDefinition) {
		String scopeName = beanDefinition.scope().getSimpleName();

		scopeName = StringUtil.cutSuffix(scopeName, "Scope");

		return scopeName.toLowerCase();
	}
}
