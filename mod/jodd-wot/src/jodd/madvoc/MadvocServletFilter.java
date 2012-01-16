// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.log.Log;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.MadvocController;
import jodd.servlet.DispatcherUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <code>Madvoc</code> filter serves as a {@link jodd.madvoc.component.MadvocController controller} part
 * of the Madvoc framework. If {@link Madvoc} @{link WebApplication} is not already created,
 * this filter will initialize and configure the Madvoc using filter init parameters.
 */
public class MadvocServletFilter implements Filter {

	private static Log log;

	protected Madvoc madvoc;
	protected MadvocController madvocController;

	/**
	 * Filter initialization.
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext servletContext = filterConfig.getServletContext();

		madvoc = Madvoc.get(servletContext);
		if (madvoc == null) {
			madvoc = createMadvoc(filterConfig);

			try {
				madvoc.startNewWebApplication(servletContext);
			} catch (Exception ex) {
				throw new ServletException("Unable to start Madvoc web application.", ex);
			}
		}

		log = Log.getLogger(MadvocServletFilter.class);

		madvocController = madvoc.getMadvocController();
	}

	/**
	 * Creates {@link Madvoc Madvoc web application} if not already created.
	 * Override it to set custom {@link MadvocConfig Madvoc configurator} or other core settings.
	 */
	protected Madvoc createMadvoc(FilterConfig filterConfig) {
		Madvoc madvoc = new Madvoc();
		madvoc.configure(filterConfig);
		return madvoc;
	}

	/**
	 * Filter destruction.
	 */
	public void destroy() {
		madvoc.stopWebApplication();
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
	 * Process unconsumed action paths. Returns <code>null</code> if action path is consumed,
	 * otherwise returns action path to be consumed by filter chain.
	 * By default it just returns action path.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	protected String processUnhandledPath(String actionPath, ServletRequest request, ServletResponse response) throws IOException, ServletException {
		return actionPath;
	}

}