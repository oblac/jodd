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

package jodd.db.oom.config;

import jodd.db.oom.DbEntityManager;
import jodd.db.oom.DbOomException;
import jodd.db.oom.meta.DbTable;
import jodd.io.findfile.ClassFinder;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.util.ClassLoaderUtil;

import java.io.File;
import java.io.InputStream;

/**
 * Auto-magically scans classpath for domain objects annotated with DbOom annotations.
 */
public class AutomagicDbOomConfigurator extends ClassFinder {

	private static final Logger log = LoggerFactory.getLogger(AutomagicDbOomConfigurator.class);

	protected final byte[] dbTableAnnotationBytes;
	protected final boolean registerAsEntities;

	public AutomagicDbOomConfigurator(boolean registerAsEntities) {
		dbTableAnnotationBytes = getTypeSignatureBytes(DbTable.class);
		this.registerAsEntities = registerAsEntities;
	}
	public AutomagicDbOomConfigurator() {
		this(true);
	}

	protected DbEntityManager dbEntityManager;

	protected long elapsed;

	/**
	 * Return elapsed number of milliseconds for configuration.
	 */
	public long getElapsed() {
		return elapsed;
	}

	/**
	 * Configures {@link DbEntityManager} with specified class path.
	 * @see AutomagicDbOomConfigurator#configure(DbEntityManager)
	 */
	public void configure(DbEntityManager dbEntityManager, File[] classpath) {
		this.dbEntityManager = dbEntityManager;

		rulesEntries.smartMode();

		elapsed = System.currentTimeMillis();
		try {
			scanPaths(classpath);
		} catch (Exception ex) {
			throw new DbOomException("Scan classpath error", ex);
		}
		elapsed = System.currentTimeMillis() - elapsed;
		if (log.isInfoEnabled()) {
			log.info("DbEntityManager configured in " + elapsed + " ms. Total entities: " + dbEntityManager.getTotalNames());
		}
	}

	/**
	 * Configures {@link DbEntityManager} with default class path.
	 * @see AutomagicDbOomConfigurator#configure(DbEntityManager, java.io.File[])
	 */
	public void configure(DbEntityManager dbEntityManager) {
		configure(dbEntityManager, ClassLoaderUtil.getDefaultClasspath());
	}

	/**
	 * Scans all classes and registers only those annotated with {@link DbTable}.
	 * Because of performance purposes, classes are not dynamically loaded; instead, their
	 * file content is examined.
	 */
	@Override
	protected void onEntry(EntryData entryData) {
		String entryName = entryData.getName();
		InputStream inputStream = entryData.openInputStream();
		if (!isTypeSignatureInUse(inputStream, dbTableAnnotationBytes)) {
			return;
		}

		Class<?> beanClass;
		try {
			beanClass = loadClass(entryName);
		} catch (ClassNotFoundException cnfex) {
			throw new DbOomException("Entry class not found: " + entryName, cnfex);
		}

		if (beanClass == null) {
			return;
		}

		DbTable dbTable = beanClass.getAnnotation(DbTable.class);
		if (dbTable == null) {
			return;
		}
		if (registerAsEntities) {
			dbEntityManager.registerEntity(beanClass);
		} else {
			dbEntityManager.registerType(beanClass);
		}
	}

}