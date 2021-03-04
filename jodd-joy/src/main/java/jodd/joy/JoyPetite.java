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
import jodd.petite.AutomagicPetiteConfigurator;
import jodd.petite.BeanDefinition;
import jodd.petite.PetiteContainer;
import jodd.petite.proxetta.ProxettaAwarePetiteContainer;
import jodd.util.ClassUtil;
import jodd.util.Consumers;
import jodd.util.StringUtil;
import jodd.util.TypeCache;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JoyPetite extends JoyBase implements JoyPetiteConfig {

	protected final Supplier<String> appNameSupplier;
	protected final Supplier<JoyScanner> joyScannerSupplier;
	protected final Supplier<JoyProps> joyPropsSupplier;
	protected final Supplier<JoyProxetta> joyProxettaSupplier;

	protected PetiteContainer petiteContainer;
	protected boolean isWebApplication = true;

	public JoyPetite(
			final Supplier<String> appNameSupplier,
			final Supplier<JoyProxetta> joyProxettaSupplier,
			final Supplier<JoyProps> joyPropsSupplier,
			final Supplier<JoyScanner> joyScannerSupplier) {
		this.appNameSupplier = appNameSupplier;
		this.joyProxettaSupplier = joyProxettaSupplier;
		this.joyScannerSupplier = joyScannerSupplier;
		this.joyPropsSupplier = joyPropsSupplier;
	}

	// ---------------------------------------------------------------- runtime

	/**
	 * Returns PetiteContainer once when it is created.
	 */
	public PetiteContainer getPetiteContainer() {
		return requireStarted(petiteContainer);
	}

	// ---------------------------------------------------------------- config

	private boolean autoConfiguration = true;
	private boolean externalsCache = true;
	private final Consumers<PetiteContainer> petiteContainerConsumers = Consumers.empty();

	@Override
	public JoyPetite disableAutoConfiguration() {
		requireNotStarted(petiteContainer);
		autoConfiguration = false;
		return this;
	}

	@Override
	public JoyPetite disableExternalsCache() {
		requireNotStarted(petiteContainer);
		externalsCache = true;
		return this;
	}

	@Override
	public JoyPetite withPetite(final Consumer<PetiteContainer> petiteContainerConsumer) {
		requireNotStarted(petiteContainer);
		petiteContainerConsumers.add(petiteContainerConsumer);
		return this;
	}

	// ---------------------------------------------------------------- lifecycle

	/**
	 * Creates and initializes Petite container.
	 * It will be auto-magically configured by scanning the classpath.
	 */
	@Override
	public void start() {
		initLogger();

		log.info("PETITE start  ----------");

		petiteContainer = createPetiteContainer();

		if (externalsCache) {
			petiteContainer.setExternalsCache(TypeCache.createDefault());
		}

		log.info("Web application? " + isWebApplication);

		if (!isWebApplication) {
			// make session scope to act as singleton scope
			// if this is not a web application (and http session is not available).
			//petiteContainer.registerScope(SessionScope.class, new SingletonScope(petiteContainer));
		}

		// load parameters from properties files
		petiteContainer.defineParameters(joyPropsSupplier.get().getProps());

		// automagic configuration
		if (autoConfiguration) {
			final AutomagicPetiteConfigurator automagicPetiteConfigurator =
				new AutomagicPetiteConfigurator(petiteContainer);

			automagicPetiteConfigurator.configure();
		}

		petiteContainerConsumers.accept(this.petiteContainer);

		log.info("PETITE OK!");
	}

	protected ProxettaAwarePetiteContainer createPetiteContainer() {
		return new ProxettaAwarePetiteContainer(joyProxettaSupplier.get().getProxetta());
	}

	/**
	 * Stops Petite container.
	 */
	@Override
	public void stop() {
		if (log != null) {
			log.info("PETITE stop");
		}
		if (petiteContainer != null) {
			petiteContainer.shutdown();
		}
		petiteContainer = null;
	}

	// ---------------------------------------------------------------- print

	public void printBeans(final int width) {
		final Print print = new Print();

		print.line("Beans", width);

		final List<BeanDefinition> beanDefinitionList = new ArrayList<>();
		final String appName = appNameSupplier.get();
		final String prefix = appName + ".";

		petiteContainer.forEachBean(beanDefinitionList::add);

		beanDefinitionList.stream()
			.sorted((bd1, bd2) -> {
				if (bd1.name().startsWith(prefix)) {
					if (bd2.name().startsWith(prefix)) {
						return bd1.name().compareTo(bd2.name());
					}
					return 1;
				}
				if (bd2.name().startsWith(prefix)) {
					if (bd1.name().startsWith(prefix)) {
						return bd1.name().compareTo(bd2.name());
					}
					return -1;
				}
				return bd1.name().compareTo(bd2.name());
			})
			.forEach(beanDefinition -> {
				print.out(Chalk256.chalk().yellow(), scopeName(beanDefinition), 10);
				print.space();


				print.outLeftRightNewLine(
					Chalk256.chalk().green(), beanDefinition.name(),
					Chalk256.chalk().blue(), ClassUtil.getShortClassName(beanDefinition.type(), 2),
					width - 10 - 1
				);
			});

		print.line(width);
	}

	private String scopeName(final BeanDefinition beanDefinition) {
		String scopeName = beanDefinition.scope().getSimpleName();

		scopeName = StringUtil.cutSuffix(scopeName, "Scope");

		return scopeName.toLowerCase();
	}
}
