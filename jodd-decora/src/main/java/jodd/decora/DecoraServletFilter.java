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

package jodd.decora;

import jodd.decora.parser.DecoraParser;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.servlet.DispatcherUtil;
import jodd.servlet.wrapper.BufferResponseWrapper;
import jodd.servlet.wrapper.LastModifiedData;
import jodd.util.ClassLoaderUtil;
import jodd.util.ClassUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.CharBuffer;

/**
 * Decora main filter.
 */
public class DecoraServletFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(DecoraServletFilter.class);

	public static final String PARAM_DECORA_MANAGER = "decora.manager";
	public static final String PARAM_DECORA_PARSER = "decora.parser";

	protected DecoraManager decoraManager;
	protected DecoraParser decoraParser;

	/**
	 * Creates Decora manager. Override to provide custom decora manager.
	 * Alternatively, set it in filter init parameters.
	 */
	protected DecoraManager createDecoraManager() {
		return new DecoraManager();
	}

	/**
	 * Creates Decora parser. Override to provide custom decora parser.
	 */
	protected DecoraParser createDecoraParser() {
		return new DecoraParser();
	}

	/**
	 * Initializes Decora filter. Loads manager and parser from init parameters.
	 */
	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		String decoraManagerClass = filterConfig.getInitParameter(PARAM_DECORA_MANAGER);

		if (decoraManagerClass != null) {
			try {
				Class decoraManagerType = ClassLoaderUtil.loadClass(decoraManagerClass);
				decoraManager = (DecoraManager) ClassUtil.newInstance(decoraManagerType);
			} catch (Exception ex) {
				log.error("Unable to load Decora manager class: " + decoraManagerClass, ex);
				throw new ServletException(ex);
			}
		} else {
			decoraManager = createDecoraManager();
		}

		String decoraParserClass = filterConfig.getInitParameter(PARAM_DECORA_PARSER);

		if (decoraParserClass != null) {
			try {
				Class decoraParserType = ClassLoaderUtil.loadClass(decoraParserClass);
				decoraParser = (DecoraParser) ClassUtil.newInstance(decoraParserType);
			} catch (Exception ex) {
				log.error("Unable to load Decora parser class: " + decoraParserClass, ex);
				throw new ServletException(ex);
			}
		} else {
			decoraParser = createDecoraParser();
		}
	}

	@Override
	public void destroy() {
	}


	/**
	 * Creates HTTP request wrapper. By default returns {@link DecoraRequestWrapper}.
	 */
	protected HttpServletRequest wrapRequest(final HttpServletRequest request) {
		return new DecoraRequestWrapper(request);
	}

	@Override
	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {

		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		final HttpServletResponse response = (HttpServletResponse) servletResponse;

		if (!decoraManager.decorateRequest(request)) {
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}

		HttpServletRequest decoraRequest = wrapRequest(request);

		/* PROCESS PAGE */

		final LastModifiedData lastModifiedData = new LastModifiedData();

		DecoraResponseWrapper pageWrapper = new DecoraResponseWrapper(request, response, lastModifiedData, decoraManager);

		filterChain.doFilter(decoraRequest, pageWrapper);

		if (!pageWrapper.isBufferingEnabled()) {
			// content was NOT buffered, so original request/response were used
			return;
		}

		char[] pageContent = pageWrapper.getBufferContentAsChars();

		if (pageContent == null || pageContent.length == 0) {
			// no page content
			return;
		}

		/* PROCESS DECORATOR */

        boolean decorated = false;

		// content was buffered, so try to decorate it

		String actionPath = DispatcherUtil.getServletPath(request);
		String decoratorPath = decoraManager.resolveDecorator(request, actionPath);

		if (decoratorPath != null) {
			BufferResponseWrapper decoratorWrapper = new BufferResponseWrapper(response, lastModifiedData);

			DispatcherUtil.forward(decoraRequest, decoratorWrapper, decoratorPath);

			char[] decoraContent = decoratorWrapper.getBufferedChars();

			Writer writer = servletResponse.getWriter();

			decoraParser.decorate(writer, pageContent, decoraContent);

			writer.flush();

			decorated = true;
			log.debug(() -> "Decora applied on " + actionPath);
		}
		else {
			log.debug(() -> "Decora not applied on " + actionPath);
		}

//		if (response.isCommitted() == false) {
//			pageWrapper.preResponseCommit();
//		}
		pageWrapper.commitResponse();

		/* DECORATOR NOT APPLIED, USE ORIGINAL RESPONSE (that is buffered) */

        if (!decorated) {
			if (pageWrapper.isBufferStreamBased()) {
				ServletOutputStream outputStream = response.getOutputStream();
				outputStream.write(pageWrapper.getBufferedBytes());
				outputStream.flush();
			} else {
				PrintWriter writer = response.getWriter();
				writer.append(CharBuffer.wrap(pageWrapper.getBufferedChars()));
				writer.flush();
			}
		}
	}

}
