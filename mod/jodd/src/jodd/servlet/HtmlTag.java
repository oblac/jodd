// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import jodd.util.CharUtil;
import jodd.util.StringPool;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Very simple utility for working with HTML tag's names and attributes.
 */
public class HtmlTag {

	public static final char TAG_START = '<';
	public static final char TAG_END = '>';

	/**
	 * Locates HTML tag in specified source.
	 */
	public static HtmlTag locateNextTag(String html, int start) {

		int left = start;
		while (true) {
			left = html.indexOf(TAG_START, left);
			if (left == -1) {
				return null;
			}

			int max = html.length() - 1;
			if (left != max - 1) {
				if (html.charAt(left + 1) == '!') {
					left++;
					continue;
				}
			}
			break;
		}

		int right = html.indexOf(TAG_END, left);
		if (right == -1) {
			return null;
		}
		right++;
		return new HtmlTag(html.substring(left, right), left, right);
	}

	protected final String tag;

	protected final int nextIndex;
	protected final int firstIndex;

	protected final int lastIndex;
	protected final boolean isClosedTag;
	protected final boolean isEndTag;
	protected final int startIndex;

	public HtmlTag(String tag) {
		this(tag, 0, 0);
	}

	protected HtmlTag(String tag, int startIndex, int nextIndex) {
		this.tag = tag;
		this.nextIndex = nextIndex;
		this.startIndex = startIndex;
		int i = tag.length() - 1;
		if (tag.charAt(i - 1) == '/') {
			i--;
			isClosedTag = true;
		} else {
			isClosedTag = false;
		}
		lastIndex = i;

		i = 1;
		if (tag.charAt(i) == '/') {
			i++;
			isEndTag = true;
		} else {
			isEndTag = false;
		}
		firstIndex = i;
	}

	/**
	 * Returns unchanged tag content.
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Returns next index for location start.
	 */
	public int getNextIndex() {
		return nextIndex;
	}

	/**
	 * Returns start index of the tag.
	 */
	public int getStartIndex() {
		return startIndex;
	}

	/**
	 * Returns <code>true</code> if tag is closed (ends with '/&gt;').
	 */
	public boolean isClosedTag() {
		return isClosedTag;
	}

	/**
	 * Returns <code>true</code> if tag is closing tag.
	 */
	public boolean isEndTag() {
		return isEndTag;
	}

	// ---------------------------------------------------------------- tag name

	protected String tagName;
	protected int attrStartIndex;

	/**
	 * Returns tag's name.
	 */
	public String getTagName() {
		if (tagName == null) {
			tagName = resolveTagName().toLowerCase();
		}
		return tagName;
	}

	/**
	 * Resolves tag name from tag's body.
	 */
	protected String resolveTagName() {
		int start = firstIndex;

		// skip all whitespaces
		while (start < lastIndex) {
			if (CharUtil.isWhitespace(tag.charAt(start)) == false) {
				break;
			}
			start++;
		}
		if (start == lastIndex) {
			return "";          // tag name not found
		}

		int end = start;
		// skip all non-whitespaces
		while (end < lastIndex) {
			if (CharUtil.isWhitespace(tag.charAt(end)) == true) {
				break;
			}
			end++;
		}
		attrStartIndex = end + 1;
		return tag.substring(start, end);
	}

	// ---------------------------------------------------------------- tag attribute

	protected Map<String, String> attributes;
	protected boolean changed;

	/**
	 * Returns attribute value or <code>null</code> if
	 * attribute value doesn't exist.
	 */
	public String getAttribute(String attrName) {
		resolveAttributes();
		return attributes.get(attrName);
	}

	/**
	 * Returns <code>true</code> if attribute is included in the tag.
	 */
	public boolean hasAttribute(String attrName) {
		resolveAttributes();
		return attributes.containsKey(attrName);
	}

	/**
	 * Removes attribute from the tag.
	 */
	public void removeAttribute(String attrName) {
		resolveAttributes();
		changed = true;
		attributes.remove(attrName);
	}


