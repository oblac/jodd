// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.config.AutomagicMadvocConfigurator;
import jodd.madvoc.config.MadvocConfigurator;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.component.MadvocController;
import jodd.util.ClassLoaderUtil;
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

/**
 * Initializes and configures Madvoc and passes requests to {@link jodd.madvoc.component.MadvocController}.
 */
public class MadvocServletFilter implements Filter {

	protected FilterConfig filterConfig;
	protected WebApplication webapp;
	protected MadvocConfig madvocConfig;
	protected MadvocController madvocController;

	/**
	 * Filter initialization.
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		webapp = loadWebApplication(filterConfig.getInitParameter("madvoc.webapp"));

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
		MadvocConfigurator configurator = loadMadvocConfig(filterConfig.getInitParameter("madvoc.configurator"));
		webapp.configure(configurator);

		// prepare web application
		madvocController = webapp.getComponent(MadvocController.class);
		if (madvocController == null) {
			throw new MadvocException("No Madvoc controller component found.");
		}
		webapp.getComponent(MadvocController.class);
	}

	/**
	 * Filter destruction.
	 */
	public void destroy() {
		webapp.destroy(madvocConfig);
	}

	// ---------------------------------------------------------------- loading config

	/**
	 * Loads {@link WebApplication}. If class name is <code>null</code>,
	 * default web application will be loaded.
	 */
	protected WebApplication loadWebApplication(String className) throws ServletException {
		if (className == null) {
			return new WebApplication();
		}

		WebApplication webApp;
		try {
			Class clazz = ClassLoaderUtil.loadClass(className, this.getClass());
			webApp = (WebApplication) clazz.newInstance();
		} catch (ClassCastException ccex) {
			ccex.printStackTrace();
			throw new ServletException("Class '" + className + "' is not a Madvoc web application.");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ServletException("Unable to load Madvoc web application class '" + className + "': " + ex.toString());
		}
		return webApp;
	}

	/**
	 * Loads {@link jodd.madvoc.config.MadvocConfigurator}. If class name is <code>null</code>,
	 * {@link jodd.madvoc.config.AutomagicMadvocConfigurator} will be created.
	 */
	protected MadvocConfigurator loadMadvocConfig(String className) throws ServletException {
		if (className == null) {
			return new AutomagicMadvocConfigurator();
		}
		MadvocConfigurator configurator;
		try {
			Class clazz = ClassLoaderUtil.loadClass(className, this.getClass());
			configurator = (MadvocConfigurator) clazz.newInstance();
		} catch (ClassCastException ccex) {
			ccex.printStackTrace();
			throw new ServletException("Class '" + className + "' is not a Madvoc configurator.");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ServletException("Unable to load Madvoc configurator class '" + className + "': " + ex.toString());
		}
		return configurator;
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
