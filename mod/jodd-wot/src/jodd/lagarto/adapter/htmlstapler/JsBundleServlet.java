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
 * JS bundle servlet simply loads javascript bundles.
 */
public class JsBundleServlet extends HttpServlet {

	protected BundlesManager bundlesManager;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		bundlesManager = BundlesManager.getBundlesManager(config.getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String bundleId = req.getParameter("id");
		File file = bundlesManager.lookupBundleFile(bundleId);
		OutputStream out = resp.getOutputStream();
		StreamUtil.copy(new FileInputStream(file), out);
	}
}
