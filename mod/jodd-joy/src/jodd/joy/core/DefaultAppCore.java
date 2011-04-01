// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.core;

import jodd.db.DbDefault;
import jodd.db.DbSessionProvider;
import jodd.db.connection.ConnectionProvider;
import jodd.db.orm.DbOrmManager;
import jodd.db.orm.config.AutomagicDbOrmConfigurator;
import jodd.db.pool.CoreConnectionPool;
import jodd.joy.AppUtil;
import jodd.joy.jtx.meta.ReadWriteTransaction;
import jodd.joy.petite.ProxettaAwarePetiteContainer;
import jodd.jtx.JtxTransactionManager;
import jodd.jtx.db.DbJtxSessionProvider;
import jodd.jtx.db.DbJtxTransactionManager;
import jodd.jtx.meta.Transaction;
import jodd.jtx.proxy.AnnotationTxAdvice;
import jodd.jtx.proxy.AnnotationTxAdviceManager;
import jodd.jtx.proxy.AnnotationTxAdviceSupport;
import jodd.petite.PetiteContainer;
import jodd.petite.config.AutomagicPetiteConfigurator;
import jodd.petite.scope.SessionScope;
import jodd.petite.scope.SingletonScope;
import jodd.props.Props;
import jodd.props.PropsUtil;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.Proxetta;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.pointcuts.MethodAnnotationPointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static jodd.joy.AppUtil.prepareAppLogDir;
import static jodd.joy.AppUtil.resolveAppDirs;

/**
 * Default application core frame.
 */
public abstract class DefaultAppCore {

	/**
	 * Petite bean names.
	 */
	public static final String PETITE_APPCORE = "app";		// AppCore
	public static final String PETITE_DBPOOL = "dbpool";	// database pool
	public static final String PETITE_DBORM = "dbOrm";		// DbOrm instance
	public static final String PETITE_APPINIT = "appInit";	// init bean

	/**
	 * Logger.
	 */
	protected static Logger log;

	/**
	 * Default scanning path that will be examined by various
	 * Jodd auto-magic tools.
	 */
	protected final String[] scanningPath;

	/**
	 * Default constructor.
	 */
	protected DefaultAppCore() {
		scanningPath = resolveScanningPath();
	}

	/**
	 * Defines the scanning path for Jodd frameworks.
	 * Scanning path will contain all classes inside and bellow
	 * the app core packages and 'jodd' packages.
	 */
	protected String[] resolveScanningPath() {
		return new String[] {this.getClass().getPackage().getName() + ".*", "jodd.*"};
	}

	// ---------------------------------------------------------------- start

	/**
	 * Returns <code>true</code> if application is started as a part of web application.
	 */
	public boolean isWebApplication() {
		return AppUtil.isWebApplication();
	}

	/**
	 * Starts the application and performs all initialization.
	 */
	public synchronized void start() {
		resolveAppDirs("app.props");			// app directories are resolved from location of 'app.props'.
		prepareAppLogDir("log");				// creates log folder, depending of application type
		initLogger();							// logger becomes available after this point
		log.info("app dir: {}", AppUtil.getAppDir());
		log.info("log dir: {}", AppUtil.getLogDir());
		log.info("classpath: {}", AppUtil.getClasspathDir());
		try {
			initTypes();
			initProxetta();
			initPetite();
			initDb();
			initApp();
			log.info("app started");
		} catch (RuntimeException rex) {
			log.error(rex.toString(), rex);
			try {
				stop();
			} catch (Exception ignore) {
			}
			throw rex;
		}
	}

	/**
	 * Stops the application.
	 */
	public synchronized void stop() {
		log.info("shutting down...");
		stopApp();
		stopDb();
		log.info("app stopped");
	}

	// ---------------------------------------------------------------- log

	/**
	 * Initializes the logger. It must be initialized after the
	 * log path is defined.
	 */
	protected void initLogger() {
		log = LoggerFactory.getLogger(DefaultAppCore.class);
	}

	
	// ---------------------------------------------------------------- proxetta

	protected Proxetta proxetta;

	/**
	 * Returns proxetta.
	 */
	public Proxetta getProxetta() {
		return proxetta;
	}

	/**
	 * Creates Proxetta with all aspects. The following aspects are created:
	 *
	 * <li>Transaction proxy - applied on all classes that contains public top-level methods
	 * annotated with <code>@Transaction</code> annotation. This is just one way how proxies
	 * can be applied - since base configuration is in Java, everything is possible.
	 */
	protected void initProxetta() {
		log.info("proxetta initialization");
		proxetta = Proxetta.withAspects(createAppAspects()).loadsWith(this.getClass().getClassLoader());
	}

	/**
	 * Creates all application aspects. By default it creates just
	 * {@link #createTxProxyAspects() transactional aspect}.
	 */
	protected ProxyAspect[] createAppAspects() {
		return new ProxyAspect[] {createTxProxyAspects()};
	}

	/**
	 * Creates TX aspect that will be applied on all classes
	 * having at least one public top-level method annotated
	 * with <code>@Transaction</code>.
	 */
	protected ProxyAspect createTxProxyAspects() {
		return new ProxyAspect(AnnotationTxAdvice.class,
				new MethodAnnotationPointcut(Transaction.class, ReadWriteTransaction.class) {
			@Override
			public boolean apply(MethodInfo methodInfo) {
				return
						isPublic(methodInfo) &&
						isTopLevelMethod(methodInfo) &&
						super.apply(methodInfo);
			}
		});
	}

