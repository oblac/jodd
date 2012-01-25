// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.decora.parser;

import jodd.decora.DecoraException;
import jodd.lagarto.EmptyTagVisitor;
import jodd.lagarto.Tag;
import jodd.lagarto.TagType;

/**
 * Region extractor parses page and resolves regions for each decora tag.
 */
public class PageRegionExtractor extends EmptyTagVisitor {

	protected final DecoraTag[] decoraTags;

	public PageRegionExtractor(DecoraTag[] decoraTags) {
		this.decoraTags = decoraTags;
	}

	// ---------------------------------------------------------------- interface

	/**
	 * Number of currently defined regions.
	 */
	protected int currentRegions;


	@Override
	public void tag(Tag tag) {

		// detect region end and extract content
		if (tag.getType() == TagType.END) {
			if (currentRegions > 0) {
				for (DecoraTag decoraTag : decoraTags) {
					if (decoraTag.isRegionStarted() && decoraTag.getName().equals(tag.getName())) {

						decoraTag.endRegion(tag.getTagPosition(), tag.getTagLength());

						currentRegions--;
					}
				}
			}
			return;
		}

		if (tag.getType() == TagType.SELF_CLOSING) {
			return;
		}

		// detect region start
		for (DecoraTag decoraTag : decoraTags) {

			if (decoraTag.isRegionUndefined() && decoraTag.isMatchedTag(tag)) {

				decoraTag.startRegion(tag.getTagPosition(), tag.getTagLength());

				currentRegions++;
			}
		}
	}

	@Override
	public void end() {
		if (currentRegions != 0) {
			throw new DecoraException("Some regions are not defined correctly.");
		}
	}
}
