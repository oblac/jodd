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

package jodd.lagarto;

import jodd.util.ArraysUtil;
import jodd.util.CharSequenceUtil;
import jodd.net.HtmlEncoder;

import java.io.IOException;

/**
 * Reusable, parsed {@link Tag tag} implementation.
 */
class ParsedTag implements Tag {

	private static final CharSequence ATTR_NAME_ID = "id";

	// flags
	private boolean caseSensitive;
	private boolean rawTag;

	// tag info
	private CharSequence name;
	private int idNdx;
	private TagType type;

	// attributes
	private int attributesCount;
	private CharSequence[] attrNames = new CharSequence[16];
	private CharSequence[] attrValues = new CharSequence[16];

	private int tagStartIndex;
	private int tagLength;
	private String position;

	// state
	private int deepLevel;
	private boolean modified;

	// ---------------------------------------------------------------- internal

	/**
	 * Initializes the instance.
	 */
	public void init(final boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * Starts the tag with the index of first '<'.
	 * Resets all tag data.
	 */
	public void start(final int startIndex) {
		this.tagStartIndex = startIndex;
		this.name = null;
		this.idNdx = -1;
		this.attributesCount = 0;
		this.tagLength = 0;
		this.modified = false;
		this.type = TagType.START;
		this.rawTag = false;
	}

	/**
	 * Defines tag end with index of last '>'.
	 * Sets the modification flag to <code>false</code>.
	 */
	void end(final int endIndex) {
		this.tagLength = endIndex - tagStartIndex;
		this.modified = false;
	}

	void increaseDeepLevel() {
		deepLevel++;
	}

	void decreaseDeepLevel() {
		deepLevel--;
	}

	// ---------------------------------------------------------------- flags

	@Override
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	@Override
	public boolean isRawTag() {
		return rawTag;
	}

	public void setRawTag(final boolean isRawTag) {
		this.rawTag = isRawTag;
	}

	// ---------------------------------------------------------------- read

	@Override
	public CharSequence getName() {
		return name;
	}

	@Override
	public CharSequence getId() {
		if (idNdx == -1) {
			return null;
		}
		return attrValues[idNdx];
	}

	@Override
	public TagType getType() {
		return type;
	}

	@Override
	public int getDeepLevel() {
		return deepLevel;
	}

	@Override
	public int getAttributeCount() {
		return attributesCount;
	}

	@Override
	public CharSequence getAttributeName(final int index) {
		if (index >= attributesCount) {
			throw new IndexOutOfBoundsException();
		}
		return attrNames[index];
	}

	@Override
	public CharSequence getAttributeValue(final int index) {
		if (index >= attributesCount) {
			throw new IndexOutOfBoundsException();
		}
		return attrValues[index];
	}

	@Override
	public CharSequence getAttributeValue(final CharSequence name) {
		for (int i = 0; i < attributesCount; i++) {
			final CharSequence current = attrNames[i];
			if (caseSensitive ? current.equals(name) : CharSequenceUtil.equalsIgnoreCase(current, name)) {
				return attrValues[i];
			}
		}
		return null;
	}

	@Override
	public int getAttributeIndex(final CharSequence name) {
		for (int i = 0; i < attributesCount; i++) {
			final CharSequence current = attrNames[i];
			if (caseSensitive ? current.equals(name) : CharSequenceUtil.equalsIgnoreCase(current, name)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean hasAttribute(final CharSequence name) {
		return getAttributeIndex(name) > -1;
	}

	// ---------------------------------------------------------------- position

	@Override
	public int getTagPosition() {
		return tagStartIndex;
	}

	@Override
	public int getTagLength() {
		return tagLength;
	}

	@Override
	public String getPosition() {
		return position;
	}

	public void setPosition(final Scanner.Position position) {
		this.position = position.toString();
	}

	// ---------------------------------------------------------------- write

	@Override
	public void setName(final CharSequence tagName) {
		this.name = tagName;
		modified = true;
	}

	@Override
	public void setType(final TagType type) {
		this.type = type;
		modified = true;
	}

	@Override
	public void addAttribute(final CharSequence name, final CharSequence value) {
		ensureLength();
		attrNames[attributesCount] = name;
		setAttrVal(attributesCount, name, value);
		attributesCount++;
		modified = true;
	}

	@Override
	public void setAttribute(final CharSequence name, final CharSequence value) {
		final int index = getAttributeIndex(name);
		if (index == -1) {
			addAttribute(name, value);
		} else {
			setAttrVal(index, name, value);
		}
		modified = true;
	}

	@Override
	public void setAttributeValue(final int index, final CharSequence value) {
		if (index >= attributesCount) {
			throw new IndexOutOfBoundsException();
		}
		setAttrVal(index, value);
		modified = true;
	}

	@Override
	public void setAttributeValue(final CharSequence name, final CharSequence value) {
		final int index = getAttributeIndex(name);
		if (index != -1) {
			setAttrVal(index, name, value);
			modified = true;
		}
	}

	@Override
	public void setAttributeName(final int index, final CharSequence name) {
		if (index >= attributesCount) {
			throw new IndexOutOfBoundsException();
		}
		attrNames[index] = name;
		modified = true;
	}

	@Override
	public void removeAttribute(final int index) {
		if (index >= attributesCount) {
			throw new IndexOutOfBoundsException();
		}
		System.arraycopy(attrNames, index + 1, attrNames, index, attributesCount - index);
		System.arraycopy(attrValues, index + 1, attrValues, index, attributesCount - index);

		attributesCount--;
		modified = true;
	}

	@Override
	public void removeAttribute(final CharSequence name) {
		final int index = getAttributeIndex(name);
		if (index != -1) {
			removeAttribute(index);
		}
		modified = true;
	}

	@Override
	public void removeAttributes() {
		attributesCount = 0;
	}

	@Override
	public boolean isModified() {
		return modified;
	}

	// ---------------------------------------------------------------- match

	@Override
	public boolean nameEquals(final CharSequence charSequence) {
		return caseSensitive ? CharSequenceUtil.equals(name, charSequence) : CharSequenceUtil.equalsIgnoreCase(name, charSequence);
	}

	// ---------------------------------------------------------------- util

	private void ensureLength() {
		if (attributesCount + 1 >= attrNames.length) {
			attrNames = ArraysUtil.resize(attrNames, attributesCount * 2);
			attrValues = ArraysUtil.resize(attrValues, attributesCount * 2);
		}
	}

	private void setAttrVal(final int index, final CharSequence name, final CharSequence value) {
		if (idNdx == -1) {
			if (CharSequenceUtil.equalsToLowercase(name, ATTR_NAME_ID)) {
				idNdx = index;
			}
		}
		attrValues[index] = value;
	}

	private void setAttrVal(final int index, final CharSequence value) {
		attrValues[index] = value;
	}

	// ---------------------------------------------------------------- output

	private void appendTo(final Appendable out) {
		try {
			out.append(type.getStartString());

			out.append(name);

			if (attributesCount > 0) {
				for (int i = 0; i < attributesCount; i++) {
					out.append(' ');
					out.append(attrNames[i]);
					final CharSequence value = attrValues[i];
					if (value != null) {
						out.append('=').append('"');
						out.append(HtmlEncoder.attributeDoubleQuoted(value));
						out.append('"');
					}
				}
			}

			out.append(type.getEndString());
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	@Override
	public void writeTo(final Appendable out) {
		appendTo(out);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		appendTo(sb);
		return sb.toString();
	}

}