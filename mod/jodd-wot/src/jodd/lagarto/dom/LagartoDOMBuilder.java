// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

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

	protected boolean parsingHtml = true;

	public boolean isParsingHtml() {
		return parsingHtml;
	}

	/**
	 * Specifies if content is parsed as HTML (default, <code>true</code>) or XML (<code>false</code>).
	 */
	public void setParsingHtml(boolean parsingHtml) {
		this.parsingHtml = parsingHtml;
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
		lagarto.parse(domBuilderTagVisitor, parsingHtml);
		return domBuilderTagVisitor.getDocument();
	}

}
