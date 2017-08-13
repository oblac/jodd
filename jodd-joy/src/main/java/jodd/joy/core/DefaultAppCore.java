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

package jodd.joy.core;

import jodd.db.DbManager;
import jodd.db.DbSessionProvider;
import jodd.db.connection.ConnectionProvider;
import jodd.db.oom.DbOomManager;
import jodd.db.oom.config.AutomagicDbOomConfigurator;
import jodd.db.pool.CoreConnectionPool;
import jodd.joy.exception.AppException;
import jodd.jtx.meta.ReadWriteTransaction;
import jodd.jtx.JtxTransactionManager;
import jodd.db.jtx.DbJtxSessionProvider;
import jodd.db.jtx.DbJtxTransactionManager;
import jodd.jtx.meta.Transaction;
import jodd.jtx.proxy.AnnotationTxAdvice;
import jodd.jtx.proxy.AnnotationTxAdviceManager;
import jodd.jtx.proxy.AnnotationTxAdviceSupport;
import jodd.petite.PetiteContainer;
import jodd.petite.config.AutomagicPetiteConfigurator;
import jodd.petite.proxetta.ProxettaAwarePetiteContainer;
import jodd.petite.scope.SessionScope;
import jodd.petite.scope.SingletonScope;
import jodd.props.Props;
import jodd.props.PropsUtil;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.pointcuts.MethodAnnotationPointcut;
import jodd.util.ClassLoaderUtil;
import jodd.util.SystemUtil;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Default application core. Contains init points to
 * all application frameworks and layers.
 */
public abstract class DefaultAppCore {

	/**
	 * Application system property - application folder.
	 */
	public static final String APP_DIR = "app.dir";
	/**
	 * Application system property - flag if web application is detected..
	 */
	public static final String APP_WEB = "app.web";

	/**
	 * Petite bean name for AppCore (this instance).
	 */
	public static final String PETITE_CORE = "core";
	/**
	 * Petite bean name for database pool.
	 */
	public static final String PETITE_DBPOOL = "dbpool";
	/**
	 * Petite bean name for <code>DbManager</code> instance.
	 */
	public static final String PETITE_DB = "db";
	/**
	 * Petite bean name for <code>DbOomManager</code> instance.
	 */
	public static final String PETITE_DBOOM = "dboom";
	/**
	 * Petite bean name for {@link AppInit} bean.
	 */
	public static final String PETITE_INIT = "init";
	/**
	 * Petite bean name for application props.
	 */
	public static final String PETITE_PROPS = "props";
	/**
	 * Petite bean name for {@link AppScanner} bean.
	 */
	public static final String PETITE_SCAN = "scan";


	/**
	 * Logger. Resolved during {@link #initLogger() initialization}.
	 */
	protected static Logger log;

	/**
	 * App dir. Resolved during initialization.
	 */
	protected String appDir;

	/**
	 * Is web application. Resolved during initialization.
	 */
	protected boolean isWebApplication;

	/**
	 * Props profiles. If set, overrides any default profile set
	 * in the props files.
	 */
	protected String[] appPropsProfiles;

	/**
	 * Default constructor.
	 */
	protected DefaultAppCore() {
	}

	// ---------------------------------------------------------------- init
	
	/**
	 * Returns <code>true</code> if application is started as a part of web application.
	 */
	public boolean isWebApplication() {
		return isWebApplication;
	}

	/**
	 * Returns application directory.
	 */
	public String getAppDir() {
		return appDir;
	}

	/**
	 * Initializes application core, invoked very first!
	 * Important: logging is not yet available in this method!
	 */
	@SuppressWarnings("unchecked")
	public void initCore() {
		if (appPropsName == null) {
			appPropsName = "app.props";
		}

		if (appPropsNamePattern == null) {
			int index = appPropsName.lastIndexOf('.');

			appPropsNamePattern = '/' + appPropsName.substring(0, index) + "*.prop*";
		}

		if (jtxAnnotations == null) {
			jtxAnnotations = new Class[] {Transaction.class, ReadWriteTransaction.class};
		}

		if (jtxScopePattern == null) {
			jtxScopePattern = "$class";
		}

		if (appDir == null) {
			resolveAppDir(appPropsName);		// app directory is resolved from location of 'app.props'.
		}

		System.setProperty(APP_DIR, appDir);
		System.setProperty(APP_WEB, Boolean.toString(isWebApplication));
	}

	/**
	 * Initializes the logger, after the log path is {@link #init() defined}.
	 */
	protected void initLogger() {
		if (log != null) {
			return;
		}

		log = LoggerFactory.getLogger(DefaultAppCore.class);
		log.info("app dir: " + appDir);
	}

