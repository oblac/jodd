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

import jodd.chalk.Chalk256;
import jodd.db.DbOom;
import jodd.db.DbSessionProvider;
import jodd.db.connection.ConnectionProvider;
import jodd.db.jtx.DbJtxSessionProvider;
import jodd.db.jtx.DbJtxTransactionManager;
import jodd.db.oom.AutomagicDbOomConfigurator;
import jodd.db.oom.DbEntityDescriptor;
import jodd.db.oom.DbEntityManager;
import jodd.db.pool.CoreConnectionPool;
import jodd.db.querymap.DbPropsQueryMap;
import jodd.db.querymap.QueryMap;
import jodd.jtx.JtxTransactionManager;
import jodd.jtx.proxy.AnnotationTxAdvice;
import jodd.jtx.proxy.AnnotationTxAdviceManager;
import jodd.jtx.proxy.AnnotationTxAdviceSupport;
import jodd.jtx.worker.LeanJtxWorker;
import jodd.petite.PetiteContainer;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.ProxyPointcut;
import jodd.proxetta.pointcuts.MethodWithAnnotationPointcut;
import jodd.util.ClassUtil;
import jodd.util.function.Consumers;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Tiny JoyDb kickstarter.
 */
public class JoyDb extends JoyBase implements JoyDbConfig {

	protected final Supplier<String> appNameSupplier;
	protected final Supplier<JoyScanner> joyScannerSupplier;
	protected final Supplier<JoyProxetta> joyProxettaSupplier;
	protected final Supplier<JoyPetite> joyPetiteSupplier;

	protected DbOom dbOom;
	protected ConnectionProvider connectionProvider;
	protected JtxTransactionManager jtxManager;
	protected String jtxScopePattern;

	public JoyDb(
			final Supplier<String> appNameSupplier,
			final Supplier<JoyPetite> joyPetiteSupplier,
			final Supplier<JoyProxetta> joyProxettaSupplier,
			final Supplier<JoyScanner> joyScannerSupplier) {
		this.appNameSupplier = appNameSupplier;
		this.joyPetiteSupplier = joyPetiteSupplier;
		this.joyScannerSupplier = joyScannerSupplier;
		this.joyProxettaSupplier = joyProxettaSupplier;
	}

	// ---------------------------------------------------------------- runtime

	/**
	 * Returns connection provider once when component is started.
	 */
	public ConnectionProvider getConnectionProvider() {
		return requireStarted(connectionProvider);
	}

	/**
	 * Returns JTX transaction manager once when component is started.
	 */
	public JtxTransactionManager getJtxManager() {
		return requireStarted(jtxManager);
	}

	/**
	 * Returns {@code true} if database usage is enabled.
	 */
	public boolean isDatabaseEnabled() {
		return databaseEnabled;
	}

	// ---------------------------------------------------------------- config

	private boolean databaseEnabled = true;
	private boolean autoConfiguration = true;
	private Supplier<ConnectionProvider> connectionProviderSupplier;
	private Consumers<DbEntityManager> dbEntityManagerConsumers = Consumers.empty();

	@Override
	public JoyDb disableDatabase() {
		requireNotStarted(connectionProvider);
		databaseEnabled = false;
		return this;
	}

	@Override
	public JoyDb disableAutoConfiguration() {
		requireNotStarted(connectionProvider);
		autoConfiguration = false;
		return this;
	}

	@Override
	public JoyDb withEntityManager(final Consumer<DbEntityManager> dbEntityManagerConsumer) {
		requireNotStarted(connectionProvider);
		dbEntityManagerConsumers.add(dbEntityManagerConsumer);
		return this;
	}

	@Override
	public JoyDb withConnectionProvider(final Supplier<ConnectionProvider> connectionProviderSupplier) {
		requireNotStarted(connectionProvider);
		this.connectionProviderSupplier = connectionProviderSupplier;
		return this;
	}

	// ---------------------------------------------------------------- lifecycle

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

		if (!databaseEnabled) {
			log.info("DB not enabled.");
			return;
		}

		log.info("DB start ----------");

		final PetiteContainer petiteContainer = joyPetiteSupplier.get().getPetiteContainer();

		// connection pool
		connectionProvider = createConnectionProviderIfNotSupplied();

		petiteContainer.addBean(beanNamePrefix() + "pool", connectionProvider);

