// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.adapter.htmlstapler;

import jodd.io.StreamUtil;

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
 */
public class HtmlStaplerServlet extends HttpServlet {

	protected HtmlStaplerBundlesManager bundlesManager;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		bundlesManager = HtmlStaplerBundlesManager.getBundlesManager(config.getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String bundleId = req.getParameter("id");
		File file = bundlesManager.lookupBundleFile(bundleId);
		writeBundleFile(resp, file);
	}

	/**
	 * Outputs bundle file to the response.
	 */
	protected void writeBundleFile(HttpServletResponse resp, File bundleFile) throws IOException {
		OutputStream out = resp.getOutputStream();
		StreamUtil.copy(new FileInputStream(bundleFile), out);
	}

}