	/**
	 * Resolves application root folders.
	 * <p>
	 * If application is started as web application, app folder is one below the WEB-INF folder.
	 * Otherwise, the root folder is equal to the working folder.
	 */
	protected void resolveAppDir(String classPathFileName) {
		URL url = ClassLoaderUtil.getResourceUrl(classPathFileName);
		if (url == null) {
			throw new AppException("Failed to resolve app dir, missing: " + classPathFileName);
		}
		String protocol = url.getProtocol();


		if (!protocol.equals("file")) {
			try {
				url = new URL(url.getFile());
			} catch (MalformedURLException ignore) {
			}
		}

		appDir = url.getFile();

		int ndx = appDir.indexOf("WEB-INF");
		isWebApplication = (ndx != -1);

		appDir = isWebApplication ? appDir.substring(0, ndx) : SystemUtil.workingFolder();
	}

	// ---------------------------------------------------------------- ready

	/**
	 * Called after the {@link #init() core initialization},
	 * during the {@link #start() application startup}.
	 */
	protected void ready() {
	}

	// ---------------------------------------------------------------- start

	protected boolean initialized;

	/**
	 * Initializes application.
	 * May be called several times, but the core
	 * will be initialized just once.
	 * Usually called manually when core needs to
	 * be created before server is started
	 * (e.g. in embedded environments)
	 */
	public void init() {
		if (initialized) {
			return;
		}

		initCore();
		initLogger();
		initProps();
		initScanner();

		initialized = true;
	}


