// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.LagartoLexer;
import jodd.lagarto.LagartoParserEngine;
import jodd.lagarto.Tag;
import jodd.util.StringUtil;

import java.nio.CharBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Lagarto DOM builder creates DOM tree from HTML, XHTML or XML content.
 */
public class LagartoDOMBuilder extends LagartoParserEngine implements DOMBuilder {

	public LagartoDOMBuilder() {
		enableHtmlMode();
	}

	/**
	 * Default void tags.
	 * http://dev.w3.org/html5/spec/Overview.html#void-elements
	 */
	public static final String[] HTML5_VOID_TAGS = {
			"area", "base", "br", "col", "embed", "hr", "img", "input",
			"keygen", "link", "menuitem", "meta", "param", "source",
			"track", "wbr"};

	// ---------------------------------------------------------------- IN flags

	protected boolean ignoreWhitespacesBetweenTags;
	protected boolean caseSensitive;
	protected boolean ignoreComments;
	protected boolean selfCloseVoidTags;
	protected boolean collectErrors;
	protected String conditionalCommentExpression;
	protected String[] voidTags = HTML5_VOID_TAGS;
	protected boolean impliedEndTags;
	protected LagartoNodeHtmlRenderer renderer = new LagartoNodeHtmlRenderer();

	/**
	 * Returns {@link LagartoNodeHtmlRenderer} instance that generates HTML output.
	 */
	public LagartoNodeHtmlRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Sets new renderer.
	 */
	public void setRenderer(LagartoNodeHtmlRenderer renderer) {
		this.renderer = renderer;
	}

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

	public boolean isCollectErrors() {
		return collectErrors;
	}

	/**
	 * Enables error collection during parsing.
	 */
	public void setCollectErrors(boolean collectErrors) {
		this.collectErrors = collectErrors;
	}

	public String getConditionalCommentExpression() {
		return conditionalCommentExpression;
	}

	public void setConditionalCommentExpression(String conditionalCommentExpression) {
		this.conditionalCommentExpression = conditionalCommentExpression;
	}

	public boolean isImpliedEndTags() {
		return impliedEndTags;
	}

	/**
	 * Enables implied end tags for certain tags.
	 * This flag reduces the performances a bit, so if you
	 * are dealing with 'straight' html that uses closes
	 * tags, consider switching this flag off.
	 */
	public void setImpliedEndTags(boolean impliedEndTags) {
		this.impliedEndTags = impliedEndTags;
	}

	// ---------------------------------------------------------------- quick settings

	/**
	 * Enables HTML5 parsing mode.
	 */
	public LagartoDOMBuilder enableHtmlMode() {
		ignoreWhitespacesBetweenTags = false;			// collect all whitespaces
		caseSensitive = false;							// HTML is case insensitive
		parseSpecialTagsAsCdata = true;					// script and style tags are parsed as CDATA
		voidTags = HTML5_VOID_TAGS;						// list of void tags
		selfCloseVoidTags = false;						// don't self close void tags
		impliedEndTags = true;							// some tags end is implied
		enableConditionalComments = true;				// enable IE conditional comments
		conditionalCommentExpression = "if !IE";		// treat HTML as non-IE browser
		renderer.reset();
		return this;
	}

	/**
	 * Enables XHTML mode.
	 */
	public LagartoDOMBuilder enableXhtmlMode() {
		ignoreWhitespacesBetweenTags = false;			// collect all whitespaces
		caseSensitive = true;							// XHTML is case sensitive
		parseSpecialTagsAsCdata = false;				// all tags are parsed in the same way
		voidTags = HTML5_VOID_TAGS;						// list of void tags
		selfCloseVoidTags = true;						// self close void tags
		impliedEndTags = false;							// no implied tag ends
		enableConditionalComments = true;				// enable IE conditional comments
		conditionalCommentExpression = "if !IE";		// treat XHTML as non-IE browser
		renderer.reset();
		return this;
	}

	/**
	 * Enables XML parsing mode.
	 */
	public LagartoDOMBuilder enableXmlMode() {
		ignoreWhitespacesBetweenTags = true;			// ignore whitespaces that are non content
		caseSensitive = true;							// XML is case sensitive
		parseSpecialTagsAsCdata = false;				// all tags are parsed in the same way
		voidTags = null;								// there are no void tags
		selfCloseVoidTags = false;						// don't self close empty tags (can be changed!)
		impliedEndTags = false;							// no implied tag ends
		enableConditionalComments = false;				// disable IE conditional comments
		conditionalCommentExpression = null;			// don't use
		renderer.reset();
		return this;
	}

