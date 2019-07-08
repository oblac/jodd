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

package jodd.petite;

import jodd.io.findfile.ClassScanner;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.petite.meta.PetiteBean;
import jodd.util.function.Consumers;

import java.util.function.Consumer;

/**
 * Auto-magically configures Petite container by analyzing the classpath.
 * <p>
 * Scans all classes on classpath and in jar files, and scans for {@link jodd.petite.meta.PetiteBean}
 * annotation (not by loading the class!). If annotation is founded, class will be loaded and
 * registered as Petite bean.
 */
public class AutomagicPetiteConfigurator {

	private static final Logger log = LoggerFactory.getLogger(AutomagicPetiteConfigurator.class);
	private final static byte[] PETITE_BEAN_ANNOTATION_BYTES = ClassScanner.bytecodeSignatureOfType(PetiteBean.class);
	private final PetiteContainer container;
	private final Consumers<ClassScanner> classScannerConsumers = new Consumers<>();

	public AutomagicPetiteConfigurator(final PetiteContainer petiteContainer) {
		this.container = petiteContainer;
	}

	public AutomagicPetiteConfigurator withScanner(final Consumer<ClassScanner> scannerConsumer) {
		classScannerConsumers.add(scannerConsumer);
		return this;
	}

	/**
	 * Configures {@link jodd.petite.PetiteContainer} with specified class path.
	 */
	public void configure() {
		long elapsed = System.currentTimeMillis();

		final ClassScanner classScanner = new ClassScanner();

		classScanner.detectEntriesMode(true);
		classScanner.scanDefaultClasspath();
		classScannerConsumers.accept(classScanner);

		registerAsConsumer(classScanner);

		try {
			classScanner.start();
		} catch (Exception ex) {
			throw new PetiteException("Scan classpath error", ex);
		}
		elapsed = System.currentTimeMillis() - elapsed;
		log.info("Petite configured in " + elapsed + " ms. Total beans: " + container.beansCount());
	}

	/**
	 * Registers a class consumer that registers only those annotated with {@link jodd.petite.meta.PetiteBean}.
	 * Because of performance purposes, classes are not dynamically loaded; instead, their
	 * file content is examined.
	 */
	public void registerAsConsumer(final ClassScanner classScanner) {
		classScanner.registerEntryConsumer(classPathEntry -> {
			if (!classPathEntry.isTypeSignatureInUse(PETITE_BEAN_ANNOTATION_BYTES)) {
				return;
			}

			final Class<?> beanClass;

			try {
				beanClass = classPathEntry.loadClass();
			} catch (ClassNotFoundException cnfex) {
				throw new PetiteException("Unable to load class: " + cnfex, cnfex);
			}

			if (beanClass == null) {
				return;
			}

			final PetiteBean petiteBean = beanClass.getAnnotation(PetiteBean.class);

			if (petiteBean == null) {
				return;
			}
			container.registerPetiteBean(beanClass, null, null, null, false, null);
		});
	}
}