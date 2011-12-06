// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.log.Log;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.MadvocController;
import jodd.servlet.DispatcherUtil;
import jodd.typeconverter.Convert;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Initializes and configures Madvoc and passes requests to {@link jodd.madvoc.component.MadvocController}.
 */
public class MadvocServletFilter implements Filter {

	private static Log log;

	/**
	 * Web application.
	 */
	public static final String PARAM_MADVOC_WEBAPP = "madvoc.webapp";
	/**
	 * Madvoc configurator.
	 */
	public static final String PARAM_MADVOC_CONFIGURATOR = "madvoc.configurator";
	/**
	 * List of Madvoc params and properties files to be found on classpath.
	 */
	public static final String PARAM_MADVOC_PARAMS = "madvoc.params";

	protected FilterConfig filterConfig;

	protected WebApplication webapp;
	protected MadvocConfig madvocConfig;
	protected MadvocController madvocController;


	/**
	 * Filter initialization.
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;

		WebApplicationStarter starter = createWebApplicationStarter(filterConfig);

		try {
			webapp = starter.startNewWebApplication(filterConfig.getServletContext());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ServletException("Unable to start Madvoc web application.", ex);
		}
		log = Log.getLogger(MadvocServletFilter.class);
		madvocController = starter.getMadvocController();
		madvocConfig = starter.getMadvocConfig();
		log.info("Madvoc application started.");
	}

	/**
	 * Creates {@link WebApplicationStarter web application starter} for this web application.
	 * Override it to set custom {@link MadvocConfig Madvoc configurator} or other core settings.
	 */
	protected WebApplicationStarter createWebApplicationStarter(FilterConfig filterConfig) {
		WebApplicationStarter starter = new WebApplicationStarter();
		starter.setWebAppClass(filterConfig.getInitParameter(PARAM_MADVOC_WEBAPP));
		starter.setParamsFiles(Convert.toStringArray(filterConfig.getInitParameter(PARAM_MADVOC_PARAMS)));
		starter.setMadvocConfigurator(filterConfig.getInitParameter(PARAM_MADVOC_CONFIGURATOR));
		return starter;
	}

	/**
	 * Filter destruction.
	 */
	public void destroy() {
		webapp.destroy(madvocConfig);
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
		} catch (Exception ex) {
			log.error("Exception while invoking action path: " + actionPath, ex);
			ex.printStackTrace();
			throw new ServletException(ex);
		}
		if (actionPath != null) {	// action path is not consumed
			actionPath = processUnhandledPath(actionPath, req, res);
			if (actionPath != null) {
				chain.doFilter(request, response);
			}
		}
	}

	/**
	 * Process unconsumed action paths. Returns <code>null</code> if action path is consumed, otherwise
	 * it returns action path to be consumed by filter chain.
	 * By default it just returns action path.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	protected String processUnhandledPath(String actionPath, ServletRequest request, ServletResponse response) throws IOException, ServletException {
		return actionPath;
	}

}
