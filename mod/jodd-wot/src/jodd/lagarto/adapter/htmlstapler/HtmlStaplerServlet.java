// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.adapter.htmlstapler;

import jodd.io.StreamUtil;
import jodd.io.ZipUtil;
import jodd.log.Log;
import jodd.servlet.ServletUtil;
import jodd.typeconverter.Convert;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * HTML stapler servlet loads web resource bundles.
 * Has optional init parameter:
 * <li>gzip - when set to <code>true</code>, bundles content will be gzipped once
 * and then resend each time.</li>
 */
public class HtmlStaplerServlet extends HttpServlet {

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

		if (useGzip && ServletUtil.isGzipSupported(request)) {
			file = lookupGzipBundleFile(file);
			response.setHeader("Content-Encoding", "gzip");
		}
		sendBundleFile(response, file);
	}

	/**
	 * Outputs bundle file to the response.
	 */
	protected void sendBundleFile(HttpServletResponse resp, File bundleFile) throws IOException {
		OutputStream out = resp.getOutputStream();
		StreamUtil.copy(new FileInputStream(bundleFile), out);
	}

	/**
	 * Locates gzipped version of bundle file. If gzip file
	 * does not exist, it will be created.
	 */
	protected File lookupGzipBundleFile(File file) throws IOException {
		String path = file.getPath() + ZipUtil.GZIP_EXT;
		File gzipFile = new File(path);

		if (gzipFile.exists() == false) {
			if (log.isDebugEnabled()) {
				log.debug("gzip bundle to " + path);
			}
			ZipUtil.gzip(file);
		}

		return gzipFile;
	}

	private static final Log log = Log.getLogger(HtmlStaplerServlet.class);

}
