// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.decora.parser;

import jodd.lagarto.LagartoParser;

import java.io.IOException;
import java.io.Writer;

/**
 * Decora parser takes decorator and page content and produce the output content.
 * <p>
 * Decora parser may be used outside of container.
 */
public class DecoraParser {

	/**
	 * Decorates page content with decorator content and outputs the result.
	 */
	public void decorate(Writer writer, char[] pageContent, char[] decoraContent) throws IOException {
		DecoraTag[] decoraTags = parseDecorator(decoraContent);

		parsePage(pageContent, decoraTags);

		writeDecoratedPage(writer, decoraContent, pageContent, decoraTags);
	}

	/**
	 * Parses decorator.
	 */
	protected DecoraTag[] parseDecorator(char[] decoraContent) {
		LagartoParser lagartoParser = new LagartoParser(decoraContent);
		DecoratorTagVisitor visitor = new DecoratorTagVisitor();
		lagartoParser.parse(visitor);
		return visitor.getDecoraTags();
	}

	/**
	 * Parses page and extracts decora regions for replacements.
	 */
	protected void parsePage(char[] pageContent, DecoraTag[] decoraTags) {
		LagartoParser lagartoParser = new LagartoParser(pageContent);
		PageRegionExtractor writer = new PageRegionExtractor(decoraTags);
		lagartoParser.parse(writer);
	}

	/**
	 * Writes decorated content.
	 */
	protected void writeDecoratedPage(Writer out, char[] decoratorContent, char[] pageContent, DecoraTag[] decoraTags) throws IOException {
		int ndx = 0;

		for (DecoraTag decoraTag : decoraTags) {
			int decoratorLen = decoraTag.getStartIndex() - ndx;
			if (decoratorLen <= 0) {
				continue;
			}
			out.write(decoratorContent, ndx, decoratorLen);

			ndx = decoraTag.getEndIndex();

			int regionLen = decoraTag.getRegionLength();

			if (regionLen == 0) {
				if (decoraTag.hasDefaultValue()) {
					out.write(decoratorContent, decoraTag.getDefaultValueStart(), decoraTag.getDefaultValueLength());
				}
			} else {
				writeRegion(out, pageContent, decoraTag, decoraTags);
			}

		}

		out.write(decoratorContent, ndx, decoratorContent.length - ndx);
	}

	/**
	 * Write region, but extract all inner regions.
	 */
	protected void writeRegion(Writer out, char[] pageContent, DecoraTag decoraTag, DecoraTag[] decoraTags) throws IOException {
		int regionStart = decoraTag.getRegionStart();
		int regionLen = decoraTag.getRegionLength();
		int regionEnd = regionStart + regionLen;

		for (DecoraTag decoraTag2 : decoraTags) {
			if (decoraTag == decoraTag2) {
				continue;
			}

			if (decoraTag.isRegionUndefined()) {
				continue;
			}

			int regionStart2 = decoraTag2.getRegionStart();

			if ((regionStart2 > regionStart) && (regionStart2 < regionEnd)) {
				out.write(pageContent, regionStart, decoraTag2.getRegionTagStart() - regionStart);

				regionStart = decoraTag2.getRegionTagEnd();
			}
		}
		out.write(pageContent, regionStart, regionEnd - regionStart);
	}

}
