// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.config;

import jodd.io.findfile.FindClass;
import jodd.db.orm.DbOrmManager;
import jodd.db.orm.DbOrmException;
import jodd.db.orm.meta.DbTable;
import jodd.util.ClassLoaderUtil;

import java.net.URL;
import java.io.InputStream;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Auto-magically reads classpath for domain objects annotated
 */
public class AutomagicDbOrmConfigurator extends FindClass {

	private static final Logger log = LoggerFactory.getLogger(AutomagicDbOrmConfigurator.class);

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
	public void configure(DbOrmManager dbOrmManager, URL[] classpath) {
		this.dbOrmManager = dbOrmManager;
		elapsed = System.currentTimeMillis();
		try {
			scanUrls(classpath);
		} catch (Exception ex) {
			throw new DbOrmException("Unable to scan classpath.", ex);
		}
		elapsed = System.currentTimeMillis() - elapsed;
		log.info("DbOrmManager configured in " + elapsed + " ms. Total entities: " + dbOrmManager.getTotalNames());
	}

	/**
	 * Configures {@link jodd.petite.PetiteContainer} with default class path.
	 * @see AutomagicDbOrmConfigurator#configure(jodd.db.orm.DbOrmManager , java.net.URL[])
	 */
	public void configure(DbOrmManager dbOrmManager) {
		configure(dbOrmManager, ClassLoaderUtil.getFullClassPath(AutomagicDbOrmConfigurator.class));
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
