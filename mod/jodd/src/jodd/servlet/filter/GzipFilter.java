// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.filter;

import jodd.servlet.ServletUtil;
import jodd.typeconverter.Convert;
import jodd.typeconverter.TypeConversionException;
import jodd.util.StringUtil;
import jodd.util.Wildcard;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Compresses output with gzip, for browsers that supports it.
 * <p>
 * Configuration is based on the following initialization parameters:
 *
 * <ul>
 * <li><b>threshold</b> - min number of bytes for compressing
 * or 0 for no compression at all. By defaults is 0.</li>
 *
 * <li><b>match</b> - comma separated string patterns to be found
 * in uri for using gzip. Only uri's that have these patterns inside will use
 * gzip. Use '*' for applying gzip on all resources (ignoring the wildcards value)</li>
 *
 * <li><b>excludes</b> - comma separated string patterns to be excluded
 * if found in uri for using gzip. It is applied only on <b>matched</b> uris.</li>
 *
 * <li><b>wildcards</b> - boolean that specifies wildcard matching for string patterns.
 * by default <code>false</code>.</li>
 *
 * </ul>
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
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		if (
				(threshold == 0) ||
				(ServletUtil.isGzipSupported(req) == false) ||
				(isGzipEligible(req) == false)
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

	/**
	 * Minimal threshold.
	 */
	protected int minThreshold = 128;

	/**
	 * The threshold number to compress, (0 == no compression).
	 */
    protected int threshold;

	protected String[] matches;
	protected String[] excludes;
	protected boolean wildcards;

	/**
	 * Filter initialization.
	 */
	public void init(FilterConfig config) throws ServletException {

		try {
			wildcards = Convert.toBooleanValue(config.getInitParameter("wildcards"), false);
		} catch (TypeConversionException ignore) {
			wildcards = false;
		}

		// min size
		try {
			threshold = Convert.toIntValue(config.getInitParameter("threshold"), 0);
		} catch (TypeConversionException ignore) {
			threshold = 0;
		}

		if (threshold < minThreshold) {
			threshold = 0;
		}

		// match string
		String uriMatch = config.getInitParameter("match");

		if ((uriMatch != null) && (uriMatch.equals("*") == false)) {
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
	}

	public void destroy() {
		matches = null;
		excludes = null;
	}

	/**
	 * Determine if uri is eligible for gzip-ing.
	 */
	private boolean isGzipEligible(HttpServletRequest req) {
		String uri = req.getRequestURI();
		if (uri == null) {
			return false;
		}

		boolean result = false;
		
		if (matches == null) {							// match=*
			result = true;
		} else {
			if (wildcards) {
				result = Wildcard.matchOne(uri, matches) != -1;
			} else {
				for (String match : matches) {
					if (uri.contains(match)) {
						result = true;
						break;
					}
				}
			}
		}

		if ((result == true) && (excludes != null)) {
			if (wildcards) {
				if (Wildcard.matchOne(uri, excludes) != -1) {
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
