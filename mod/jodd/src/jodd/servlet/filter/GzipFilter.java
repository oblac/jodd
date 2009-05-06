// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.filter;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Compresses output with gzip, for browsers that supports it.<p>
 *
 * Configuration of this filter is based on the following initialization
 * parameters:
 *
 * <ul>
 * <li><strong>threshold</strong> - min number of bytes for compressing
 * or 0 for no compression at all.</li>
 *
 * <li><strong>match</strong> - comma separated string patterns to be found
 * in uri for using gzip. Only uri's that have these patterns inside will use
 * gzip.</li>
 *
 * <li><strong>excludes</strong> - comma separated string patterns to be excluded
 * if found in uri for using gzip. It is applied only on matched uris.</li>
 *
 * </ul>
 *
 * <p>
 * This filter has been build and extend from Tomcats example.
 * <p>
 *
 * Technical notes: i have found that this is the only way how gzip filter
 * can be build. The first idea that comes to mind is to wrap response to
 * some character-based wrapper, and then to gzip it to the output. This
 * works fine except when forwarding is used: forwarded page is gzipped, but
 * response header is no more there! I have not an idea how to fix this
 * except from this approach presented in Tomcat.
 */
public class GzipFilter implements Filter {
	
	private FilterConfig config;

	protected FilterConfig getFilterConfig() {
		return (config);
	}

	/**
	 * If browser supports gzip, set the Content-Encoding response header and
	 * invoke resource with a wrapped response that collects all the output.
	 * Extract the output and write it into a gzipped byte array. Finally, write
	 * that array to the client's output stream.
	 *
	 * If browser does not support gzip, invoke resource normally.
	 *
	 * @exception ServletException
	 * @exception IOException
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		if ( (threshold == 0) || (isGzipSupported(req) == false) || (isGzipEligible(req) == false) ) {
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
	 * Comma separated string patterns to be found in the request URI
	 */
	protected String uriMatch;
	
	/**
	 * Comma separated string patterns to be excluded in the request URI if
	 * founded by match.
	 */
	protected String uriExclude;

	/**
	 * Minimal threshold.
	 */
	private int minThreshold = 128;

	/**
	 * The threshold number to compress, (0 == no compression).
	 */
    protected int threshold;

	private String[] extensions;
	private String[] excludes;

	/**
	 * Filter initialization.
	 *
	 * @exception ServletException
	 */
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
		extensions = null;

		// min size
		try {
			threshold = Integer.parseInt(config.getInitParameter("threshold"));
		} catch (NumberFormatException nfe) {
			threshold = 0;
		}
		if (threshold < minThreshold) {
			threshold = 0;
		}

		// match string
		uriMatch = config.getInitParameter("match");
		if ((uriMatch != null) && (uriMatch.equals("*") == false)) {
			StringTokenizer st = new StringTokenizer(uriMatch, ",");
			int i = st.countTokens();
			if (i >= 1) {
				extensions = new String[i];
				i = 0;
				while (st.hasMoreTokens()) {
					extensions[i] = st.nextToken().trim();
					i++;
				}
			}
		}
		// exclude string
		uriExclude = config.getInitParameter("exclude");
		if (uriExclude != null) {
			StringTokenizer st = new StringTokenizer(uriExclude, ",");
			int i = st.countTokens();
			if (i >= 1) {
				excludes = new String[i];
				i = 0;
				while (st.hasMoreTokens()) {
					excludes[i] = st.nextToken().trim();
					i++;
				}
			}
		}
	}

	public void destroy() {
		config = null;
		extensions = null;
		excludes = null;
	}

	private boolean isGzipSupported(HttpServletRequest req) {
		String browserEncodings = req.getHeader("Accept-Encoding");
		return (browserEncodings != null) && (browserEncodings.indexOf("gzip") != -1);
	}

	/**
	 * Determine if uri is eligible for gzip-ing.
	 *
	 * @return true to gzip the uri, otherwise false
	 */
	private boolean isGzipEligible(HttpServletRequest req) {
		String uri = req.getRequestURI();
		if (uri == null) {
			return false;
		}
		boolean result = false;
		
		if (extensions == null) {						// match=*
			result = true;
		} else {
			for (String extension : extensions) {
				if (uri.indexOf(extension) != -1) {
					result = true;						// extension founded
					break;
				}
			}
		}

		if ((result == true) && (excludes != null)) {
			for (String exclude : excludes) {
				if (uri.indexOf(exclude) != -1) {
					result = false;						// excludes founded
					break;
				}
			}
		}
		return result;
	}
}
