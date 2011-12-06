// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc.filter;

import jodd.lagarto.TagWriter;
import jodd.lagarto.adapter.StripHtmlTagAdapter;
import jodd.lagarto.adapter.htmlstapler.HtmlStaplerBundlesManager;
import jodd.lagarto.adapter.htmlstapler.HtmlStaplerTagAdapter;
import jodd.lagarto.filter.SimpleLagartoServletFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import static jodd.lagarto.adapter.htmlstapler.HtmlStaplerBundlesManager.Strategy.RESOURCES_ONLY;

public class AppLagartoServletFilter extends SimpleLagartoServletFilter {

	protected HtmlStaplerBundlesManager bundlesManager;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);

		bundlesManager = new HtmlStaplerBundlesManager(filterConfig.getServletContext(), RESOURCES_ONLY);
		bundlesManager.reset();
	}

	@Override
	protected LagartoParsingProcessor createParsingProcessor() {
		return new LagartoParsingProcessor() {
			@Override
			protected char[] parse(TagWriter rootTagWriter, HttpServletRequest request) {
				StripHtmlTagAdapter stripHtmlTagAdapter = new StripHtmlTagAdapter(rootTagWriter);

				HtmlStaplerTagAdapter htmlStaplerTagAdapter = new HtmlStaplerTagAdapter(stripHtmlTagAdapter, request);

				char[] content = invokeLagarto(htmlStaplerTagAdapter);

				return htmlStaplerTagAdapter.postProcess(content);
			}
		};
	}

}
