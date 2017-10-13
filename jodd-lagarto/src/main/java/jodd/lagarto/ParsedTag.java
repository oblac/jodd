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
import jodd.util.HtmlEncoder;

import java.io.IOException;

/**
 * Reusable, parsed {@link Tag tag} implementation.
 */
class ParsedTag implements Tag {

	private static final char[] ATTR_NAME_ID = new char[] {'i', 'd'};

	// flags
	private boolean caseSensitive;
	private boolean rawTag;

	// tag info
	private CharSequence name;
	private int idNdx;
	private TagType type;

	// attributes
	private int attributesCount;
	private CharSequence[] attrNames = new CharSequence[10];
	private CharSequence[] attrValues = new CharSequence[10];

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
	public void init(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * Starts the tag with the index of first '<'.
	 * Resets all tag data.
	 */
	public void start(int startIndex) {
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
	void end(int endIndex) {
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

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public boolean isRawTag() {
		return rawTag;
	}

	public void setRawTag(boolean isRawTag) {
		this.rawTag = isRawTag;
	}

	// ---------------------------------------------------------------- read

	public CharSequence getName() {
		return name;
	}

	public CharSequence getId() {
		if (idNdx == -1) {
			return null;
		}
		return attrValues[idNdx];
	}

	public TagType getType() {
		return type;
	}

	public int getDeepLevel() {
		return deepLevel;
	}

	public int getAttributeCount() {
		return attributesCount;
	}

	public CharSequence getAttributeName(int index) {
		if (index >= attributesCount) {
			throw new IndexOutOfBoundsException();
		}
		return attrNames[index];
	}

	public CharSequence getAttributeValue(int index) {
		if (index >= attributesCount) {
			throw new IndexOutOfBoundsException();
		}
		return attrValues[index];
	}

	public CharSequence getAttributeValue(CharSequence name) {
		for (int i = 0; i < attributesCount; i++) {
			CharSequence current = attrNames[i];
			if (caseSensitive ? current.equals(name) : TagUtil.equalsIgnoreCase(current, name)) {
				return attrValues[i];
			}
		}
		return null;
	}

	public CharSequence getAttributeValue(char[] name) {
		for (int i = 0; i < attributesCount; i++) {
			CharSequence current = attrNames[i];
			if (caseSensitive ? TagUtil.equals(current, name) : TagUtil.equalsIgnoreCase(current, name)) {
				return attrValues[i];
			}
		}
		return null;
	}

	public int getAttributeIndex(CharSequence name) {
		for (int i = 0; i < attributesCount; i++) {
			CharSequence current = attrNames[i];
			if (caseSensitive ? current.equals(name) : TagUtil.equalsIgnoreCase(current, name)) {
				return i;
			}
		}
		return -1;
	}

	public int getAttributeIndex(char[] name) {
		for (int i = 0; i < attributesCount; i++) {
			CharSequence current = attrNames[i];
			if (caseSensitive ? TagUtil.equals(current, name) : TagUtil.equalsIgnoreCase(current, name)) {
				return i;
			}
		}
		return -1;
	}

	public boolean hasAttribute(CharSequence name) {
		return getAttributeIndex(name) > -1;
	}

	// ---------------------------------------------------------------- position

	public int getTagPosition() {
		return tagStartIndex;
	}

	public int getTagLength() {
		return tagLength;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(Scanner.Position position) {
		this.position = position.toString();
	}

	// ---------------------------------------------------------------- write

	public void setName(CharSequence tagName) {
		this.name = tagName;
		modified = true;
	}

	public void setType(TagType type) {
		this.type = type;
		modified = true;
	}

	public void addAttribute(CharSequence name, CharSequence value) {
		ensureLength();
		attrNames[attributesCount] = name;
		setAttrVal(attributesCount, name, value);
		attributesCount++;
		modified = true;
	}

	public void setAttribute(CharSequence name, CharSequence value) {
		int index = getAttributeIndex(name);
		if (index == -1) {
			addAttribute(name, value);
		} else {
			setAttrVal(index, name, value);
		}
		modified = true;
	}

	public void setAttributeValue(int index, CharSequence value) {
		if (index >= attributesCount) {
			throw new IndexOutOfBoundsException();
		}
		setAttrVal(index, value);
		modified = true;
	}

	public void setAttributeValue(CharSequence name, CharSequence value) {
		int index = getAttributeIndex(name);
		if (index != -1) {
			setAttrVal(index, name, value);
			modified = true;
		}
	}

	public void setAttributeName(int index, CharSequence name) {
		if (index >= attributesCount) {
			throw new IndexOutOfBoundsException();
		}
		attrNames[index] = name;
		modified = true;
	}

	public void removeAttribute(int index) {
		if (index >= attributesCount) {
			throw new IndexOutOfBoundsException();
		}
		System.arraycopy(attrNames, index + 1, attrNames, index, attributesCount - index);
		System.arraycopy(attrValues, index + 1, attrValues, index, attributesCount - index);

		attributesCount--;
		modified = true;
	}

	public void removeAttribute(CharSequence name) {
		int index = getAttributeIndex(name);
		if (index != -1) {
			removeAttribute(index);
		}
		modified = true;
	}

	public void removeAttributes() {
		attributesCount = 0;
	}

	public boolean isModified() {
		return modified;
	}

	// ---------------------------------------------------------------- match

	public boolean nameEquals(char[] chars) {
		return caseSensitive ? TagUtil.equals(name, chars) : TagUtil.equalsIgnoreCase(name, chars);
	}

	public boolean nameEquals(CharSequence charSequence) {
		return caseSensitive ? TagUtil.equals(name, charSequence) : TagUtil.equalsIgnoreCase(name, charSequence);
	}

	/**
	 * Match tag name to given name in <b>lowercase</b>.
	 */
	public boolean matchTagName(char[] tagNameLowercase) {
		return TagUtil.equalsToLowercase(name, tagNameLowercase);
	}

	public boolean matchTagNamePrefix(char[] tagNamePrefixLowercase) {
			return TagUtil.startsWithLowercase(name, tagNamePrefixLowercase);
		}

	// ---------------------------------------------------------------- util

	private void ensureLength() {
		if (attributesCount + 1 >= attrNames.length) {
			attrNames = ArraysUtil.resize(attrNames, attributesCount * 2);
			attrValues = ArraysUtil.resize(attrValues, attributesCount * 2);
		}
	}

	private void setAttrVal(int index, CharSequence name, CharSequence value) {
		if (idNdx == -1) {
			if (TagUtil.equalsToLowercase(name, ATTR_NAME_ID)) {
				idNdx = index;
			}
		}
		attrValues[index] = value;
	}

	private void setAttrVal(int index, CharSequence value) {
		attrValues[index] = value;
	}

	// ---------------------------------------------------------------- output

	private void appendTo(Appendable out) {
		try {
			out.append(type.getStartString());

			out.append(name);

			if (attributesCount > 0) {
				for (int i = 0; i < attributesCount; i++) {
					out.append(' ');
					out.append(attrNames[i]);
					CharSequence value = attrValues[i];
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

	public void writeTo(Appendable out) throws IOException {
		appendTo(out);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		appendTo(sb);
		return sb.toString();
	}

}