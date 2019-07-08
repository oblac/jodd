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

/**
 * Tag information.
 */
public interface Tag {

	// ---------------------------------------------------------------- flags

	/**
	 * Returns case-sensitive flag for various name matching.
	 */
	boolean isCaseSensitive();

	/**
	 * Returns <code>true</code> if tag should parse inner text content as RAWTEXT.
	 */
	boolean isRawTag();

	// ---------------------------------------------------------------- read

	/**
	 * Returns tags name.
	 */
	CharSequence getName();

	/**
	 * Returns {@link TagType type of tag} (e.g. open, close, etc).
	 */
	TagType getType();

	/**
	 * Returns <b>id</b> attribute value of a tag.
	 * Implementations may simply call {@link #getAttributeValue(java.lang.CharSequence)}
	 * or to cache this value for better performances.
	 */
	CharSequence getId();

	/**
	 * Returns 1-based deep level of a tag from the root.
	 */
	int getDeepLevel();

	/**
	 * Returns number of tag attributes.
	 */
	int getAttributeCount();

	/**
	 * Returns attribute name.
	 */
	CharSequence getAttributeName(int index);

	/**
	 * Returns attribute value or <code>null</code> for an empty attribute,
	 */
	CharSequence getAttributeValue(int index);

	/**
	 * Returns attribute value or <code>null</code> for an empty attribute,
	 * Returns <code>null</code> also if attribute name does not exist.
	 */
	CharSequence getAttributeValue(CharSequence name);

	/**
	 * Returns attribute index or <code>-1</code> if not found.
	 */
	int getAttributeIndex(CharSequence name);

	/**
	 * Detects if an attribute is present.
	 */
	boolean hasAttribute(CharSequence name);

	// ---------------------------------------------------------------- position

	/**
	 * Returns tag position in the input source.
	 */
	int getTagPosition();

	/**
	 * Returns tag length in the input source.
	 */
	int getTagLength();

	/**
	 * Returns tag position string or <code>null</code> if position is not calculated.
	 */
	public String getPosition();

	// ---------------------------------------------------------------- write

	/**
	 * Sets tag name.
	 */
	void setName(CharSequence tagName);

	/**
	 * Sets {@link jodd.lagarto.TagType tag type}.
	 */
	void setType(TagType type);

	/**
	 * Adds new attribute without checking if it already exist
	 * thus allowing duplicate attributes.
	 */
	void addAttribute(CharSequence name, CharSequence value);

	/**
	 * Sets new attribute value. If attribute already exist, it's value is changed.
	 * If attribute does not exist, it will be added to the tag.
	 */
	void setAttribute(CharSequence name, CharSequence value);

	/**
	 * Sets value for attribute at specific index. Throws exception
	 * if index is invalid.
	 */
	void setAttributeValue(int index, CharSequence value);

	/**
	 * Sets value for attribute with given name. If attribute with given
	 * name doesn't exist, nothing changes.
	 */
	void setAttributeValue(CharSequence name, CharSequence value);

	/**
	 * Changes attribute name on specific index. Throws exception
	 * if index is invalid.
	 */
	void setAttributeName(int index, CharSequence name);

	/**
	 * Removes attribute at given index. Throws exception
	 * if index is invalid.
	 */
	void removeAttribute(int index);

	/**
	 * Removes attribute by given name.
	 */
	void removeAttribute(CharSequence name);

	/**
	 * Removes all attributes.
	 */
	void removeAttributes();

	/**
	 * Returns <code>true</code> if tag is modified.
	 */
	boolean isModified();

	// ---------------------------------------------------------------- match

	/**
	 * Returns <code>true</code> if name equals to given char sequence.
	 */
	boolean nameEquals(CharSequence charSequence);

	// ---------------------------------------------------------------- output

	/**
	 * Writes the tag to the output.
	 */
	void writeTo(Appendable out);

	/**
	 * Get the complete tag as a string.
	 */
	@Override
	String toString();

}