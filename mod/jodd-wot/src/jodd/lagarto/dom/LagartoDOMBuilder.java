// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.LagartoParser;

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

	protected boolean ignoreWhitespacesBetweenTags;
	protected boolean caseSensitive;
	protected boolean parseSpecialTagsAsCdata = true;

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

	// ---------------------------------------------------------------- quick settings

	/**
	 * Enables HTML5 parsing mode.
	 */
	public void enableHtmlMode() {
		ignoreWhitespacesBetweenTags = false;
		caseSensitive = false;
		parseSpecialTagsAsCdata = true;
	}

	/**
	 * Enables XHTML/XML parsing mode.
	 */
	public void enableXmlMode() {
		ignoreWhitespacesBetweenTags = true;
		caseSensitive = true;
		parseSpecialTagsAsCdata = false;
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
