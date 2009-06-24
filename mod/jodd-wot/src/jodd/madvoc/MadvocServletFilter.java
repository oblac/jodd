// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.config.AutomagicMadvocConfigurator;
import jodd.madvoc.config.MadvocConfigurator;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.component.MadvocController;
import jodd.util.ClassLoaderUtil;
import jodd.util.PropertiesUtil;
import jodd.servlet.DispatcherUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Initializes and configures Madvoc and passes requests to {@link jodd.madvoc.component.MadvocController}.
 */
public class MadvocServletFilter implements Filter {

	protected static final Logger log = LoggerFactory.getLogger(MadvocServletFilter.class);

	public static final String PARAM_MADVOC_WEBAPP = "madvoc.webapp";
	public static final String PARAM_MADVOC_CONFIGURATOR = "madvoc.configurator";
	public static final String PARAM_MADVOC_PARAMS = "madvoc.params";

	protected FilterConfig filterConfig;
	protected WebApplication webapp;
	protected MadvocConfig madvocConfig;
	protected MadvocController madvocController;

	/**
	 * Filter initialization.
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("Madvoc servlet filter initialization");
		this.filterConfig = filterConfig;
		webapp = loadWebApplication(filterConfig.getInitParameter(PARAM_MADVOC_WEBAPP));

		// params
		Properties params = loadMadvocParams(filterConfig.getInitParameter(PARAM_MADVOC_PARAMS));
		webapp.defineParams(params);
		
		// configure
		webapp.registerMadvocComponents();
		madvocConfig = webapp.getComponent(MadvocConfig.class);
		if (madvocConfig == null) {
			throw new MadvocException("No Madvoc configuration component found.");
		}
		webapp.init(madvocConfig, filterConfig.getServletContext());

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
		MadvocConfigurator configurator = loadMadvocConfig(filterConfig.getInitParameter(PARAM_MADVOC_CONFIGURATOR));
		webapp.configure(configurator);

		// prepare web application
		madvocController = webapp.getComponent(MadvocController.class);
		if (madvocController == null) {
			throw new MadvocException("No Madvoc controller component found.");
		}
		madvocController.init(filterConfig.getServletContext());
	}

	/**
	 * Filter destruction.
	 */
	public void destroy() {
		webapp.destroy(madvocConfig);
	}

	// ---------------------------------------------------------------- loading configuration

	/**
	 * Loads {@link WebApplication}. If class name is <code>null</code>,
	 * default web application will be loaded.
	 */
	protected WebApplication loadWebApplication(String className) throws ServletException {
		if (className == null) {
			log.info("Loading default web application");
			return new WebApplication();
		}
		log.info("Loading web application: {}", className);
		WebApplication webApp;
		try {
			Class clazz = ClassLoaderUtil.loadClass(className, this.getClass());
			webApp = (WebApplication) clazz.newInstance();
		} catch (ClassCastException ccex) {
			ccex.printStackTrace();
			throw new ServletException("Class '" + className + "' is not a Madvoc web application.", ccex);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ServletException("Unable to load Madvoc web application class '" + className + "': " + ex.toString(), ex);
		}
		return webApp;
	}

	/**
	 * Loads {@link jodd.madvoc.config.MadvocConfigurator}. If class name is <code>null</code>,
	 * {@link jodd.madvoc.config.AutomagicMadvocConfigurator} will be created.
	 */
	protected MadvocConfigurator loadMadvocConfig(String className) throws ServletException {
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
			throw new ServletException("Class '" + className + "' is not a Madvoc configurator.", ccex);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ServletException("Unable to load Madvoc configurator class '" + className + "': " + ex.toString(), ex);
		}
		return configurator;
	}

	/**
	 * Loads parameters from properties files.
	 */
	protected Properties loadMadvocParams(String pattern) throws ServletException {
		if (pattern == null) {
			return new Properties();
		}
		log.info("Loading Madvoc parameters from: {}", pattern);
		try {
			return PropertiesUtil.createFromClasspath(pattern);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ServletException("Unable to load Madvoc parameters from: :" + pattern + ".properties': " + ex.toString(), ex);
		}
	}

	// ---------------------------------------------------------------- do filter


	/**
	 * Builds {@link ActionRequest} and invokes it. If action result is a chain, it repeats the process.
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		String actionPath = DispatcherUtil.getServletPath(request);
		try {
			actionPath = madvocController.invoke(actionPath, request, response);
		} catch (ServletException sex) {
			throw sex;
		} catch (IOException ioex) {
			throw ioex;
		} catch (Exception ex) {
			throw new ServletException(ex);
		}
		if (actionPath != null) {	// action path is not consumed
			processUnhandledPath(actionPath, req, res,  chain);
		}
	}

	/**
	 * Process unconsumed action paths.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	protected void processUnhandledPath(String actionPath, ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(request, response);
	}

}
