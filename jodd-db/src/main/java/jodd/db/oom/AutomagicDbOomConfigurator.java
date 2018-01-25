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

package jodd.db.oom;

import jodd.db.oom.meta.DbTable;
import jodd.io.findfile.ClassScanner;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import java.util.function.Consumer;

/**
 * Auto-magically scans classpath for domain objects annotated with DbOom annotations.
 */
public class AutomagicDbOomConfigurator {

	private static final Logger log = LoggerFactory.getLogger(AutomagicDbOomConfigurator.class);
	private final ClassScanner classScanner = new ClassScanner();

	protected final byte[] dbTableAnnotationBytes;
	protected final boolean registerAsEntities;

	public AutomagicDbOomConfigurator(final boolean registerAsEntities) {
		dbTableAnnotationBytes = ClassScanner.bytecodeSignatureOfType(DbTable.class);
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

	public AutomagicDbOomConfigurator withScanner(final Consumer<ClassScanner> scannerConsumer) {
		scannerConsumer.accept(classScanner);
		return this;
	}

	/**
	 * Configures {@link DbEntityManager} with specified class path.
	 * @see AutomagicDbOomConfigurator#configure(DbEntityManager)
	 */
	public void configure(final DbEntityManager dbEntityManager) {
		this.dbEntityManager = dbEntityManager;

		classScanner.smartModeEntries();
		classScanner.onEntry(ENTRY_CONSUMER);
		classScanner.scanDefaultClasspath();

		elapsed = System.currentTimeMillis();
		try {
			classScanner.start();
		} catch (Exception ex) {
			throw new DbOomException("Scan classpath error", ex);
		}
		elapsed = System.currentTimeMillis() - elapsed;
		if (log.isInfoEnabled()) {
			log.info("DbEntityManager configured in " + elapsed + "ms. Total entities: " + dbEntityManager.getTotalNames());
		}
	}

	/**
	 * Scans all classes and registers only those annotated with {@link DbTable}.
	 * Because of performance purposes, classes are not dynamically loaded; instead, their
	 * file content is examined.
	 */
	private Consumer<ClassScanner.EntryData> ENTRY_CONSUMER = new Consumer<ClassScanner.EntryData>() {
	@Override
	public void accept(final ClassScanner.EntryData entryData) {
		String entryName = entryData.name();
		if (!entryData.isTypeSignatureInUse(dbTableAnnotationBytes)) {
			return;
		}

		Class<?> beanClass;
		try {
			beanClass = classScanner.loadClass(entryName);
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
	}};

}