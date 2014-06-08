// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import java.io.IOException;

/**
 * Tag information.
 */
public interface Tag {

	// ---------------------------------------------------------------- read

	/**
	 * Returns tags name.
	 */
	String getName();

	/**
	 * Returns {@link TagType type of tag} (e.g. open, close, etc).
	 */
	TagType getType();

	/**
	 * Returns <b>id</b> attribute value of a tag.
	 * Implementations may simply call {@link #getAttributeValue(String, boolean)}
	 * or to cache this value for better performances.
	 */
	String getId();

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
	String getAttributeName(int index);

	/**
	 * Returns attribute value or <code>null</code> for an empty attribute,
	 */
	String getAttributeValue(int index);

	/**
	 * Returns attribute value or <code>null</code> for an empty attribute,
	 * Returns <code>null</code> also if attribute name does not exist.
	 */
	String getAttributeValue(String name, boolean caseSensitive);

	/**
	 * Returns attribute index or <code>-1</code> if not found.
	 */
	int getAttributeIndex(String name, boolean caseSensitive);

	/**
	 * Detects if an attribute is present.
	 */
	boolean hasAttribute(String name, boolean caseSensitive);

	// ---------------------------------------------------------------- advanced

	/**
	 * Returns tag position in the input source.
	 */
	int getTagPosition();

	/**
	 * Returns tag length in the input source.
	 */
	int getTagLength();

	/**
	 * Returns tag position or <code>null</code> if position is not calculated.
	 */
	public String getPosition();

	// ---------------------------------------------------------------- write

	/**
	 * Sets new tag name.
	 */
	void setName(String tagName);

	/**
	 * Sets new tag type.
	 */
	void setType(TagType type);

	/**
	 * Adds new attribute without checking if it already exist
	 * thus allowing duplicate attributes.
	 */
	void addAttribute(String name, String value);

	/**
	 * Sets new attribute value. If attribute already exist, it's value is changed.
	 * If attribute does not exist, it will be added to the list.
	 */
	void setAttribute(String name, boolean caseSensitive, String value);

	/**
	 * Sets value for attribute at specific index.
	 */
	void setAttributeValue(int index, String value);

	/**
	 * Sets value for attribute at specific index.
	 */
	void setAttributeValue(String name, boolean caseSensitive, String value);

	/**
	 * Changes attribute name on specific index.
	 */
	void setAttributeName(int index, String name);

	/**
	 * Removes attribute.
	 */
	void removeAttribute(int index);

	/**
	 * Removes attribute.
	 */
	void removeAttribute(String name, boolean caseSensitive);

	/**
	 * Removes all attributes.
	 */
	void removeAttributes();

	/**
	 * Returns <code>true</code> if tag is modified.
	 */
	boolean isModified();

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