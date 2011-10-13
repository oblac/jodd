package jodd.lagarto;

import jodd.util.ArraysUtil;

import java.io.IOException;
import java.nio.CharBuffer;

/**
 * Reusable, parsed {@link Tag tag} implementation.
 */
class ParsedTag implements Tag {

	private static final String ATTR_NAME_ID = "id";
	// tag info
	private String name;
	private int idNdx;
	private TagType type;
	private String tagStart;
	private String tagEnd;

	// attributes
	private int attributesCount;
	private String[] attrNames = new String[10];
	private String[] attrValues = new String[10];

	// input data
	private final CharBuffer input;
	private int position;
	private int length;

	// state
	private int deepLevel;
	private boolean modified;

	// ---------------------------------------------------------------- internal

	ParsedTag(CharBuffer input) {
		this.input = input;
	}

	// 1
	void startTag(String name) {
		this.name = name;
		this.idNdx = -1;
		this.attributesCount = 0;
	}

	// 2
	void defineTag(TagType type, int start, int length) {
		this.type = type;
		this.position = start;
		this.length = length;
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

	public int getTagPosition() {
		return position;
	}

	public int getTagLength() {
		return length;
	}

	// ---------------------------------------------------------------- write

	public void setName(String tagName) {
		this.name = tagName;
		modified = true;
	}

	public void setType(TagType type) {
		this.type = type;
		modified = true;
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
		writeTo(out, false);
	}

	public void writeTo(Appendable out, boolean buildAlways) throws IOException {
		if (modified) {
			buildAlways = true;
		}
		if (buildAlways) {
			appendTo(out);
		} else {
			out.append(input.subSequence(position, position + length));
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(length);
		appendTo(sb);
		return sb.toString();
	}
}
