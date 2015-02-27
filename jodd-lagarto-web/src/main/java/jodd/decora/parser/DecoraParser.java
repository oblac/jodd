// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

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
	public void decorate(Writer writer, char[] pageContent, char[] decoraContent) throws IOException {
		DecoraTag[] decoraTags = parseDecorator(decoraContent);

		parsePage(pageContent, decoraTags);

		writeDecoratedPage(writer, decoraContent, pageContent, decoraTags);
	}

	/**
	 * Parses decorator file and collects {@link jodd.decora.parser.DecoraTag Decora tags}
	 * used in template. Returned Decora tags have start and end index set,
	 * but their region is not set.
	 */
	protected DecoraTag[] parseDecorator(char[] decoraContent) {
		LagartoParser lagartoParser = new LagartoParser(decoraContent, true);
		lagartoParser.getConfig().setEnableRawTextModes(false);

		DecoratorTagVisitor visitor = new DecoratorTagVisitor();
		lagartoParser.parse(visitor);
		return visitor.getDecoraTags();
	}

	/**
	 * Parses target page and extracts Decora regions for replacements.
	 */
	protected void parsePage(char[] pageContent, DecoraTag[] decoraTags) {
		LagartoParser lagartoParser = new LagartoParser(pageContent, true);
		PageRegionExtractor writer = new PageRegionExtractor(decoraTags);
		lagartoParser.parse(writer);
	}

	/**
	 * Writes decorated content.
	 */
	protected void writeDecoratedPage(Writer out, char[] decoratorContent, char[] pageContent, DecoraTag[] decoraTags) throws IOException {
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
	protected void writeRegion(Writer out, char[] pageContent, DecoraTag decoraTag, DecoraTag[] decoraTags) throws IOException {
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