	/**
	 * Adds attribute and its value to a tag. Attribute is added to the end of
	 * the tag, just before closing '>'. If name is not specified, nothing will
	 * be added. If value is not specified, it will be set to an empty string.
	 */
	public void setAttribute(String name, String value) {
		resolveAttributes();
		changed = true;
		attributes.put(name.toLowerCase(), HtmlEncoder.text(value));
	}

	/**
	 * Adds single attribute without value to a tag. Attribute is added to the
	 * end of the tag, just before closing '>'. If name is not specified, nothing
	 * will be added.
	 */
	public void setAttribute(String name) {
		resolveAttributes();
		changed = true;
		attributes.put(name.toLowerCase(), null);
	}

	/**
	 * Returns total number of attributes.
	 */
	public int totalAttributes() {
		resolveAttributes();
		return attributes.size();
	}


	/**
	 * Resolves attributes from tag's body.
	 */
	protected void resolveAttributes() {
		if (attributes != null) {
			return;
		}

		attributes = new LinkedHashMap<String, String>();
		if (attrStartIndex == 0) {
			resolveTagName();
		}
		int start = attrStartIndex;
		while (start < lastIndex) {

			// skip all whitespaces
			while (start < lastIndex) {
				if (CharUtil.isWhitespace(tag.charAt(start)) == false) {
					break;
				}
				start++;
			}
			if (start == lastIndex) {
				return;
			}

			// attribute name
			int end = start;
			while (end < lastIndex) {
				if ((CharUtil.isWhitespace(tag.charAt(end)) == true) || (tag.charAt(end) == '=')) {
					break;
				}
				end++;
			}
			String attributeName = tag.substring(start, end).toLowerCase();
			if (start == lastIndex) {
				attributes.put(attributeName, null);
				return;
			}

			// skip all whitespaces to the =
			start = end;
			while (start < lastIndex) {
				if (CharUtil.isWhitespace(tag.charAt(start)) == false) {
					break;
				}
				start++;
			}
			if (start == lastIndex) {
				return;
			}

			// check for '='
			if (tag.charAt(start) != '=') {
				attributes.put(attributeName, null);        // no value founded.
				continue;
			}

			// parse value
			start++;
			// skip all whitespaces
			while (start < lastIndex) {
				if (CharUtil.isWhitespace(tag.charAt(start)) == false) {
					break;
				}
				start++;
			}
			if (start == lastIndex) {
				return;
			}

			char quote = tag.charAt(start);
			if ((quote != '"') && (quote != '\'')) {
				quote = 0;
			}
			start++;
			end = start;
			while (end < lastIndex) {
				if (tag.charAt(end) == quote) {
					break;
				}
				end++;
			}

			String value = tag.substring(start, end);
			attributes.put(attributeName, value);

			start = end + 1;
		}
	}

	// ---------------------------------------------------------------- misc

	protected String suffix = StringPool.EMPTY;

	/**
	 * Adds suffix text that will be appended after the tags end.
	 */
	public void setSuffixText(String suffix) {
		this.suffix = suffix;
	}

	// ---------------------------------------------------------------- public parse

	/**
	 * Resolves tag.
	 */
	public String resolveTag() {
		if (changed == false) {
			return tag + suffix;
		}
		StringBuilder t = new StringBuilder();
		t.append(TAG_START);
		if (isEndTag) {
			t.append('/');
		}
		t.append(getTagName()).append(' ');
		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			t.append(entry.getKey());
			String value = entry.getValue();
			if (value != null) {
				t.append('=').append('\"').append(value).append('\"');
			}
			t.append(' ');
		}
		t.setLength(t.length() - 1);
		if (isClosedTag) {
			t.append('/');
		}
		t.append(TAG_END).append(suffix);
		return t.toString();
	}

	@Override
	public String toString() {
		return resolveTag();
	}

}
