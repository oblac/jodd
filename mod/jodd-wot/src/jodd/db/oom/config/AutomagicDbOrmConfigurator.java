// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.config;

import jodd.io.findfile.FindClass;
import jodd.db.oom.DbOrmManager;
import jodd.db.oom.DbOrmException;
import jodd.db.oom.meta.DbTable;
import jodd.log.Log;
import jodd.util.ClassLoaderUtil;

import java.io.File;
import java.io.InputStream;

/**
 * Auto-magically reads classpath for domain objects annotated
 */
public class AutomagicDbOrmConfigurator extends FindClass {

	private static final Log log = Log.getLogger(AutomagicDbOrmConfigurator.class);

	protected final byte[] dbTableAnnotationBytes;
	protected final boolean registerAsEntities;

	public AutomagicDbOrmConfigurator(boolean registerAsEntities) {
		dbTableAnnotationBytes = getTypeSignatureBytes(DbTable.class);
		this.registerAsEntities = registerAsEntities;
	}
	public AutomagicDbOrmConfigurator() {
		this(true);
	}

	protected DbOrmManager dbOrmManager;

	protected long elapsed;

	/**
	 * Return elapsed number of milliseconds for configuration.
	 */
	public long getElapsed() {
		return elapsed;
	}

	/**
	 * Configures {@link jodd.petite.PetiteContainer} with specified class path.
	 * @see AutomagicDbOrmConfigurator#configure(jodd.db.orm.DbOrmManager)
	 */
	public void configure(DbOrmManager dbOrmManager, File[] classpath) {
		this.dbOrmManager = dbOrmManager;
		elapsed = System.currentTimeMillis();
		try {
			scanPaths(classpath);
		} catch (Exception ex) {
			throw new DbOrmException("Unable to scan classpath.", ex);
		}
		elapsed = System.currentTimeMillis() - elapsed;
		log.info("DbOrmManager configured in " + elapsed + " ms. Total entities: " + dbOrmManager.getTotalNames());
	}

	/**
	 * Configures {@link jodd.petite.PetiteContainer} with default class path.
	 * @see AutomagicDbOrmConfigurator#configure(jodd.db.orm.DbOrmManager, java.io.File[])
	 */
	public void configure(DbOrmManager dbOrmManager) {
		configure(dbOrmManager, ClassLoaderUtil.getDefaultClasspath());
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
			throw new DbOrmException("Unable to load class: " + entryName, cnfex);
		}
		DbTable dbTable = beanClass.getAnnotation(DbTable.class);
		if (dbTable == null) {
			return;
		}
		if (registerAsEntities == true) {
			dbOrmManager.registerEntity(beanClass);
		} else {
			dbOrmManager.registerType(beanClass);
		}
	}

	/**
	 * Loads class from classname using default classloader.
	 */
	protected Class loadClass(String className) throws ClassNotFoundException {
		return ClassLoaderUtil.loadClass(className, this.getClass());
	}
}
