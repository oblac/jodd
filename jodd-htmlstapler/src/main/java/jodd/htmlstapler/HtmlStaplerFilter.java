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

package jodd.htmlstapler;

import jodd.bean.BeanUtil;
import jodd.io.StreamUtil;
import jodd.lagarto.TagVisitor;
import jodd.lagarto.TagWriter;
import jodd.lagarto.adapter.StripHtmlTagAdapter;
import jodd.lagarto.filter.SimpleLagartoServletFilter;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.servlet.DispatcherUtil;
import jodd.servlet.ServletUtil;
import jodd.util.StringPool;
import jodd.time.TimeUtil;
import jodd.net.MimeTypes;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static jodd.htmlstapler.HtmlStaplerBundlesManager.Strategy;

/**
 * HtmlStapler filter.
 * Part of the parameters are here, the other part is in the
 * {@link #createBundleManager(javax.servlet.ServletContext, jodd.htmlstapler.HtmlStaplerBundlesManager.Strategy)}  bundle manager}.
 */
public class HtmlStaplerFilter extends SimpleLagartoServletFilter {

	private static final Logger log = LoggerFactory.getLogger(HtmlStaplerFilter.class);

	protected HtmlStaplerBundlesManager bundlesManager;

	protected boolean enabled = true;
	protected boolean stripHtml = true;
	protected boolean resetOnStart = true;
	protected boolean useGzip;
	protected int cacheMaxAge = TimeUtil.SECONDS_IN_DAY * 30;
	protected Strategy staplerStrategy = Strategy.RESOURCES_ONLY;

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);

		bundlesManager = createBundleManager(filterConfig.getServletContext(), staplerStrategy);

		readFilterConfigParameters(filterConfig, this,
				"enabled",
				"stripHtml",
				"resetOnStart",
				"useGzip",
				"cacheMaxAge"
		);

		String staplerStrategyName = filterConfig.getInitParameter("strategy");
		if (staplerStrategyName != null) {
			if (staplerStrategyName.equalsIgnoreCase("ACTION_MANAGED")) {
				staplerStrategy = Strategy.ACTION_MANAGED;
			}
		}

		readFilterConfigParameters(filterConfig, bundlesManager,
				"bundleFolder",
				"downloadLocal",
				"localAddressAndPort",
				"localFilesEncoding",
				"notFoundExceptionEnabled",
				"sortResources",
				"staplerPath",
				"randomDigestChars"
		);

		if (resetOnStart) {
			bundlesManager.reset();
		}
	}

	/**
	 * Reads filter config parameters and set into destination target.
	 */
	protected void readFilterConfigParameters(final FilterConfig filterConfig, final Object target, final String... parameters) {
		for (String parameter : parameters) {
			String value = filterConfig.getInitParameter(parameter);

			if (value != null) {
				BeanUtil.declared.setProperty(target, parameter, value);
			}
		}
	}

	/**
	 * Creates {@link HtmlStaplerBundlesManager} instance.
	 */
	protected HtmlStaplerBundlesManager createBundleManager(final ServletContext servletContext, final Strategy strategy) {
		String webRoot = servletContext.getRealPath(StringPool.EMPTY);

		String contextPath = ServletUtil.getContextPath(servletContext);

		return new HtmlStaplerBundlesManager(contextPath, webRoot, strategy);
	}

	@Override
	protected LagartoParsingProcessor createParsingProcessor() {
		if (!enabled) {
			return null;
		}

		return new LagartoParsingProcessor() {
			@Override
			protected char[] parse(final TagWriter rootTagWriter, final HttpServletRequest request) {

				TagVisitor visitor = rootTagWriter;

				if (stripHtml) {

					visitor = new StripHtmlTagAdapter(rootTagWriter) {
						@Override
						public void end() {
							super.end();
							if (log.isDebugEnabled()) {
								log.debug("Stripped: " + getStrippedCharsCount() + " chars");
							}
						}
					};
				}

				String servletPath = DispatcherUtil.getServletPath(request);

				HtmlStaplerTagAdapter htmlStaplerTagAdapter =
						new HtmlStaplerTagAdapter(bundlesManager, servletPath, visitor);

				// todo add more adapters

				char[] content = invokeLagarto(htmlStaplerTagAdapter);

				return htmlStaplerTagAdapter.postProcess(content);
			}
		};
	}

	@Override
	protected boolean processActionPath(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse, final String actionPath) throws IOException {

		String bundlePath = '/' + bundlesManager.getStaplerPath() + '/';

		if (!actionPath.startsWith(bundlePath)) {
			return false;
		}

		String bundleId = actionPath.substring(bundlePath.length());

		File file = bundlesManager.lookupBundleFile(bundleId);

		if (log.isDebugEnabled()) {
			log.debug("bundle: " + bundleId);
		}

		int ndx = bundleId.lastIndexOf('.');
		String extension = bundleId.substring(ndx + 1);

		String contentType = MimeTypes.getMimeType(extension);
		servletResponse.setContentType(contentType);

		if (useGzip && ServletUtil.isGzipSupported(servletRequest)) {
			file = bundlesManager.lookupGzipBundleFile(file);

			servletResponse.setHeader("Content-Encoding", "gzip");
		}

		if (!file.exists()) {
			throw new IOException("bundle not found: " + bundleId);
		}

		servletResponse.setHeader("Content-Length", String.valueOf(file.length()));
		servletResponse.setHeader("Last-Modified", TimeUtil.formatHttpDate(file.lastModified()));

		if (cacheMaxAge > 0) {
			servletResponse.setHeader("Cache-Control", "max-age=" + cacheMaxAge);
		}

		sendBundleFile(servletResponse, file);

		return true;
	}

	/**
	 * Outputs bundle file to the response.
	 */
	protected void sendBundleFile(final HttpServletResponse resp, final File bundleFile) throws IOException {
		OutputStream out = resp.getOutputStream();
		FileInputStream fileInputStream = new FileInputStream(bundleFile);
		try {
			StreamUtil.copy(fileInputStream, out);
		}
		finally {
			StreamUtil.close(fileInputStream);
		}
	}

}