// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.servlet.lagarto;

import jodd.io.FastCharArrayWriter;
import jodd.lagarto.LagartoParser;
import jodd.lagarto.TagAdapter;
import jodd.lagarto.visitor.TagWriter;

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
	protected final char[] parse(final char[] content, final HttpServletRequest request) {

		final LagartoParsingProcessor lpp = createParsingProcessor();

		if (lpp == null) {
			return content;
		}

		lpp.init(content);

		return lpp.parse(request);
	}

	/**
	 * Returns custom {@link LagartoParsingProcessor parsing processor}.
	 * Returns <code>null</code> when content does not have to be parsed.
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
		public void init(final char[] content) {
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
		public char[] parse(final HttpServletRequest request) {
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
		public char[] invokeLagarto(final TagAdapter tagAdapter) {
			lagartoParser.parse(tagAdapter);
			return fastCharArrayWriter.toCharArray();
		}

	}

}
