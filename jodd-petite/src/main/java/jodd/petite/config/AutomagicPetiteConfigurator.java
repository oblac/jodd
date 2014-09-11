// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

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