// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.decora.parser;

import jodd.lagarto.Tag;

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

	public DecoraTag(String name, String id, int start, int end) {
		this(name, id, start, end, -1, 0);
	}

	public DecoraTag(String name, String id, int start, int end, int defaultValueStart, int defaultValueLength) {
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
	public boolean isMatchedTag(Tag tag) {
		if (name.equals(tag.getName()) == false) {
			return false;
		}
		if (id != null) {
			if (id.equals(tag.getId()) == false) {
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
	public void startRegion(int start, int tagLen) {
		this.regionStart = start + tagLen;
		this.regionLength = 0;
		this.regionTagStart = start;
	}

	/**
	 * Ends region definition by setting the region length.
	 */
	public void endRegion(int regionEnd, int tagLen) {
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


	public int getRegionTagStart() {
		return regionTagStart;
	}

	public int getRegionTagEnd() {
		return regionTagEnd;
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
