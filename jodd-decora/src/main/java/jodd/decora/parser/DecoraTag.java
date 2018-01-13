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

import jodd.lagarto.Tag;
import jodd.util.CharSequenceUtil;

/**
 * Decora tag, defined by the name and its position in decorator file.
 */
public class DecoraTag {

	private final String name;
	private final String id;
	private final int start;
	private final int end;
	private final int defaultValueStart;
	private final int defaultValueLength;

	public DecoraTag(final String name, final String id, final int start, final int end) {
		this(name, id, start, end, -1, 0);
	}

	public DecoraTag(final String name, final String id, final int start, final int end, int defaultValueStart, final int defaultValueLength) {
		this.name = name;
		this.id = id;
		this.start = start;
		this.end = end;
		if (defaultValueLength == 0) {
			defaultValueStart = -1;
		}
		this.defaultValueStart = defaultValueStart;
		this.defaultValueLength = defaultValueLength;
	}

	/**
	 * Duplicates static content of the class, for caching purposes.
	 * If a decorator is static content, it does not have to be
	 * parsed all over again. Instead, just store parsed list of
	 * <code>DecoraTag</code>s and duplicate it.
	 */
	public DecoraTag duplicate() {
		return new DecoraTag(name, id, start, end, defaultValueStart, defaultValueLength);
	}

	/**
	 * Returns decora tag name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns start index of Decora tag in decorator file.
	 */
	public int getStartIndex() {
		return start;
	}

	/**
	 * Returns end index of Decora tag in decorator file.
	 */
	public int getEndIndex() {
		return end;
	}

	/**
	 * Returns start index of Decora default value if exist.
	 * Returns -1 if default value is not specified.
	 */
	public int getDefaultValueStart() {
		return defaultValueStart;
	}

	/**
	 * Returns the length of Decora default value if exist.
	 * Returns 0 if default value is not specified.
	 */
	public int getDefaultValueLength() {
		return defaultValueLength;
	}

	/**
	 * Returns <code>true</code> if Decora tag has
	 * default value.
	 */
	public boolean hasDefaultValue() {
		return defaultValueStart != -1;
	}

	/**
	 * Returns <code>true</code> if provided tag
	 * matches decorator tag.
	 */
	public boolean isMatchedTag(final Tag tag) {
		if (!tag.nameEquals(name)) {
			return false;
		}
		if (id != null) {
			CharSequence tagId = tag.getId();

			if (tagId == null) {
				return false;
			}

			if (!CharSequenceUtil.equals(id, tagId)) {
				return false;
			}
		}
		return true;
	}

	// ---------------------------------------------------------------- region

	private int regionStart = -1;
	private int regionLength;

	private int regionTagStart;
	private int regionTagEnd;

	private int deepLevel;

	/**
	 * Returns <code>true</code> if region is fully defined.
	 * Returns <code>false</code> if region is either started or
	 * undefined.
	 */
	public boolean isRegionDefined() {
		return regionLength != 0;
	}

	/**
	 * Returns <code>true</code> if region definition is started.
	 * Region start is known, but not yet the end.
	 */
	public boolean isRegionStarted() {
		return (regionStart != -1) && (regionLength == 0);
	}

	/**
	 * Returns <code>true</code> if region is undefined.
	 * Returns <code>false</code> if region is either started
	 * or defined.
	 */
	public boolean isRegionUndefined() {
		return regionStart == -1;
	}

	/**
	 * Starts defining region by setting the start index and reset region length to zero.
	 */
	public void startRegion(final int start, final int tagLen, final int deepLevel) {
		this.regionStart = start + tagLen;
		this.regionLength = 0;
		this.regionTagStart = start;
		this.deepLevel = deepLevel;
	}

	/**
	 * Ends region definition by setting the region length.
	 */
	public void endRegion(final int regionEnd, final int tagLen) {
		this.regionLength = regionEnd - regionStart;
		this.regionTagEnd = regionEnd + tagLen;
	}

	/**
	 * Returns start index of the region in the page.
	 */
	public int getRegionStart() {
		return regionStart;
	}

	/**
	 * Returns region length.
	 */
	public int getRegionLength() {
		return regionLength;
	}

	/**
	 * Returns <code>true</code> if region of this Decora tag
	 * is inside of region of provided Decora tag.
	 */
	public boolean isInsideOtherTagRegion(final DecoraTag decoraTag) {
		return (regionStart > decoraTag.getRegionStart()) && (regionStart < decoraTag.getRegionStart() + decoraTag.getRegionLength());
	}

	public int getRegionTagStart() {
		return regionTagStart;
	}

	public int getRegionTagEnd() {
		return regionTagEnd;
	}

	public int getDeepLevel() {
		return deepLevel;
	}

	@Override
	public String toString() {
		return "DecoraTag{" +
				"name='" + name + '\'' +
				", start=" + start +
				", end=" + end +
				", regionStart=" + regionStart +
				", regionLength=" + regionLength +
				'}';
	}
}
