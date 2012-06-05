// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.adapter.htmlstapler;

import jodd.datetime.JDateTime;
import jodd.io.StreamUtil;
import jodd.log.Log;
import jodd.servlet.ServletUtil;
import jodd.typeconverter.Convert;
import jodd.util.MimeTypes;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * HTML stapler servlet loads web resource bundles.
 * Has optional init parameter:
 * <li>gzip - when set to <code>true</code>, bundles content will be gzipped once
 * and then resend each time.</li>
 */
public class HtmlStaplerServlet extends HttpServlet {

	private static final SimpleDateFormat HTTP_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

	protected HtmlStaplerBundlesManager bundlesManager;

	protected boolean useGzip;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		bundlesManager = HtmlStaplerBundlesManager.getBundlesManager(config.getServletContext());

		useGzip = Convert.toBooleanValue(config.getInitParameter("gzip"), false);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String bundleId = request.getParameter("id");
		File file = bundlesManager.lookupBundleFile(bundleId);

		if (log.isDebugEnabled()) {
			log.debug("bundle: " + bundleId);
		}

		int ndx = bundleId.lastIndexOf('-');
		String extension = bundleId.substring(ndx + 1);

		String contentType = MimeTypes.getMimeType(extension);
		response.setContentType(contentType);

		if (useGzip && ServletUtil.isGzipSupported(request)) {
			file = bundlesManager.lookupGzipBundleFile(file);

			response.setHeader("Content-Encoding", "gzip");
		}

		if (file.exists() == false) {
			throw new ServletException("bundle not found: " + bundleId);
		}

		response.setHeader("Content-Length", String.valueOf(file.length()));

		response.setHeader("Last-Modified", getHttpDate(file.lastModified()));

		System.out.println(getHttpDate(file.lastModified()));

		sendBundleFile(response, file);
	}

	protected String getHttpDate(long time) {
		Date date = new Date(time);

		return HTTP_DATE_FORMAT.format(date);
	}

	/**
	 * Outputs bundle file to the response.
	 */
	protected void sendBundleFile(HttpServletResponse resp, File bundleFile) throws IOException {
		OutputStream out = resp.getOutputStream();
		StreamUtil.copy(new FileInputStream(bundleFile), out);
	}

	private static final Log log = Log.getLogger(HtmlStaplerServlet.class);






}