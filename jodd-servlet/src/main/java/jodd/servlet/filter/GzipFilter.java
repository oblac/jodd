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

import jodd.io.FileNameUtil;
import jodd.servlet.ServletUtil;
import jodd.typeconverter.Converter;
import jodd.typeconverter.TypeConversionException;
import jodd.util.StringPool;
import jodd.util.StringUtil;
import jodd.util.Wildcard;

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
 * Compresses output with GZIP, for browsers that supports it.
 * <p>
 * Configuration is based on the following initialization parameters:
 *
 * <ul>
 * <li><code>threshold</code> - min number of bytes for compressing
 * or 0 for no compression at all. By defaults is 0. Good value is 128.</li>
 *
 * <li><code>match</code> - comma separated string patterns to be found
 * in the uri for using gzip. Only uris that match these patterns will be gzipped.
 * Use '<b>*</b>' to enable default matching using just <b>extensions</b>.
 * <b>extensions</b> (ignoring the wildcards value)</li>
 *
 * <li><code>extensions</code> - when <b>match</b> is set to all resources,
 * this parameter defines list of URI extensions that should be gzipped.
 * By default set to: <code>html, htm, css, js</code>. Use '<b>*</b>' to
 * match all extensions.
 * </li>
 *
 * <li><code>exclude</code> - comma separated string patterns to be excluded
 * if found in uri for using gzip. It is applied only if all urls are <b>matched</b>.</li>
 *
 * <li><code>wildcards</code> - boolean that specifies wildcard matching for string patterns.
 * by default <code>false</code>. URL is matched as {@link Wildcard#matchPathOne(String, String[]) paths}.</li>
 *
 * <li><code>requestParameterName</code> - name of request parameter that can override GZipping.
 * Default value is <code>gzip</code>. Set it to an empty string to turn this feature off.
 * </li>
 *
 * </ul>
 *
 * All matching is done in lowercase. You can override this class for finer control.
 */
public class GzipFilter implements Filter {
	
	/**
	 * If browser supports gzip, sets the Content-Encoding response header and
	 * invoke resource with a wrapped response that collects all the output.
	 * Extracts the output and write it into a gzipped byte array. Finally, write
	 * that array to the client's output stream.
	 * <p>
	 * If browser does not support gzip, invokes resource normally.
	 */
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws ServletException, IOException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		if (
				(threshold == 0) ||
				(!ServletUtil.isGzipSupported(req)) ||
				(!isGzipEligible(req))
		) {
			chain.doFilter(request, response);
			return;
		}

		GzipResponseWrapper wrappedResponse = new GzipResponseWrapper(res);
		wrappedResponse.setCompressionThreshold(threshold);

		try {
			chain.doFilter(request, wrappedResponse);
		} finally {
			wrappedResponse.finishResponse();
		}
	}

    protected int threshold;			// the threshold number to compress, (0 == no compression).
	protected String[] matches;
	protected String[] excludes;
	protected boolean wildcards;
	protected String requestParameterName;
	protected String[] extensions;

	/**
	 * Filter initialization.
	 */
	@Override
	public void init(final FilterConfig config) {

		try {
			wildcards = Converter.get().toBooleanValue(config.getInitParameter("wildcards"), false);
		} catch (TypeConversionException ignore) {
			wildcards = false;
		}

		// min size
		try {
			threshold = Converter.get().toIntValue(config.getInitParameter("threshold"), 0);
		} catch (TypeConversionException ignore) {
			threshold = 0;
		}

		// match string
		String uriMatch = config.getInitParameter("match");

		if ((uriMatch != null) && (!uriMatch.equals(StringPool.STAR))) {
			matches = StringUtil.splitc(uriMatch, ',');
			for (int i = 0; i < matches.length; i++) {
				matches[i] = matches[i].trim();
			}
		}

		// exclude string
		String uriExclude = config.getInitParameter("exclude");

		if (uriExclude != null) {
			excludes = StringUtil.splitc(uriExclude, ',');
			for (int i = 0; i < excludes.length; i++) {
				excludes[i] = excludes[i].trim();
			}
		}

		// request parameter name
		requestParameterName = config.getInitParameter("requestParameterName");

		if (requestParameterName == null) {
			requestParameterName = "gzip";
		}

		requestParameterName = requestParameterName.trim();

		// allowed extensions

		String urlExtensions = config.getInitParameter("extensions");

		if (urlExtensions != null) {
			if (urlExtensions.equals(StringPool.STAR)) {
				extensions = null;
			} else {
				extensions = StringUtil.splitc(urlExtensions, ", ");
			}
		} else {
			extensions = new String[] {"html", "htm", "js", "css"};
		}

	}

	@Override
	public void destroy() {
	}

	/**
	 * Determine if request is eligible for GZipping.
	 */
	protected boolean isGzipEligible(final HttpServletRequest request) {
		// request parameter name

		if (requestParameterName.length() != 0) {
			String forceGzipString = request.getParameter(requestParameterName);

			if (forceGzipString != null) {
				return Converter.get().toBooleanValue(forceGzipString, false);
			}
		}

		// extract uri

		String uri = request.getRequestURI();

		if (uri == null) {
			return false;
		}

		uri = uri.toLowerCase();

		boolean result = false;

		// check uri

		if (matches == null) {					// match == *
			if (extensions == null) {			// extensions == *
				return true;
			}
			// extension
			String extension = FileNameUtil.getExtension(uri);

			if (extension.length() > 0) {
				extension = extension.toLowerCase();

				if (StringUtil.equalsOne(extension, extensions) != -1) {
					result = true;
				}
			}
		} else {
			if (wildcards) {
				result = Wildcard.matchPathOne(uri, matches) != -1;
			} else {
				for (String match : matches) {
					if (uri.contains(match)) {
						result = true;
						break;
					}
				}
			}
		}

		if ((result) && (excludes != null)) {
			if (wildcards) {
				if (Wildcard.matchPathOne(uri, excludes) != -1) {
					result = false;
				}
			} else {
				for (String exclude : excludes) {
					if (uri.contains(exclude)) {
						result = false;						// excludes founded
						break;
					}
				}
			}
		}

		return result;
	}

}