	/**
	 * Starts the application and performs all initialization.
	 */
	public void start() {
		init();

		ready();

		try {
			startProxetta();
			startPetite();
			startDb();
			startApp();

			log.info("app started");
		} catch (RuntimeException rex) {
			if (log != null) {
				log.error(rex.toString(), rex);
			} else {
				System.out.println(rex.toString());
				rex.printStackTrace();
			}
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
	public void stop() {
		if (log != null) {
			log.info("shutting down...");
		}

		stopApp();
		stopDb();
		stopPetite();

		if (log != null) {
			log.info("app stopped");
		}
	}

	// ---------------------------------------------------------------- props

	/**
	 * Main application props file name, must exist in class path.
	 */
	protected String appPropsName;

	/**
	 * Application props file name pattern.
	 */
	protected String appPropsNamePattern;

	/**
	 * Application props.
	 */
	protected Props appProps;

	/**
	 * Returns applications properties loaded from props files.
	 */
	public Props getAppProps() {
		return appProps;
	}

	/**
	 * Creates and loads application props.
	 * It first loads system properties (registered as <code>sys.*</code>)
	 * and then environment properties (registered as <code>env.*</code>).
	 * Finally, props files are read from the classpath. All properties
	 * are loaded using
	 * <p>
	 * If props have been already loaded, does nothing.
	 */
	protected void initProps() {
		if (appProps != null) {
			return;
		}

		appProps = createProps();

		appProps.loadSystemProperties("sys");
		appProps.loadEnvironment("env");

		PropsUtil.loadFromClasspath(appProps, appPropsNamePattern);

		if (appPropsProfiles != null) {
			appProps.setActiveProfiles(appPropsProfiles);
		}
	}

	/**
	 * Creates new Props. Empty props will be ignored,
	 * and missing macros will be resolved as empty string.
	 */
	protected Props createProps() {
		Props props = new Props();
		props.setSkipEmptyProps(true);
		props.setIgnoreMissingMacros(true);
		return props;
	}

	// ---------------------------------------------------------------- scanning

	protected AppScanner appScanner;

	/**
	 * Returns scanner.
	 */
	public AppScanner getAppScanner() {
		return appScanner;
	}

	/**
	 * Initializes {@link AppScanner}.
	 */
	protected void initScanner() {
		if (appScanner != null) {
			return;
		}

		appScanner = new AppScanner(this);
	}

	// ---------------------------------------------------------------- proxetta

	protected ProxyProxetta proxetta;

	/**
	 * Returns proxetta.
	 */
	public ProxyProxetta getProxetta() {
		return proxetta;
	}

	/**
	 * Creates Proxetta with all aspects. The following aspects are created:
	 * <ul>
	 * <li>Transaction proxy - applied on all classes that contains public top-level methods
	 * annotated with <code>@Transaction</code> annotation. This is just one way how proxies
	 * can be applied - since base configuration is in Java, everything is possible.</li>
	 * </ul>
	 */
	protected void startProxetta() {
		log.info("proxetta initialization");
		proxetta = ProxyProxetta.withAspects(createAppAspects());
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
	 * with {@link #jtxAnnotations registered JTX annotations}.
	 */
	protected ProxyAspect createTxProxyAspects() {
		return new ProxyAspect(
				AnnotationTxAdvice.class,
				new MethodAnnotationPointcut(jtxAnnotations) {
			@Override
			public boolean apply(MethodInfo methodInfo) {
				return
						methodInfo.isPublicMethod() &&
						methodInfo.isTopLevelMethod() &&
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
	protected void startPetite() {
		log.info("petite initialization");
		petite = createPetiteContainer();

		log.info("app in web: " + Boolean.valueOf(isWebApplication));
		if (!isWebApplication) {
			// make session scope to act as singleton scope
			// if this is not a web application (and http session is not available).
			petite.registerScope(SessionScope.class, new SingletonScope());
		}

		// load parameters from properties files
		petite.defineParameters(appProps);

		// adds a scanner bean, so it can be immediately configured from props
		petite.addBean(PETITE_SCAN, appScanner);

		// automagic configuration
		registerPetiteContainerBeans(petite);

		// add AppCore instance to Petite
		petite.addBean(PETITE_CORE, this);

		petite.addBean(PETITE_PROPS, appProps);
	}

	/**
	 * Configures Petite container. By default scans the class path
	 * for petite beans and registers them automagically.
	 */
	protected void registerPetiteContainerBeans(PetiteContainer petiteContainer) {
		AutomagicPetiteConfigurator pcfg = new AutomagicPetiteConfigurator();
		appScanner.configure(pcfg);
		pcfg.configure(petiteContainer);
	}

	/**
	 * Creates Petite container. By default, it creates
	 * {@link jodd.petite.proxetta.ProxettaAwarePetiteContainer proxetta aware petite container}.
	 */
	protected PetiteContainer createPetiteContainer() {
		return new ProxettaAwarePetiteContainer(proxetta);
	}

	/**
	 * Stops Petite container.
	 */
	protected void stopPetite() {
		if (petite != null) {
			petite.shutdown();
		}
	}

	// ---------------------------------------------------------------- database

	protected boolean useDatabase = true;

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
	 * JTX annotations.
	 */
	protected Class<? extends Annotation>[] jtxAnnotations;

	/**
	 * JTX scope pattern.
	 * @see AnnotationTxAdviceManager
	 */
	protected String jtxScopePattern;

	/**
	 * Initializes database. First, creates connection pool.
	 * and transaction manager. Then, Jodds DbOomManager is
	 * configured. It is also configured automagically, by scanning
	 * the class path for entities.
	 */
	@SuppressWarnings("unchecked")
	protected void startDb() {
		if (!useDatabase) {
			log.info("database is not used");
			return;
		}

		log.info("database initialization");

		// connection pool
		connectionProvider = createConnectionProvider();
		petite.addBean(PETITE_DBPOOL, connectionProvider);
		connectionProvider.init();

		checkConnectionProvider();

		// transactions manager
		jtxManager = createJtxTransactionManager(connectionProvider);
		jtxManager.setValidateExistingTransaction(true);

		AnnotationTxAdviceManager annTxAdviceManager = new AnnotationTxAdviceManager(jtxManager, jtxScopePattern);
		annTxAdviceManager.registerAnnotations(jtxAnnotations);
		AnnotationTxAdviceSupport.manager = annTxAdviceManager;

		DbSessionProvider sessionProvider = new DbJtxSessionProvider(jtxManager);

		// global settings
		DbManager dbManager = DbManager.getInstance();
		dbManager.setConnectionProvider(connectionProvider);
		dbManager.setSessionProvider(sessionProvider);
		petite.addBean(PETITE_DB, dbManager);

		DbOomManager dbOomManager = DbOomManager.getInstance();
		petite.addBean(PETITE_DBOOM, dbOomManager);

		// automatic configuration
		registerDbEntities(dbOomManager);
	}

	/**
	 * Registers DbOom entities. By default, scans the
	 * class path and register entities automagically.
	 */
	protected void registerDbEntities(DbOomManager dbOomManager) {
		AutomagicDbOomConfigurator dbcfg = new AutomagicDbOomConfigurator();
		appScanner.configure(dbcfg);
		dbcfg.configure(dbOomManager);
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
	protected ConnectionProvider createConnectionProvider() {
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

	/**
	 * Closes database resources at the end.
	 */
	protected void stopDb() {
		if (!useDatabase) {
			return;
		}

		if (log != null) {
			log.info("database shutdown");
		}

		if (jtxManager != null) {
			jtxManager.close();
		}

		if (connectionProvider != null) {
			connectionProvider.close();
		}
	}

	// ---------------------------------------------------------------- init

	protected AppInit appInit;

	/**
	 * Initializes business part of the application.
	 * Simply delegates to {@link AppInit#init()}.
	 */
	protected void startApp() {
		appInit = petite.getBean(PETITE_INIT);
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

}