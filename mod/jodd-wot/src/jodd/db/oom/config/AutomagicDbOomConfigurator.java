// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.config;

import jodd.db.oom.DbOomManager;
import jodd.io.findfile.FindClass;
import jodd.db.oom.DbOomException;
import jodd.db.oom.meta.DbTable;
import jodd.log.Log;
import jodd.util.ClassLoaderUtil;

import java.io.File;
import java.io.InputStream;

/**
 * Auto-magically reads classpath for domain objects annotated
 */
public class AutomagicDbOomConfigurator extends FindClass {

	private static final Log log = Log.getLogger(AutomagicDbOomConfigurator.class);

	protected final byte[] dbTableAnnotationBytes;
	protected final boolean registerAsEntities;

	public AutomagicDbOomConfigurator(boolean registerAsEntities) {
		dbTableAnnotationBytes = getTypeSignatureBytes(DbTable.class);
		this.registerAsEntities = registerAsEntities;
	}
	public AutomagicDbOomConfigurator() {
		this(true);
	}

	protected DbOomManager dbOomManager;

	protected long elapsed;

	/**
	 * Return elapsed number of milliseconds for configuration.
	 */
	public long getElapsed() {
		return elapsed;
	}

	/**
	 * Configures {@link jodd.petite.PetiteContainer} with specified class path.
	 * @see AutomagicDbOomConfigurator#configure(jodd.db.oom.DbOomManager)
	 */
	public void configure(DbOomManager dbOomManager, File[] classpath) {
		this.dbOomManager = dbOomManager;
		elapsed = System.currentTimeMillis();
		try {
			scanPaths(classpath);
		} catch (Exception ex) {
			throw new DbOomException("Unable to scan classpath.", ex);
		}
		elapsed = System.currentTimeMillis() - elapsed;
		log.info("DbOomManager configured in " + elapsed + " ms. Total entities: " + dbOomManager.getTotalNames());
	}

	/**
	 * Configures {@link jodd.petite.PetiteContainer} with default class path.
	 * @see AutomagicDbOomConfigurator#configure(jodd.db.oom.DbOomManager, java.io.File[])
	 */
	public void configure(DbOomManager dbOomManager) {
		configure(dbOomManager, ClassLoaderUtil.getDefaultClasspath());
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
		if (isTypeSignatureInUse(inputStream, dbTableAnnotationBytes) == false) {
			return;
		}

		Class<?> beanClass;
		try {
			beanClass = loadClass(entryName);
		} catch (ClassNotFoundException cnfex) {
			throw new DbOomException("Unable to load class: " + entryName, cnfex);
		}
		DbTable dbTable = beanClass.getAnnotation(DbTable.class);
		if (dbTable == null) {
			return;
		}
		if (registerAsEntities == true) {
			dbOomManager.registerEntity(beanClass);
		} else {
			dbOomManager.registerType(beanClass);
		}
	}

	/**
	 * Loads class from classname using default classloader.
	 */
	protected Class loadClass(String className) throws ClassNotFoundException {
		return ClassLoaderUtil.loadClass(className, this.getClass());
	}
}
