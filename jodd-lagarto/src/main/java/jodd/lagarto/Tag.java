// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import java.io.IOException;

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
	 * Sets new tag name.
	 */
	void setName(CharSequence tagName);

	/**
	 * Sets new tag type.
	 */
	void setType(TagType type);

	/**
	 * Adds new attribute without checking if it already exist
	 * thus allowing duplicate attributes.
	 */
	void addAttribute(CharSequence name, CharSequence value);

	/**
	 * Sets new attribute value. If attribute already exist, it's value is changed.
	 * If attribute does not exist, it will be added to the list.
	 */
	void setAttribute(CharSequence name, CharSequence value);

	/**
	 * Sets value for attribute at specific index.
	 */
	void setAttributeValue(int index, CharSequence value);

	/**
	 * Sets value for attribute at specific index.
	 */
	void setAttributeValue(CharSequence name, CharSequence value);

	/**
	 * Changes attribute name on specific index.
	 */
	void setAttributeName(int index, CharSequence name);

	/**
	 * Removes attribute.
	 */
	void removeAttribute(int index);

	/**
	 * Removes attribute.
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
	 * Returns <code>true</code> if name equals to given chars.
	 */
	boolean nameEquals(char[] chars);
	/**
	 * Returns <code>true</code> if name equals to given char sequence.
	 */
	boolean nameEquals(CharSequence charSequence);

	/**
	 * Matches tag name to given lowercase tag name.
	 * Should be somewhat faster then {@link #nameEquals(char[])}
	 * since only one name is getting converted to lower ascii.
	 */
	boolean matchTagName(char[] tagNameLowercase);

	// ---------------------------------------------------------------- output

	/**
	 * Writes the tag to the output.
	 */
	void writeTo(Appendable out) throws IOException;

	/**
	 * Get the complete tag as a string.
	 */
	String toString();

}