		if (connectionProvider instanceof CoreConnectionPool) {
			final CoreConnectionPool pool = (CoreConnectionPool) connectionProvider;
			if (pool.getDriver() == null) {
				databaseEnabled = false;
				log.warn("DB configuration not set (" + beanNamePrefix() + "pool.*). DB will be disabled.");
				return;
			}
		}
		connectionProvider.init();

		checkConnectionProvider();

		// transactions manager
		jtxManager = createJtxTransactionManager(connectionProvider);
		jtxManager.setValidateExistingTransaction(true);

		final AnnotationTxAdviceManager annTxAdviceManager = new AnnotationTxAdviceManager(new LeanJtxWorker(jtxManager), jtxScopePattern);
		AnnotationTxAdviceSupport.manager = annTxAdviceManager;

		// create proxy
		joyProxettaSupplier.get().getProxetta().withAspect(createTxProxyAspects(annTxAdviceManager.getAnnotations()));

		final DbSessionProvider sessionProvider = new DbJtxSessionProvider(jtxManager);

		// querymap
		final long startTime = System.currentTimeMillis();

		final QueryMap queryMap = new DbPropsQueryMap();

		log.debug("Queries loaded in " + (System.currentTimeMillis() - startTime) + "ms.");
		log.debug("Total queries: " + queryMap.size());

		// dboom
		dbOom = DbOom.create()
			.withConnectionProvider(connectionProvider)
			.withSessionProvider(sessionProvider)
			.withQueryMap(queryMap)
			.get();

		dbOom.connect();

		final DbEntityManager dbEntityManager = dbOom.entityManager();
		dbEntityManager.reset();

		petiteContainer.addBean(beanNamePrefix() + "query", dbOom.queryConfig());
		petiteContainer.addBean(beanNamePrefix() + "oom", dbOom.config());

		// automatic configuration
		if (autoConfiguration) {
			final AutomagicDbOomConfigurator automagicDbOomConfigurator =
				new AutomagicDbOomConfigurator(dbEntityManager, true);

			automagicDbOomConfigurator.registerAsConsumer(
				joyScannerSupplier.get().getClassScanner());
		}

		dbEntityManagerConsumers.accept(dbEntityManager);

		log.info("DB OK!");
	}

	/**
	 * Creates JTX transaction manager.
	 */
	protected JtxTransactionManager createJtxTransactionManager(final ConnectionProvider connectionProvider) {
		return new DbJtxTransactionManager(connectionProvider);
	}

	/**
	 * Returns <code>ConnectionProvider</code> instance.
	 * Instance will be registered into the Petite context.
	 */
	protected ConnectionProvider createConnectionProviderIfNotSupplied() {
		if (connectionProviderSupplier != null) {
			return connectionProviderSupplier.get();
		}
		return new CoreConnectionPool();
	}

	/**
	 * Checks if connection provider can return a connection.
	 */
	protected void checkConnectionProvider() {
		final Connection connection = connectionProvider.getConnection();
		try {
			final DatabaseMetaData databaseMetaData = connection.getMetaData();
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

	protected ProxyAspect createTxProxyAspects(final Class<? extends Annotation>[] annotations) {
		return new ProxyAspect(
			AnnotationTxAdvice.class,
			((ProxyPointcut) MethodInfo::isPublicMethod)
				.and(MethodWithAnnotationPointcut.of(annotations))
		);
	}


	@Override
	public void stop() {
		if (!databaseEnabled) {
			return;
		}

		if (log != null) {
			log.info("DB stop");
		}

		if (jtxManager != null) {
			jtxManager.close();
		}
		jtxManager = null;

		if (connectionProvider != null) {
			connectionProvider.close();
		}
		connectionProvider = null;

		if (dbOom != null) {
			dbOom.shutdown();
		}
		dbOom = null;
	}

	protected String beanNamePrefix() {
		final String appName = appNameSupplier.get();
		return appName + ".db.";
	}

	// ---------------------------------------------------------------- print

	public void printEntities(final int width) {
		if (!databaseEnabled) {
			return;
		}

		final List<DbEntityDescriptor> list = new ArrayList<>();
		dbOom.entityManager().forEachEntity(list::add);

		if (list.isEmpty()) {
			return;
		}

		final Print print = new Print();
		print.line("Entities", width);

		list.stream()
			.sorted(Comparator.comparing(DbEntityDescriptor::getEntityName))
			.forEach(ded -> print.outLeftRightNewLine(
				Chalk256.chalk().yellow(), ded.getTableName(),
				Chalk256.chalk().blue(),   ClassUtil.getShortClassName(ded.getType(), 2),
				width));

		print.line(width);
	}
}
