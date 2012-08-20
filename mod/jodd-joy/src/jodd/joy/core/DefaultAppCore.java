// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.core;

import jodd.db.DbManager;
import jodd.db.DbSessionProvider;
import jodd.db.connection.ConnectionProvider;
import jodd.db.oom.DbOomManager;
import jodd.db.oom.config.AutomagicDbOomConfigurator;
import jodd.db.pool.CoreConnectionPool;
import jodd.joy.exception.AppException;
import jodd.joy.jtx.meta.ReadWriteTransaction;
import jodd.jtx.JtxTransactionManager;
import jodd.jtx.db.DbJtxSessionProvider;
import jodd.jtx.db.DbJtxTransactionManager;
import jodd.jtx.meta.Transaction;
import jodd.jtx.proxy.AnnotationTxAdvice;
import jodd.jtx.proxy.AnnotationTxAdviceManager;
import jodd.jtx.proxy.AnnotationTxAdviceSupport;
import jodd.log.Log;
import jodd.petite.PetiteContainer;
import jodd.petite.config.AutomagicPetiteConfigurator;
import jodd.petite.proxetta.ProxettaAwarePetiteContainer;
import jodd.petite.scope.SessionScope;
import jodd.petite.scope.SingletonScope;
import jodd.props.Props;
import jodd.props.PropsUtil;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.Proxetta;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.pointcuts.MethodAnnotationPointcut;
import jodd.util.ClassLoaderUtil;
import jodd.util.SystemUtil;

import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Default application core frame. Contains init point to
 * all application frameworks and layers.
 */
public abstract class DefaultAppCore {

	/**
	 * Application properties (from props config file).
	 */
	public static final String APP_DIR = "app.dir";
	public static final String APP_WEB = "app.web";

	/**
	 * Petite bean name for AppCore (this instance).
	 */
	public static final String PETITE_APPCORE = "app";
	/**
	 * Petite bean name for database pool.
	 */
	public static final String PETITE_DBPOOL = "dbpool";
	/**
	 * Petite bean name for DbManager instance.
	 */
	public static final String PETITE_DB = "db";
	/**
	 * Petite bean name for DbOomManager instance.
	 */
	public static final String PETITE_DBOOM = "dboom";
	/**
	 * Petite bean name for {@link AppInit} bean.
	 */
	public static final String PETITE_APPINIT = "appInit";

	/**
	 * Logger. Resolved during {@link #initLogger() initialization}.
	 */
	protected static Log log;

	/**
	 * App dir. Resolved during initialization.
	 */
	protected String appDir;

	/**
	 * Is web application. Resolved during initialization.
	 */
	protected boolean isWebApplication;

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
	 * Initializes system core, invoked very first!
	 * May also set the value of <code>appDir</code>.
	 * Important: logging is not yet available in this method!
	 */
	@SuppressWarnings("unchecked")
	public void init() {
		if (appPropsName == null) {
			appPropsName = "app.props";
		}

		if (appPropsNamePattern == null) {
			appPropsNamePattern = "/app*.prop*";
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
		log = Log.getLogger(DefaultAppCore.class);
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
			throw new AppException("Unable to resolve app dirs, missing: " + classPathFileName);
		}
		String protocol = url.getProtocol();


		if (protocol.equals("file") == false) {
			try {
				url = new URL(url.getFile());
			} catch (MalformedURLException ignore) {
			}
		}

		appDir = url.getFile();

		int ndx = appDir.indexOf("WEB-INF");
		isWebApplication = (ndx != -1);

		appDir = isWebApplication ? appDir.substring(0, ndx) : SystemUtil.getWorkingFolder();
	}

	// ---------------------------------------------------------------- ready

	/**
	 * Callback when core initialization is done.
	 */
	protected void ready() {
	}

	// ---------------------------------------------------------------- start

	/**
	 * Starts the application and performs all initialization.
	 */
	public void start() {

		init();
		initLogger();
		initProps();
		initScanning();

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
		log.info("shutting down...");

		stopApp();
		stopDb();

		log.info("app stopped");
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
	 * It first load system properties (registered as <code>sys.*</code>)
	 * and then environment properties (registered as <code>env.*</code>).
	 * Finally, props files are read from the classpath.
	 */
	protected void initProps() {
		if (appProps == null) {
			appProps = new Props();
		}

		appProps.loadSystemProperties("sys");
		appProps.loadEnvironment("env");

		PropsUtil.loadFromClasspath(appProps, appPropsNamePattern);
	}

	// ---------------------------------------------------------------- scanning

	protected AppScanner appScanner;

	/**
	 * Creates {@link AppScanner}.
	 */
	protected void initScanning() {
		appScanner = new AppScanner(this);
		appScanner.init();
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
	protected void startProxetta() {
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
	 * with {@link #jtxAnnotations registered JTX annotations}.
	 */
	protected ProxyAspect createTxProxyAspects() {
		return new ProxyAspect(
				AnnotationTxAdvice.class,
				new MethodAnnotationPointcut(jtxAnnotations) {
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
	protected void startPetite() {
		log.info("petite initialization");
		petite = createPetiteContainer();

		log.info("app in web: " + Boolean.valueOf(isWebApplication));
		if (isWebApplication == false) {
			// make session scope to act as singleton scope
			// if this is not a web application (and http session is not available).
			petite.registerScope(SessionScope.class, new SingletonScope());
		}

		// automagic configuration
		AutomagicPetiteConfigurator pcfg = new AutomagicPetiteConfigurator();
		appScanner.configure(pcfg);
		pcfg.configure(petite);

		// load parameters from properties files
		petite.defineParameters(appProps);

		// add AppCore instance to Petite
		petite.addBean(PETITE_APPCORE, this);
	}

	/**
	 * Creates Petite container. By default, it creates
	 * {@link jodd.petite.proxetta.ProxettaAwarePetiteContainer proxetta aware petite container}.
	 */
	protected PetiteContainer createPetiteContainer() {
		return new ProxettaAwarePetiteContainer(proxetta);
	}

	// ---------------------------------------------------------------- database

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
	 * JTX annotations.
	 */
	protected Class<? extends Annotation>[] jtxAnnotations;

	/**
	 * JTX scope pattern.
	 * @see {@link AnnotationTxAdviceManager}
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
		log.info("database initialization");

		// connection pool
		petite.registerBean(PETITE_DBPOOL, CoreConnectionPool.class);
		connectionProvider = (ConnectionProvider) petite.getBean(PETITE_DBPOOL);
		connectionProvider.init();

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
		DbOomManager.setInstance(dbOomManager);
		petite.addBean(PETITE_DBOOM, dbOomManager);

		// automatic configuration
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
	protected void startApp() {
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

}