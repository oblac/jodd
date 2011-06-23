// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.config;

import jodd.log.Log;
import jodd.petite.PetiteContainer;
import jodd.petite.PetiteException;
import jodd.petite.meta.PetiteBean;
import jodd.io.findfile.FindClass;
import jodd.util.ClassLoaderUtil;

import java.io.File;
import java.io.InputStream;

/**
 * Auto-magically configures Petite container by analyzing the classpath.
 * <p>
 * Scans all classes on classpath and in jar files, and scans for {@link jodd.petite.meta.PetiteBean}
 * annotation (not by loading the class!). If annotation is founded, class will be loaded and
 * registered as Petite bean.
 */
public class AutomagicPetiteConfigurator extends FindClass implements PetiteConfigurator {

	private static final Log log = Log.getLogger(AutomagicPetiteConfigurator.class);

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
			throw new PetiteException("Unable to scan classpath.", ex);
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
		PetiteBean petiteBean = beanClass.getAnnotation(PetiteBean.class);
		if (petiteBean == null) {
			return;
		}
		container.registerBean(beanClass);
	}

	/**
	 * Loads class from classname using default classloader.
	 */
	protected Class loadClass(String className) throws ClassNotFoundException {
		return ClassLoaderUtil.loadClass(className, this.getClass());
	}
}
