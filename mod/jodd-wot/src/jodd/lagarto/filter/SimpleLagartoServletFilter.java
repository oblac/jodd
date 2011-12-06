// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.filter;

import jodd.io.FastCharArrayWriter;
import jodd.lagarto.LagartoParser;
import jodd.lagarto.TagAdapter;
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
	 * Parses content using Lagarto and {@link LagartoParsingProcessor}.
	 */
	@Override
	protected final char[] parse(char[] content, HttpServletRequest request) {

		LagartoParsingProcessor lpp = createParsingProcessor();

		lpp.init(content);

		return lpp.parse(request);
	}

	/**
	 * Returns custom {@link LagartoParsingProcessor parsing processor}.
	 */
	protected abstract LagartoParsingProcessor createParsingProcessor();

	/**
	 * Wrapper over Lagarto parsing process.
	 */
	protected abstract static class LagartoParsingProcessor {

		protected LagartoParser lagartoParser;
		protected FastCharArrayWriter fastCharArrayWriter;
		protected TagWriter tagWriter;

		/**
		 * Initialize processor by creating new Lagarto and root TagWriter.
		 */
		public void init(char[] content) {
			// create Lagarto
			lagartoParser = new LagartoParser(content);

			// prepare root tag writer
			fastCharArrayWriter = new FastCharArrayWriter();
			tagWriter = new TagWriter(fastCharArrayWriter);
		}

		/**
		 * Parses given and return adapted content.
		 * Delegates call to {@link #parse(jodd.lagarto.TagWriter, javax.servlet.http.HttpServletRequest)}
		 */
		public char[] parse(HttpServletRequest request) {
			return parse(tagWriter, request);
		}

		/**
		 * Creates set of nested adapters and {@link #invokeLagarto(jodd.lagarto.TagAdapter) invokes lagarto parsing}.
		 * May be used if modifications does not overlap so everything can
		 * be done within single visit (i.e. within just one parsing).
		 * Returns parsed content so it may be modified afterwards.
		 */
		protected abstract char[] parse(TagWriter rootTagWriter, HttpServletRequest request);

		/**
		 * Invokes Lagarto parser with provided set of adapters
		 * and returns processed content.
		 */
		public char[] invokeLagarto(TagAdapter tagAdapter) {
			lagartoParser.parse(tagAdapter);
			return fastCharArrayWriter.toCharArray();
		}

	}

}