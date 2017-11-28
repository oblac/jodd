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

package jodd.joy;

import jodd.db.DbDetector;
import jodd.db.DbSessionProvider;
import jodd.db.JoddDb;
import jodd.db.connection.ConnectionProvider;
import jodd.db.jtx.DbJtxSessionProvider;
import jodd.db.jtx.DbJtxTransactionManager;
import jodd.db.oom.DbEntityManager;
import jodd.db.oom.config.AutomagicDbOomConfigurator;
import jodd.db.pool.CoreConnectionPool;
import jodd.jtx.JoddJtx;
import jodd.jtx.JtxTransactionManager;
import jodd.jtx.proxy.AnnotationTxAdviceManager;
import jodd.jtx.proxy.AnnotationTxAdviceSupport;
import jodd.petite.PetiteContainer;
import jodd.util.Consumers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static jodd.joy.JoddJoy.PETITE_DB;
import static jodd.joy.JoddJoy.PETITE_DBPOOL;

public class JoyDb extends JoyBase {

	protected final Supplier<JoyScanner> scannerSupplier;
	protected final Supplier<PetiteContainer> petiteContainerSupplier;
	protected final Config config = new Config();

	protected ConnectionProvider connectionProvider;
	protected JtxTransactionManager jtxManager;
	protected String jtxScopePattern;

	public JoyDb(Supplier<PetiteContainer> petiteContainerSupplier, Supplier<JoyScanner> scannerSupplier) {
		this.scannerSupplier = scannerSupplier;
		this.petiteContainerSupplier = petiteContainerSupplier;
	}

	public Config config() {
		return config;
	}

	public class Config {
		private boolean useDatabase = true;
		private boolean autoConfiguration = true;
		private Supplier<ConnectionProvider> connectionProviderSupplier;
		private Consumers<DbEntityManager> dbEntityManagerConsumers = Consumers.empty();

		public Config disableDatabase() {
			useDatabase = false;
			return this;
		}

		public Config disableAutoConfiguration() {
			autoConfiguration = false;
			return this;
		}

		public Config withEntityManager(Consumer<DbEntityManager> dbEntityManagerConsumer) {
			dbEntityManagerConsumers.add(dbEntityManagerConsumer);
			return this;
		}

		public Config withConnectionProvider(Supplier<ConnectionProvider> connectionProviderSupplier) {
			this.connectionProviderSupplier = connectionProviderSupplier;
			return this;
		}
	}

	/**
	 * Initializes database. First, creates connection pool.
	 * and transaction manager. Then, Jodds DbEntityManager is
	 * configured. It is also configured automagically, by scanning
	 * the class path for entities.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void start() {
		initLogger();

		if (!config.useDatabase) {
			log.info("DB not used");
			return;
		}

		log.info("DB start ----------");

		// connection pool
		connectionProvider = createConnectionProviderIfNotSupplied();
		petiteContainerSupplier.get().addBean(PETITE_DBPOOL, connectionProvider);
		connectionProvider.init();

		checkConnectionProvider();

		// transactions manager
		jtxManager = createJtxTransactionManager(connectionProvider);
		jtxManager.setValidateExistingTransaction(true);

		AnnotationTxAdviceManager annTxAdviceManager = new AnnotationTxAdviceManager(jtxManager, jtxScopePattern);
		annTxAdviceManager.registerAnnotations(JoddJtx.get().defaults().getTxAnnotations());
		AnnotationTxAdviceSupport.manager = annTxAdviceManager;

		DbSessionProvider sessionProvider = new DbJtxSessionProvider(jtxManager);

		// global settings
		JoddDb.get().connectionProvider(connectionProvider);
		JoddDb.get().sessionProvider(sessionProvider);
		petiteContainerSupplier.get().addBean(PETITE_DB, JoddDb.get().defaults());           // todo -> this is for the configuration!, make this for each bean

		DbEntityManager dbEntityManager = JoddDb.get().dbEntityManager();
		dbEntityManager.reset();

		// automatic configuration
		if (config.autoConfiguration) {
			registerDbEntities(dbEntityManager);
		}

		DbDetector.detectDatabaseAndConfigureDbOom(connectionProvider);

		config.dbEntityManagerConsumers.accept(dbEntityManager);
	}

	/**
	 * Registers DbOom entities. By default, scans the
	 * class path and register entities automagically.
	 */
	protected void registerDbEntities(DbEntityManager dbEntityManager) {
		AutomagicDbOomConfigurator dbcfg = new AutomagicDbOomConfigurator();

		scannerSupplier.get().applyTo(dbcfg);

		log.info("*DB Automagic scanning");

		dbcfg.configure(dbEntityManager);
	}

	/**
	 * Creates JTX transaction manager.
	 */
	protected JtxTransactionManager createJtxTransactionManager(ConnectionProvider connectionProvider) {
		return new DbJtxTransactionManager(connectionProvider);
	}

	/**
	 * Returns <code>ConnectionProvider</code> instance.
	 * Instance will be registered into the Petite context.
	 */
	protected ConnectionProvider createConnectionProviderIfNotSupplied() {
		if (config.connectionProviderSupplier != null) {
			return config.connectionProviderSupplier.get();
		}
		return new CoreConnectionPool();
	}

	/**
	 * Checks if connection provider can return a connection.
	 */
	protected void checkConnectionProvider() {
		Connection connection = connectionProvider.getConnection();
		try {
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			String name = databaseMetaData.getDatabaseProductName();
			String version = databaseMetaData.getDatabaseProductVersion();

			if (log.isInfoEnabled()) {
				log.info("Connected to database: " + name + " v" + version);
			}
		} catch (SQLException sex) {
			log.error("DB connection failed: ", sex);
		} finally {
			connectionProvider.closeConnection(connection);
		}
	}

	@Override
	public void stop() {
		if (!config.useDatabase) {
			return;
		}

		if (log != null) {
			log.info("DB stop");
		}

		if (jtxManager != null) {
			jtxManager.close();
		}

		if (connectionProvider != null) {
			connectionProvider.close();
		}
	}
}