	// ---------------------------------------------------------------- petite

	protected PetiteContainer petite;

	/**
	 * Returns application container (Petite).
	 */
	public PetiteContainer getPetite() {
		return petite;
	}

	/**
	 * Creates and initializes Petite container.
	 * It will be auto-magically configured by scanning the classpath.
	 * Also, all 'app*.prop*' will be loaded and values will
	 * be injected in the matched beans. At the end it registers
	 * this instance of core into the container.
	 */
	protected void initPetite() {
		log.info("petite initialization");
		petite = createPetiteContainer();
		boolean isWebApplication = AppUtil.isWebApplication();
		log.info("app in web: {}", Boolean.valueOf(isWebApplication));
		if (isWebApplication == false) {
			// make session scope to act as singleton scope
			// if this is not a web application (and http session is not available).
			petite.registerScope(SessionScope.class, new SingletonScope());
		}

		// automagic configuration
		AutomagicPetiteConfigurator pcfg = new AutomagicPetiteConfigurator();
		pcfg.setIncludedEntries(scanningPath);
		pcfg.configure(petite);

		// load parameters from properties files
		Props appProps = createPetiteProps();
		PropsUtil.loadFromClasspath(appProps, "/app*.prop*");
		petite.defineParameters(appProps);

		// add AppCore instance to Petite
		petite.addBean(PETITE_APPCORE, this);
	}

	/**
	 * Creates application {@link Props}. May be overridden to
	 * configure the props features.
	 */
	protected Props createPetiteProps() {
		return new Props();
	}

	/**
	 * Creates Petite container. By default, it creates
	 * {@link jodd.joy.petite.ProxettaAwarePetiteContainer proxetta aware petite container}.
	 */
	protected PetiteContainer createPetiteContainer() {
		return new ProxettaAwarePetiteContainer(proxetta);
	}

	// ---------------------------------------------------------------- database

	/**
	 * Database debug mode will print out sql statements.
	 */
	protected boolean debugMode;

	/**
	 * Returns <code>true</code> if debug mode is on.
	 */
	public boolean isDebugMode() {
		return debugMode;
	}

	/**
	 * JTX manager.
	 */
	protected JtxTransactionManager jtxManager;

	/**
	 * Returns JTX transaction manager.
	 */
	public JtxTransactionManager getJtxManager() {
		return jtxManager;
	}

	/**
	 * Database connection provider.
	 */
	protected ConnectionProvider connectionProvider;

	/**
	 * Returns connection provider.
	 */
	public ConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}

	/**
	 * Initializes database. First, creates connection pool.
	 * and transaction manager. Then, Jodds DbOrmManager is
	 * configured. It is also configured automagically, by scanning
	 * the class path for entities.
	 */
	protected void initDb() {
		log.info("database initialization");

		// connection pool
		petite.registerBean(PETITE_DBPOOL, CoreConnectionPool.class);
		connectionProvider = (ConnectionProvider) petite.getBean(PETITE_DBPOOL);
		connectionProvider.init();

		// transactions manager
		jtxManager = createJtxTransactionManager(connectionProvider);
		jtxManager.setValidateExistingTransaction(true);
		AnnotationTxAdviceManager annTxAdviceManager = new AnnotationTxAdviceManager(jtxManager, "$class");
		annTxAdviceManager.registerAnnotations(Transaction.class, ReadWriteTransaction.class);
		AnnotationTxAdviceSupport.manager = annTxAdviceManager;
		DbSessionProvider sessionProvider = new DbJtxSessionProvider(jtxManager);

		// global settings
		DbDefault.debug = debugMode;
		DbDefault.connectionProvider = connectionProvider;
		DbDefault.sessionProvider = sessionProvider;

		DbOrmManager dbOrmManager = createDbOrmManager();
		DbOrmManager.setInstance(dbOrmManager);
		petite.addBean(PETITE_DBORM, dbOrmManager);

		// automatic configuration
		AutomagicDbOrmConfigurator dbcfg = new AutomagicDbOrmConfigurator();
		dbcfg.setIncludedEntries(scanningPath);
		dbcfg.configure(dbOrmManager);
	}

	/**
	 * Creates JTX transaction manager.
	 */
	protected JtxTransactionManager createJtxTransactionManager(ConnectionProvider connectionProvider) {
		return new DbJtxTransactionManager(connectionProvider);
	}

	/**
	 * Creates DbOrmManager.
	 */
	protected DbOrmManager createDbOrmManager() {
		return DbOrmManager.getInstance();
	}

	/**
	 * Closes database resources at the end.
	 */
	protected void stopDb() {
		log.info("database shutdown");
		jtxManager.close();
		connectionProvider.close();
	}

	// ---------------------------------------------------------------- init

	protected AppInit appInit;

	/**
	 * Initializes business part of the application.
	 * Simply delegates to {@link AppInit#init()}.
	 */
	protected void initApp() {
		appInit = (AppInit) petite.getBean(PETITE_APPINIT);
		if (appInit != null) {
			appInit.init();
		}
	}

	/**
	 * Stops business part of the application.
	 * Simply delegates to {@link AppInit#stop()}.
	 */
	protected void stopApp() {
		if (appInit != null) {
			appInit.stop();
		}
	}

	// ---------------------------------------------------------------- new types

	/**
	 * Initializes types for beanutil and db conversions and other usages.
	 */
	protected void initTypes() {
	}

}