	// ---------------------------------------------------------------- parse

	/**
	 * Creates DOM tree from provided content.
	 */
	public Document parse(CharSequence content) {
		initialize(CharBuffer.wrap(content));
		return doParse();
	}

	/**
	 * Creates DOM tree from the provided content.
	 */
	public Document parse(CharBuffer content) {
		initialize(content);
		return doParse();
	}

	/**
	 * Parses the content.
	 */
	protected Document doParse() {
		DOMBuilderTagVisitor domBuilderTagVisitor = new DOMBuilderTagVisitor(this);

		parse(domBuilderTagVisitor);

		return domBuilderTagVisitor.getDocument();
	}

	// ---------------------------------------------------------------- factory

	/**
	 * Creates {@link CData tag}.
	 */
	public CData createCData(String cdata) {
		return new CData(this, cdata);
	}

	/**
	 * Creates {@link Comment}.
	 * @see Comment#Comment(LagartoDOMBuilder, String)
	 */
	public Comment createComment(String comment) {
		return new Comment(this, comment);
	}

	/**
	 * Creates conditional {@link Comment}.
	 * @see Comment#Comment(LagartoDOMBuilder, String, boolean, boolean, String)
	 */
	public Comment createConditionalComment(String comment, boolean isStartingTag, boolean conditionalDownlevelHidden, String additionalComment) {
		return new Comment(this, comment, isStartingTag, conditionalDownlevelHidden, additionalComment);
	}

	/**
	 * Creates root {@link Document} node.
	 */
	public Document createDocument() {
		return new Document(this);
	}

	/**
	 * Creates {@link Element} node from a {@link Tag}.
	 */
	public Element createElement(Tag tag, boolean voidElement, boolean selfClosed) {
		Element element = new Element(this, tag, voidElement, selfClosed);

		if (isCalculatePosition()) {
			element.position = calculatePosition(tag);
		}

		return element;
	}

	/**
	 * Creates empty tag.
	 */
	public Element createElement(String name) {
		return new Element(this, name, false, false);
	}

	/**
	 * Creates empty {@link Element} node.
	 */
	public Element createElement(String tagName, boolean voidElement, boolean selfClosed) {
		return new Element(this, tagName, voidElement, selfClosed);
	}

	/**
	 * Creates {@link Text} node.
	 */
	public Text createText(String text) {
		return new Text(this, text);
	}

	/**
	 * Creates empty {@link Text} node.
	 */
	public Text createText() {
		return new Text(this, null);
	}

	public DocumentType createDocumentType(String value, String publicId, String baseUri) {
		return new DocumentType(this, value, publicId, baseUri);
	}

	public XmlDeclaration createXmlDeclaration(Tag tag) {
		return new XmlDeclaration(this, tag);
	}

	public XmlDeclaration createXmlDeclaration(String string) {
		return new XmlDeclaration(this, string);
	}

	// ---------------------------------------------------------------- OUT

	protected List<String> errors;
	protected long elapsed;

	/**
	 * Add new error message to the {@link #getErrors() errors list}.
	 * If errors are {@link #isCollectErrors() not collected} error
	 * message is ignored.
	 */
	public void addError(String message) {
		if (collectErrors) {
			if (errors == null) {
				errors = new LinkedList<String>();
			}
			errors.add(message);
		}
	}

	/**
	 * Returns list of warnings and errors occurred during parsing.
	 * Returns <code>null</code> if parsing was successful; or if
	 * errors are {@link #setCollectErrors(boolean) not collected}.
	 */
	public List<String> getErrors() {
		return errors;
	}

	/**
	 * Returns elapsed parsing time in milliseconds.
	 */
	public long getParsingTime() {
		return elapsed;
	}

	// ---------------------------------------------------------------- position

	/**
	 * Calculates position of a tag.
	 */
	protected LagartoLexer.Position calculatePosition(Tag tag) {
		LagartoLexer lexer = getLexer();

		LagartoLexer.Position position = lexer.currentPosition();

		int column = position.column;

		if (tag.getName() != null) {
			column -= tag.getName().length();
		}
		for (int i = 0; i < tag.getAttributeCount(); i++) {
			column -= tag.getAttributeName(i).length();
			String value = tag.getAttributeValue(i);
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