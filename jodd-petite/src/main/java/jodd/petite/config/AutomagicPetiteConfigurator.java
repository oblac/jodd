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

package jodd.petite.config;

import jodd.petite.PetiteContainer;
import jodd.petite.PetiteException;
import jodd.petite.meta.PetiteBean;
import jodd.io.findfile.ClassFinder;
import jodd.util.ClassLoaderUtil;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

import java.io.File;
import java.io.InputStream;

/**
 * Auto-magically configures Petite container by analyzing the classpath.
 * <p>
 * Scans all classes on classpath and in jar files, and scans for {@link jodd.petite.meta.PetiteBean}
 * annotation (not by loading the class!). If annotation is founded, class will be loaded and
 * registered as Petite bean.
 */
public class AutomagicPetiteConfigurator extends ClassFinder implements PetiteConfigurator {

	private static final Logger log = LoggerFactory.getLogger(AutomagicPetiteConfigurator.class);

	protected final byte[] petiteBeanAnnotationBytes;

	public AutomagicPetiteConfigurator() {
		petiteBeanAnnotationBytes = getTypeSignatureBytes(PetiteBean.class);
	}

	protected PetiteContainer container;

	protected long elapsed;

	/**
	 * Return elapsed number of milliseconds for configuration. 
	 */
	public long getElapsed() {
		return elapsed;
	}

	/**
	 * Configures {@link jodd.petite.PetiteContainer} with specified class path.
	 * @see AutomagicPetiteConfigurator#configure(jodd.petite.PetiteContainer)
	 */
	public void configure(PetiteContainer petiteContainer, File[] classpath) {
		this.container = petiteContainer;

		rulesEntries.smartMode();

		elapsed = System.currentTimeMillis();
		try {
			scanPaths(classpath);
		} catch (Exception ex) {
			throw new PetiteException("Scan classpath error", ex);
		}
		elapsed = System.currentTimeMillis() - elapsed;
		log.info("Petite configured in " + elapsed + " ms. Total beans: " + petiteContainer.getTotalBeans());
	}

	/**
	 * Configures {@link jodd.petite.PetiteContainer} with default class path.
	 * @see AutomagicPetiteConfigurator#configure(jodd.petite.PetiteContainer, java.io.File[])
	 */
	public void configure(PetiteContainer petiteContainer) {
		configure(petiteContainer, ClassLoaderUtil.getDefaultClasspath());
	}

	/**
	 * Scans all classes and registers only those annotated with {@link jodd.petite.meta.PetiteBean}.
	 * Because of performance purposes, classes are not dynamically loaded; instead, their
	 * file content is examined. 
	 */
	@Override
	protected void onEntry(EntryData entryData) {
		String entryName = entryData.getName();
		InputStream inputStream = entryData.openInputStream();
		if (isTypeSignatureInUse(inputStream, petiteBeanAnnotationBytes) == false) {
			return;
		}
		Class<?> beanClass;

		try {
			beanClass = loadClass(entryName);
		} catch (ClassNotFoundException cnfex) {
			throw new PetiteException("Unable to load class: " + cnfex, cnfex);
		}

		if (beanClass == null) {
			return;
		}

		PetiteBean petiteBean = beanClass.getAnnotation(PetiteBean.class);
		if (petiteBean == null) {
			return;
		}
		container.registerPetiteBean(beanClass, null, null, null, false);
	}

}