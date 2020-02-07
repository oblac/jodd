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

package jodd.servlet.lagarto;

import jodd.io.FileNameUtil;
import jodd.servlet.DispatcherUtil;
import jodd.servlet.wrapper.BufferResponseWrapper;

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
 * Lagarto servlet filter takes HTML content and invokes user defined parser on it.
 * This filter is a generic one and does not use Lagarto parser explicitly.
 * It just gives a placeholder where user can add it's own parsing mechanism.
 */
public abstract class LagartoServletFilter implements Filter {

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	/**
	 * Wraps the response and parse it using Lagarto parser.
	 * It first calls {@link #processActionPath(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, String)}
	 * to optionally consumes path, then {@link #acceptActionPath(javax.servlet.http.HttpServletRequest, String)} to
	 * check if path is accepted for processing.
	 */
	@Override
	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		final HttpServletResponse response = (HttpServletResponse) servletResponse;
		final String actionPath = DispatcherUtil.getServletPath(request);

		if (processActionPath(request, response, actionPath)) {
			return;
		}

		if (!acceptActionPath(request, actionPath)) {
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}

		final BufferResponseWrapper wrapper = new BufferResponseWrapper(response);
		filterChain.doFilter(servletRequest, wrapper);

		// reset servlet response content length AFTER the chain, since
		// servlet container may set it and we are changing the content.
		servletResponse.setContentLength(-1);

		char[] content = wrapper.getBufferContentAsChars();

		if ((content != null) && (content.length != 0)) {
			try {
				content = parse(content, request);
			} catch (final Exception ex) {
				throw new ServletException(ex);
			}

			wrapper.writeContentToResponse(content);
		}
	}

	/**
	 * Manually process the action path and returns <code>true</code> if path is consumed.
	 * When path is consumed, filter chain is not continued.
	 * By default, it returns <code>false</code>.
	 */
	protected boolean processActionPath(
		final HttpServletRequest servletRequest,
		final HttpServletResponse servletResponse,
		final String actionPath) throws IOException {

		return false;
	}

	/**
	 * Accepts action path for further parsing. By default, only <code>*.htm(l)</code>
	 * requests are passed through and those without any extension.
	 */
	protected boolean acceptActionPath(final HttpServletRequest request, final String actionPath) {
		final String extension = FileNameUtil.getExtension(actionPath);
		if (extension.length() == 0) {
			return true;
		}
		if (extension.equals("html") || extension.equals("htm")) {
			return true;
		}
		return false;
	}

	/**
	 * Main method that parses content. It can use Lagarto parser
	 * or any other custom parsing technology.
	 */
	protected abstract char[] parse(char[] content, HttpServletRequest request);

}
