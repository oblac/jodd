// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.filter;

import jodd.io.FastCharArrayWriter;
import jodd.lagarto.LagartoParser;
import jodd.lagarto.TagVisitor;
import jodd.lagarto.TagWriter;

import javax.servlet.http.HttpServletRequest;

/**
 * Simple version of {@link LagartoServletFilter} that uses Lagarto.
 * May be used when set of adapters does not overlap each other during
 * single content parsing i.e. visiting. Otherwise, use more generic
 * {@link LagartoServletFilter}.
 */
public abstract class SimpleLagartoServletFilter extends LagartoServletFilter {

	/**
	 * Parses content using Lagarto and {@link #createAdapters(jodd.lagarto.TagWriter) custom adapters}.
	 */
	@Override
	protected char[] parse(char[] content, HttpServletRequest request) {
		// create Lagarto
		LagartoParser lagartoParser = new LagartoParser(content);

		// prepare root tag writer
		FastCharArrayWriter fastCharArrayWriter = new FastCharArrayWriter();
        TagWriter tagWriter = new TagWriter(fastCharArrayWriter);

		TagVisitor tagVisitors = createAdapters(tagWriter, request);

		// parse
		lagartoParser.parse(tagVisitors);

		// return modified content
		return fastCharArrayWriter.toCharArray();
	}

	/**
	 * Creates set of nested adapters to apply while parsing.
	 * May be used if modifications does not overlap so everything can
	 * be done within single visit (i.e. within just one parsing).
	 */
	protected abstract TagVisitor createAdapters(TagWriter rootTagWriter, HttpServletRequest request);

}