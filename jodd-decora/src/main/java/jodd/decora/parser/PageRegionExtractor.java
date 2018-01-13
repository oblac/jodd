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

	public PageRegionExtractor(final DecoraTag[] decoraTags) {
		this.decoraTags = decoraTags;
	}

	// ---------------------------------------------------------------- interface

	/**
	 * Region marker for founded, and not yet closed regions.
	 */
	public static class RegionMarker {
		public final CharSequence name;
		public int innerCount;

		public RegionMarker(final CharSequence name) {
			this.name = name;
			this.innerCount = 0;
		}
	}

	/**
	 * Decora tags of current regions.
	 */
	protected LinkedList<RegionMarker> regionMarkers = new LinkedList<>();


	@Override
	public void tag(final Tag tag) {

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
