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

package jodd.servlet.filter;

import jodd.core.JoddCore;
import jodd.typeconverter.Converter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;


/**
 * Example filter that sets the character encoding to be used in parsing the
 * incoming request, either unconditionally or only if the client did not
 * specify a character encoding.  Configuration of this filter is based on
 * the following initialization parameters:
 *
 * <ul>
 * <li><strong>encoding</strong> - The character encoding to be configured
 *     for this request, either conditionally or unconditionally based on
 *     the <code>ignore</code> initialization parameter.  This parameter
 *     is required, so there is no default.</li>
 * <li><strong>ignore</strong> - If set to "true", any character encoding
 *     specified by the client is ignored, and the value returned by the
 *     <code>selectEncoding()</code> method is set.  If set to "false,
 *     <code>selectEncoding()</code> is called <strong>only</strong> if the
 *     client has not already specified an encoding.  By default, this
 *     parameter is set to "true".</li>
 * </ul>
 *
 * <p>Although this filter can be used unchanged, it is also easy to
 * subclass it and make the <code>selectEncoding()</code> method more
 * intelligent about what encoding to choose, based on characteristics of
 * the incoming request (such as the values of the <code>Accept-Language</code>
 * and <code>User-Agent</code> headers, or a value stashed in the current
 * user's session.
 */

public class CharacterEncodingFilter implements Filter {

	/**
	 * The default character encoding to set for requests that pass through
	 * this filter.
	 */
	protected String encoding;

	/**
	 * The filter configuration object we are associated with.  If this value
	 * is null, this filter instance is not currently configured.
	 */
	protected FilterConfig filterConfig;

	/**
	 * Should a character encoding specified by the client be ignored?
	 */
	protected boolean ignore = true;


	/**
	 * Take this filter out of service.
	 */
	@Override
	public void destroy() {
		this.encoding = null;
		this.filterConfig = null;
	}


	/**
	 * Select and set (if specified) the character encoding to be used to
	 * interpret request parameters for this request.
	 *
	 * @param request  servlet request we are processing
	 * @param response servlet response we are creating
	 * @param chain    filter chain we are processing
	 *
	 * @exception IOException
	 *                   if an input/output error occurs
	 * @exception ServletException
	 *                   if a servlet error occurs
	 */
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {

		// Conditionally select and set the character encoding to be used
		if (ignore || (request.getCharacterEncoding() == null)) {
			String encodingStr = selectEncoding(request);
			if (encodingStr != null) {
				request.setCharacterEncoding(encodingStr);
				response.setCharacterEncoding(encodingStr);
			}
		}
		chain.doFilter(request, response);
	}

	/**
	 * Place this filter into service.
	 *
	 * @param filterConfig The filter configuration object
	 */
	@Override
	public void init(final FilterConfig filterConfig) {

		this.filterConfig = filterConfig;
		this.encoding = filterConfig.getInitParameter("encoding");
		if (this.encoding == null) {
			this.encoding = JoddCore.encoding;
		}
		this.ignore = Converter.get().toBooleanValue(filterConfig.getInitParameter("ignore"), true);
	}

	/**
	 * Select an appropriate character encoding to be used, based on the
	 * characteristics of the current request and/or filter initialization
	 * parameters.  If no character encoding should be set, return
	 * <code>null</code>.
	 * <p>
	 * The default implementation unconditionally returns the value configured
	 * by the <strong>encoding</strong> initialization parameter for this
	 * filter.
	 *
	 * @param request The servlet request we are processing
	 */
	protected String selectEncoding(final ServletRequest request) {
		return this.encoding;
	}
}

