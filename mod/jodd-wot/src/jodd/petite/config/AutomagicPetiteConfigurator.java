// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.config;

import jodd.petite.PetiteContainer;
import jodd.petite.PetiteException;
import jodd.petite.meta.PetiteBean;
import jodd.io.findfile.FindClass;
import jodd.util.ClassLoaderUtil;

import java.net.URL;
import java.io.InputStream;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Auto-magically configures Petite container by analyzing the classpath.
 * <p>
 * Scans all classes on classpath and in jar files, and scans for {@link jodd.petite.meta.PetiteBean}
 * annotation (not by loading the class!). If annotation is founded, class will be loaded and
 * registered as Petite bean.
 */
public class AutomagicPetiteConfigurator extends FindClass implements PetiteConfigurator {

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
	public void configure(PetiteContainer petiteContainer, URL[] classpath) {
		this.container = petiteContainer;
		elapsed = System.currentTimeMillis();
		try {
			scanUrls(classpath);
		} catch (Exception ex) {
			throw new PetiteException("Unable to scan classpath.", ex);
		}
		elapsed = System.currentTimeMillis() - elapsed;
		log.info("Petite configured in " + elapsed + " ms. Total beans: " + petiteContainer.getManager().getTotalBeans());
	}

	/**
	 * Configures {@link jodd.petite.PetiteContainer} with default class path.
	 * @see AutomagicPetiteConfigurator#configure(jodd.petite.PetiteContainer, java.net.URL[])
	 */
	public void configure(PetiteContainer petiteContainer) {
		configure(petiteContainer, ClassLoaderUtil.getFullClassPath(AutomagicPetiteConfigurator.class));
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
