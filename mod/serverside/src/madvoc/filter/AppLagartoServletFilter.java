// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package madvoc.filter;

import jodd.lagarto.TagVisitor;
import jodd.lagarto.TagWriter;
import jodd.lagarto.adapter.StripHtmlTagAdapter;
import jodd.lagarto.adapter.htmlstapler.HtmlStaplerBundlesManager;
import jodd.lagarto.adapter.htmlstapler.HtmlStaplerTagAdapter;
import jodd.lagarto.filter.SimpleLagartoServletFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class AppLagartoServletFilter extends SimpleLagartoServletFilter {

	protected HtmlStaplerBundlesManager bundlesManager;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);

		bundlesManager = new HtmlStaplerBundlesManager(filterConfig.getServletContext());
		bundlesManager.reset();
	}

	@Override
	protected TagVisitor createAdapters(TagWriter rootTagWriter, HttpServletRequest request) {
		StripHtmlTagAdapter stripHtmlTagAdapter = new StripHtmlTagAdapter(rootTagWriter);

		HtmlStaplerTagAdapter htmlStaplerTagAdapter = new HtmlStaplerTagAdapter(stripHtmlTagAdapter, request);

		return htmlStaplerTagAdapter;
	}
}
