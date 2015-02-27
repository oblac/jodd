// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.decora.parser;

import jodd.decora.DecoraException;
import jodd.lagarto.EmptyTagVisitor;
import jodd.lagarto.Tag;
import jodd.lagarto.TagType;

import java.util.LinkedList;

/**
 * Region extractor parses page and resolves regions for each Decora tag.
 */
public class PageRegionExtractor extends EmptyTagVisitor {

	protected final DecoraTag[] decoraTags;

	public PageRegionExtractor(DecoraTag[] decoraTags) {
		this.decoraTags = decoraTags;
	}

	// ---------------------------------------------------------------- interface

	/**
	 * Region marker for founded, and not yet closed regions.
	 */
	public static class RegionMarker {
		public final CharSequence name;
		public int innerCount;

		public RegionMarker(CharSequence name) {
			this.name = name;
			this.innerCount = 0;
		}
	}

	/**
	 * Decora tags of current regions.
	 */
	protected LinkedList<RegionMarker> regionMarkers = new LinkedList<RegionMarker>();


	@Override
	public void tag(Tag tag) {

		// detect region end and extract content
		if (tag.getType() == TagType.END) {

			if (!regionMarkers.isEmpty()) {

				// first check for inner tags
				RegionMarker regionMarker = regionMarkers.getLast();
				if (tag.nameEquals(regionMarker.name)) {
					regionMarker.innerCount--;
				}

				if (regionMarker.innerCount <= 0) {
					// region is closed, find Decora tag

					for (DecoraTag decoraTag : decoraTags) {
						if (decoraTag.isRegionStarted() && tag.nameEquals(decoraTag.getName())) {

							decoraTag.endRegion(tag.getTagPosition(), tag.getTagLength());

							regionMarkers.removeLast();
							return;
						}
					}

					throw new DecoraException("Region end is not aligned: " + tag.getName());
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
				// Decora region detected

				decoraTag.startRegion(tag.getTagPosition(), tag.getTagLength(), tag.getDeepLevel());

				RegionMarker regionMarker = new RegionMarker(tag.getName());
				regionMarkers.add(regionMarker);

				break;
			}
		}

		// detect inner tags of the same name as the last region
		if (!regionMarkers.isEmpty()) {
			RegionMarker regionMarker = regionMarkers.getLast();

			if (tag.nameEquals(regionMarker.name)) {
				regionMarker.innerCount++;	// increment count
			}
		}
	}

	@Override
	public void end() {
		if (!regionMarkers.isEmpty()) {
			throw new DecoraException("Invalid regions detected: " + regionMarkers.getLast().name);
		}
	}
}
