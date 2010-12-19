// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.typeconverter.Convert;
import jodd.util.ClassLoaderUtil;
import jodd.util.PropertiesUtil;
import jodd.madvoc.component.MadvocController;
import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.config.MadvocConfigurator;
import jodd.madvoc.config.AutomagicMadvocConfigurator;

import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.servlet.ServletContext;

/**
 * Creates, initializes and starts {@link jodd.madvoc.WebApplication}.
 */
public class WebApplicationStarter {

	private static Logger log;

	// ---------------------------------------------------------------- params

	protected String webAppClass;
	protected String[] paramsFiles;
	protected String madvocConfigurator;

	public void setWebAppClass(String webAppClass) {
		this.webAppClass = webAppClass;
	}

	public void setParamsFiles(String[] paramsFiles) {
		this.paramsFiles = paramsFiles;
	}

	public void setMadvocConfigurator(String madvocConfigurator) {
		this.madvocConfigurator = madvocConfigurator;
	}

	// ---------------------------------------------------------------- start

	protected WebApplication webapp;
	protected MadvocController madvocController;
	protected MadvocConfig madvocConfig;

	/**
	 * Returns Madvoc controller once web application is started.
	 */
	public MadvocController getMadvocController() {
		return madvocController;
	}

	/**
	 * Returns Madvoc controller once web application is started.
	 */
	public MadvocConfig getMadvocConfig() {
		return madvocConfig;
	}

	/**
	 * Creates and starts web application and returns created instance.
	 */
	public WebApplication startNewWebApplication(ServletContext context) {
		webapp = createWebApplication(webAppClass);
		log = LoggerFactory.getLogger(WebApplicationStarter.class);
		if (webAppClass == null) {
			log.info("Default web application created.");
		} else {
			log.info("Created web application: {}", webAppClass);
		}


		// initialize web application
		webapp.initWebApplication();

		// params
		Properties params = loadMadvocParams(paramsFiles);
		webapp.defineParams(params);

		// configure
		webapp.registerMadvocComponents();
		madvocConfig = webapp.getComponent(MadvocConfig.class);
		if (madvocConfig == null) {
			throw new MadvocException("No Madvoc configuration component found.");
		}
		webapp.init(madvocConfig, context);

		// actions
		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);
		if (actionsManager == null) {
			throw new MadvocException("No Madvoc actions manager component found.");
		}
		webapp.initActions(actionsManager);
		// results
		ResultsManager resultsManager = webapp.getComponent(ResultsManager.class);
		if (resultsManager == null) {
			throw new MadvocException("No Madvoc results manager component found.");
		}
		webapp.initResults(resultsManager);

		// configure with external configurator
		MadvocConfigurator configurator = loadMadvocConfig(madvocConfigurator);
		webapp.configure(configurator);

		// prepare web application
		madvocController = webapp.getComponent(MadvocController.class);
		if (madvocController == null) {
			throw new MadvocException("No Madvoc controller component found.");
		}
		madvocController.init(context);
		return webapp;
	}


	// ---------------------------------------------------------------- loading configuration

	/**
	 * Loads {@link WebApplication}. If class name is <code>null</code>,
	 * default web application will be loaded.
	 */
	protected WebApplication createWebApplication(String webAppClassName) {
		if (webAppClass == null) {
			return new WebApplication();
		}
		WebApplication webApp;
		try {
			Class webAppClass = ClassLoaderUtil.loadClass(webAppClassName, this.getClass());
			webApp = (WebApplication) webAppClass.newInstance();
		} catch (ClassNotFoundException cnfex) {
			throw new MadvocException("Madvoc web application class not found: " + webAppClassName, cnfex);
		} catch (ClassCastException ccex) {
			throw new MadvocException("Class '" + webAppClass + "' is not a Madvoc web application.", ccex);
		} catch (Exception ex) {
			throw new MadvocException("Unable to load Madvoc web application class '" + webAppClass + "': " + ex.toString(), ex);
		}
		return webApp;
	}

	/**
	 * Loads Madvoc parameters.
	 */
	protected Properties loadMadvocParams(String[] patterns) {
		if (patterns == null) {
			return new Properties();
		}
		log.info("Loading Madvoc parameters from: {}", patterns);
		try {
			return PropertiesUtil.createFromClasspath(patterns);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MadvocException("Unable to load Madvoc parameters from: :" + Convert.toString(patterns) + ".properties': " + ex.toString(), ex);
		}
	}


	/**
	 * Loads {@link jodd.madvoc.config.MadvocConfigurator}. If class name is <code>null</code>,
	 * {@link jodd.madvoc.config.AutomagicMadvocConfigurator} will be created.
	 */
	protected MadvocConfigurator loadMadvocConfig(String className) {
		if (className == null) {
			log.info("Configuring Madvoc using default automagic configurator");
			return new AutomagicMadvocConfigurator();
		}
		log.info("Configuring Madvoc using configurator: {}", className);
		MadvocConfigurator configurator;
		try {
			Class clazz = ClassLoaderUtil.loadClass(className, this.getClass());
			configurator = (MadvocConfigurator) clazz.newInstance();
		} catch (ClassCastException ccex) {
			ccex.printStackTrace();
			throw new MadvocException("Class '" + className + "' is not a Madvoc configurator.", ccex);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MadvocException("Unable to load Madvoc configurator class '" + className + "': " + ex.toString(), ex);
		}
		return configurator;
	}

}
