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

package jodd.decora.parser;

import jodd.lagarto.LagartoParser;

import java.io.IOException;
import java.io.Writer;

/**
 * Decora parser takes page content and puts it into decorator template, to produce the output.
 * Important: Decora templates <b>do not</b> support <i>raw</i> tags, since Decora tags may be placed inside.
 * Decora parser does not depend on servlets.
 */
public class DecoraParser {

	/**
	 * Decorates page content with decorator template and outputs the result.
	 */
	public void decorate(final Writer writer, final char[] pageContent, final char[] decoraContent) throws IOException {
		DecoraTag[] decoraTags = parseDecorator(decoraContent);

		parsePage(pageContent, decoraTags);

		writeDecoratedPage(writer, decoraContent, pageContent, decoraTags);
	}

	/**
	 * Parses decorator file and collects {@link jodd.decora.parser.DecoraTag Decora tags}
	 * used in template. Returned Decora tags have start and end index set,
	 * but their region is not set.
	 */
	protected DecoraTag[] parseDecorator(final char[] decoraContent) {
		LagartoParser lagartoParser = new LagartoParser(decoraContent);
		lagartoParser.getConfig().setEnableRawTextModes(false);

		DecoratorTagVisitor visitor = new DecoratorTagVisitor();
		lagartoParser.parse(visitor);
		return visitor.getDecoraTags();
	}

	/**
	 * Parses target page and extracts Decora regions for replacements.
	 */
	protected void parsePage(final char[] pageContent, final DecoraTag[] decoraTags) {
		LagartoParser lagartoParser = new LagartoParser(pageContent);
		PageRegionExtractor writer = new PageRegionExtractor(decoraTags);
		lagartoParser.parse(writer);
	}

	/**
	 * Writes decorated content.
	 */
	protected void writeDecoratedPage(final Writer out, final char[] decoratorContent, final char[] pageContent, final DecoraTag[] decoraTags) throws IOException {
		int ndx = 0;

		for (DecoraTag decoraTag : decoraTags) {
			// [1] just copy content before the Decora tag
			int decoratorLen = decoraTag.getStartIndex() - ndx;
			if (decoratorLen <= 0) {
				continue;
			}
			out.write(decoratorContent, ndx, decoratorLen);

			ndx = decoraTag.getEndIndex();

			// [2] now write region at the place of Decora tag
			int regionLen = decoraTag.getRegionLength();

			if (regionLen == 0) {
				if (decoraTag.hasDefaultValue()) {
					out.write(decoratorContent, decoraTag.getDefaultValueStart(), decoraTag.getDefaultValueLength());
				}
			} else {
				writeRegion(out, pageContent, decoraTag, decoraTags);
			}

		}

		// write remaining content
		out.write(decoratorContent, ndx, decoratorContent.length - ndx);
	}

	/**
	 * Writes region to output, but extracts all inner regions.
	 */
	protected void writeRegion(final Writer out, final char[] pageContent, final DecoraTag decoraTag, final DecoraTag[] decoraTags) throws IOException {
		int regionStart = decoraTag.getRegionStart();
		int regionLen = decoraTag.getRegionLength();
		int regionEnd = regionStart + regionLen;

		for (DecoraTag innerDecoraTag : decoraTags) {
			if (decoraTag == innerDecoraTag) {
				continue;
			}

			if (decoraTag.isRegionUndefined()) {
				continue;
			}

			if (innerDecoraTag.isInsideOtherTagRegion(decoraTag)) {
				// write everything from region start to the inner Decora tag
				out.write(pageContent, regionStart, innerDecoraTag.getRegionTagStart() - regionStart);

				regionStart = innerDecoraTag.getRegionTagEnd();
			}
		}

		// write remaining content of the region
		out.write(pageContent, regionStart, regionEnd - regionStart);
	}

}