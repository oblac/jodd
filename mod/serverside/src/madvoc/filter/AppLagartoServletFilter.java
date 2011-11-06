// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package madvoc.filter;

import jodd.lagarto.TagVisitor;
import jodd.lagarto.TagWriter;
import jodd.lagarto.adapter.jsbundle.JsBundleTagAdapter;
import jodd.lagarto.adapter.StripHtmlTagAdapter;
import jodd.lagarto.adapter.jsbundle.JsBundlesManager;
import jodd.lagarto.filter.SimpleLagartoServletFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppLagartoServletFilter extends SimpleLagartoServletFilter {

	JsBundlesManager jsBundlesManager;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);

		jsBundlesManager = new JsBundlesManager(filterConfig.getServletContext());
		jsBundlesManager.reset();
	}

	@Override
	protected TagVisitor createAdapters(TagWriter rootTagWriter, HttpServletRequest request) {
		StripHtmlTagAdapter stripHtmlTagAdapter = new StripHtmlTagAdapter(rootTagWriter);

		JsBundleTagAdapter jsBundleTagAdapter = new JsBundleTagAdapter(jsBundlesManager, stripHtmlTagAdapter, request);

		return jsBundleTagAdapter;
	}
}
