// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.util.ArraysUtil;
import jodd.util.CharUtil;

import java.io.IOException;

/**
 * Reusable, parsed {@link Tag tag} implementation.
 */
class ParsedTag implements Tag {

	private static final String ATTR_NAME_ID = "id";
	// tag info
	protected String name;
	private int idNdx;
	private TagType type;
	private String tagStart;
	private String tagEnd;

	// attributes
	private int attributesCount;
	private String[] attrNames = new String[10];
	private String[] attrValues = new String[10];

	// input data
	private final LagartoLexer lexer;

	private int tagStartIndex;
	private int tagEndIndex;
	private int tagLength;

	// state
	private int deepLevel;
	private boolean modified;

	// ---------------------------------------------------------------- internal

	ParsedTag(LagartoLexer lexer) {
		this.lexer = lexer;
	}

	ParsedTag(char[] input) {
		this.lexer = null;
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
		this.tagStart = type.getStartString();
		this.tagEnd = type.getEndString();
	}

	/**
	 * Defines tag end with index of last '>'.
	 * Sets the modification flag to <code>false</code>.
	 */
	void end(int endIndex) {
		this.tagEndIndex = endIndex;
		this.tagLength = endIndex - tagStartIndex;
		this.modified = false;
	}

	// 2
	@Deprecated
	void defineTag(TagType type, int start, int length) {
		this.type = type;
		this.modified = false;
		this.tagStart = type.getStartString();
		this.tagEnd = type.getEndString();
	}

	void increaseDeepLevel() {
		deepLevel++;
	}

	void decreaseDeepLevel() {
		deepLevel--;
	}

	void setTagMarks(String start, String end) {
		this.tagStart = start;
		this.tagEnd = end;
	}


	public boolean matchTagName(char[] tagNameLowercase) {
		int len = tagNameLowercase.length;
		int tagNameLen = name.length();

		if (len != tagNameLen) {
			return false;
		}

		int ndx = 0;
		while (ndx < len) {
			if (CharUtil.toLowerAscii(name.charAt(ndx)) != tagNameLowercase[ndx]) {
				return false;
			}
			ndx++;
		}
		return true;
	}


	// ---------------------------------------------------------------- read

	public String getName() {
		return name;
	}

	public String getId() {
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

	public String getAttributeName(int index) {
		if (index >= attributesCount) {
			throw new IndexOutOfBoundsException();
		}
		return attrNames[index];
	}

	public String getAttributeValue(int index) {
		if (index >= attributesCount) {
			throw new IndexOutOfBoundsException();
		}
		return attrValues[index];
	}

	public String getAttributeValue(String name, boolean caseSensitive) {
		for (int i = 0; i < attributesCount; i++) {
			String current = attrNames[i];
			if (caseSensitive ? name.equals(current) : name.equalsIgnoreCase(current)) {
				return attrValues[i];
			}
		}
		return null;
	}

	public int getAttributeIndex(String name, boolean caseSensitive) {
		for (int i = 0; i < attributesCount; i++) {
			String current = attrNames[i];
			if (caseSensitive ? name.equals(current) : name.equalsIgnoreCase(current)) {
				return i;
			}
		}
		return -1;
	}

	public boolean hasAttribute(String name, boolean caseSensitive) {
		return getAttributeIndex(name, caseSensitive) > -1;
	}

	// ---------------------------------------------------------------- advanced

	public int getTagPosition() {		//  todo  remove from public API
		return tagStartIndex;
	}

	public int getTagLength() {			//  todo  remove from public API
		return tagLength;
	}

	// ---------------------------------------------------------------- write

	public void setName(String tagName) {
		this.name = tagName;
		modified = true;
	}

	public void setType(TagType type) {
		this.type = type;
		modified = true;
		tagStart = type.getStartString();
		tagEnd = type.getEndString();
	}

	public void addAttribute(String name, String value) {
		ensureLength();
		attrNames[attributesCount] = name;
		setAttrVal(attributesCount, name, value);
		attributesCount++;
		modified = true;
	}

	public void setAttribute(String name, boolean caseSensitive, String value) {
		int index = getAttributeIndex(name, caseSensitive);
		if (index == -1) {
			addAttribute(name, value);
		} else {
			setAttrVal(index, name, value);
		}
		modified = true;
	}

	public void setAttributeValue(int index, String value) {
		if (index >= attributesCount) {
			throw new IndexOutOfBoundsException();
		}
		setAttrVal(index, value);
		modified = true;
	}

	public void setAttributeValue(String name, boolean caseSensitive, String value) {
		int index = getAttributeIndex(name, caseSensitive);
		if (index != -1) {
			setAttrVal(index, name, value);
		}
		modified = true;
	}

	public void setAttributeName(int index, String name) {
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

	public void removeAttribute(String name, boolean caseSensitive) {
		int index = getAttributeIndex(name, caseSensitive);
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

	public void setModified() {
		modified = true;
	}

	// ---------------------------------------------------------------- util

	private void ensureLength() {
		if (attributesCount + 1 >= attrNames.length) {
			attrNames = ArraysUtil.resize(attrNames, attributesCount * 2);
			attrValues = ArraysUtil.resize(attrValues, attributesCount * 2);
		}
	}

	private void setAttrVal(int index, String name, String value) {
		if (idNdx == -1) {
			if (name.equalsIgnoreCase(ATTR_NAME_ID)) {
				idNdx = index;
			}
		}
		attrValues[index] = value;
	}

	private void setAttrVal(int index, String value) {
		attrValues[index] = value;
	}

	// ---------------------------------------------------------------- output

	private void appendTo(Appendable out) {
		try {
			out.append(tagStart);
			out.append(name);
			if (attributesCount > 0) {
				for (int i = 0; i < attributesCount; i++) {
					out.append(' ');
					out.append(attrNames[i]);
  					String value = attrValues[i];
					if (value != null) {
						out.append('=').append('"');
						out.append(value);
						out.append('"');
					}
				}
			}
			out.append(tagEnd);
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


	// ---------------------------------------------------------------- position

	/**
	 * Calculates approx position of a tag from current position.
	 */
	public LagartoLexer.Position calculateTagPosition() {
		LagartoLexer.Position position = lexer.currentPosition();

		int column = position.column;

		if (getName() != null) {
			column -= getName().length();
		}
		for (int i = 0; i < getAttributeCount(); i++) {
			column -= getAttributeName(i).length();
			String value = getAttributeValue(i);
			if (value != null) {
				column -= value.length();
				column--;	// for '='
			}
			column--;		// for attribute separation
		}

		int diff = position.column - column;

		position.column = column;
		position.offset -= diff;

		return position;
	}

}