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

package jodd.madvoc;

import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.MadvocController;
import jodd.servlet.DispatcherUtil;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

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

	private static Logger log;

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
				throw new ServletException("Madvoc web application error", ex);
			}
		}

		log = LoggerFactory.getLogger(MadvocServletFilter.class);

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
			MadvocResponseWrapper madvocResponse = new MadvocResponseWrapper(response);

			actionPath = madvocController.invoke(actionPath, request, madvocResponse);
		} catch (Exception ex) {
			log.error("Invoking action path failed: " + actionPath, ex);

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