// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.LagartoParser;
import jodd.util.StringUtil;

import java.nio.CharBuffer;

/**
 * Lagarto DOM builder creates DOM tree from HTML or XML content.
 * XML parsing has the following differences:
 * <ul>
 *     <li>no special tags (<code>script</code>, <code>style</code>, <code>pre</code>)</li>
 *     <li>empty text nodes are ignored</li>
 *     <li>tag attributes are case sensitive</li>
 * </ul>
 */
public class LagartoDOMBuilder {

	/**
	 * Default void tags.
	 * http://dev.w3.org/html5/spec/Overview.html#void-elements
	 */
	public static final String[] HTML5_VOID_TAGS = {
			"area", "base", "br", "col", "command", "embed", "hr", "img", "input", "keygen", "link", "meta", "param", "source", "track", "wbr"};

	protected boolean ignoreWhitespacesBetweenTags;
	protected boolean caseSensitive;
	protected boolean parseSpecialTagsAsCdata = true;
	protected boolean ignoreComments;
	protected boolean selfCloseVoidTags;
	protected String[] voidTags = HTML5_VOID_TAGS;

	public boolean isIgnoreWhitespacesBetweenTags() {
		return ignoreWhitespacesBetweenTags;
	}

	/**
	 * Specifies if whitespaces between open/closed tags should be ignored.
	 */
	public void setIgnoreWhitespacesBetweenTags(boolean ignoreWhitespacesBetweenTags) {
		this.ignoreWhitespacesBetweenTags = ignoreWhitespacesBetweenTags;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * Specifies if tag names are case sensitive.
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public boolean isParseSpecialTagsAsCdata() {
		return parseSpecialTagsAsCdata;
	}

	/**
	 * Specifies if special tags should be parsed as CDATA block.
	 * @see LagartoParser#parse(jodd.lagarto.TagVisitor, boolean)
	 */
	public void setParseSpecialTagsAsCdata(boolean parseSpecialTagsAsCdata) {
		this.parseSpecialTagsAsCdata = parseSpecialTagsAsCdata;
	}

	public boolean isIgnoreComments() {
		return ignoreComments;
	}

	/**
	 * Specifies if comments should be ignored in DOM tree.
	 */
	public void setIgnoreComments(boolean ignoreComments) {
		this.ignoreComments = ignoreComments;
	}

	public String[] getVoidTags() {
		return voidTags;
	}

	/**
	 * Sets void tags. If <code>null</code>, void tags are not used.
	 */
	public void setVoidTags(String... voidTags) {
		this.voidTags = voidTags;
	}

	/**
	 * Returns <code>true</code> if void tags are used.
	 * Using void tags makes parsing a different.
	 */
	public boolean hasVoidTags() {
		return voidTags != null;
	}

	/**
	 * Returns <code>true</code> if tag name is void.
	 * If void tags are not defined, returns <code>false</code>
	 * for any input.
	 */
	public boolean isVoidTag(String tagName) {
		if (voidTags == null) {
			return false;
		}
		tagName = tagName.toLowerCase();
		return StringUtil.equalsOne(tagName, voidTags) != -1;
	}

	public boolean isSelfCloseVoidTags() {
		return selfCloseVoidTags;
	}

	/**
	 * Specifies if void tags should be self closed.
	 */
	public void setSelfCloseVoidTags(boolean selfCloseVoidTags) {
		this.selfCloseVoidTags = selfCloseVoidTags;
	}

	// ---------------------------------------------------------------- quick settings

	/**
	 * Enables HTML5 parsing mode.
	 */
	public LagartoDOMBuilder enableHtmlMode() {
		ignoreWhitespacesBetweenTags = false;	// collect all whitespaces
		caseSensitive = false;					// HTML is case insensitive
		parseSpecialTagsAsCdata = true;			// script and style tags are parsed as CDATA
		voidTags = HTML5_VOID_TAGS;				// list of void tags
		selfCloseVoidTags = false;			// don't self close void tags
		return this;
	}

	/**
	 * Enables XHTML mode.
	 */
	public LagartoDOMBuilder enableXhtmlMode() {
		ignoreWhitespacesBetweenTags = false;	// collect all whitespaces
		caseSensitive = true;					// XHTML is case sensitive
		parseSpecialTagsAsCdata = false;		// all tags are parsed in the same way
		voidTags = HTML5_VOID_TAGS;				// list of void tags
		selfCloseVoidTags = true;				// self close void tags
		return this;
	}

	/**
	 * Enables XML parsing mode.
	 */
	public LagartoDOMBuilder enableXmlMode() {
		ignoreWhitespacesBetweenTags = true;	// ignore whitespaces that are non content
		caseSensitive = true;					// XML is case sensitive
		parseSpecialTagsAsCdata = false;		// all tags are parsed in the same way
		voidTags = null;						// there are no void tags
		selfCloseVoidTags = false;				// don't self close empty tags (can be changed!)
		return this;
	}

	// ---------------------------------------------------------------- parse

	/**
	 * Creates DOM tree from provided content.
	 */
	public Document parse(CharSequence content) {
		LagartoParser lagarto = new LagartoParser(content);
		return parse(lagarto);
	}

	/**
	 * Creates DOM tree from the provided content.
	 */
	public Document parse(CharBuffer content) {
		LagartoParser lagarto = new LagartoParser(content);
		return parse(lagarto);
	}

	protected Document parse(LagartoParser lagarto) {
		DOMBuilderTagVisitor domBuilderTagVisitor = new DOMBuilderTagVisitor(this);
		lagarto.parse(domBuilderTagVisitor, parseSpecialTagsAsCdata);
		return domBuilderTagVisitor.getDocument();
	}

}
