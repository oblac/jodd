// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.adapter.jsbundle;

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
 * JS bundles servlet simply loads javascript bundles.
 */
public class JsBundlesServlet extends HttpServlet {

	protected JsBundlesManager jsBundlesManager;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		jsBundlesManager = JsBundlesManager.getJsBundlesManager(config.getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String bundleId = req.getParameter("id");
		File file = jsBundlesManager.lookupBundleFile(bundleId);
		OutputStream out = resp.getOutputStream();
		StreamUtil.copy(new FileInputStream(file), out);
	}
}
