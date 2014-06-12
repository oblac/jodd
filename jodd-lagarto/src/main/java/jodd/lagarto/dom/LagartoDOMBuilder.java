// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.LagartoParser;

/**
 * Lagarto DOM builder creates DOM tree from HTML, XHTML or XML content.
 */
public class LagartoDOMBuilder implements DOMBuilder {

	public LagartoDOMBuilder() {
		enableHtmlMode();
	}

	// ---------------------------------------------------------------- flags

	protected LagartoDomBuilderConfig config = new LagartoDomBuilderConfig();
	protected LagartoNodeHtmlRenderer renderer = new LagartoNodeHtmlRenderer();

	public LagartoDomBuilderConfig getConfig() {
		return config;
	}

	public void setConfig(LagartoDomBuilderConfig config) {
		this.config = config;
	}

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

	// ---------------------------------------------------------------- quick settings

	/**
	 * Enables debug mode. Performances are lost.
	 */
	public LagartoDOMBuilder enableDebug() {
		config.collectErrors = true;
		config.setCalculatePosition(true);
		return this;
	}

	/**
	 * Disables debug mode.
	 */
	public LagartoDOMBuilder disableDebug() {
		config.collectErrors = false;
		config.setCalculatePosition(false);
		return this;
	}

	/**
	 * Enables {@link #enableHtmlMode() html mode} with additional
	 * and somewhat experimental rules.
	 */
	public LagartoDOMBuilder enableHtmlPlusMode() {
		enableHtmlMode();
		config.useFosterRules = true;
		config.unclosedTagAsOrphanCheck = true;
		return this;
	}


	/**
	 * Enables HTML5 parsing mode.
	 */
	public LagartoDOMBuilder enableHtmlMode() {
		config.ignoreWhitespacesBetweenTags = false;			// collect all whitespaces
		config.setCaseSensitive(false);							// HTML is case insensitive
		config.setEnableRawTextModes(true);						// script and style tags are parsed as CDATA
		config.enabledVoidTags = true;							// list of void tags
		config.selfCloseVoidTags = false;						// don't self close void tags
		config.impliedEndTags = true;							// some tags end is implied
		config.setEnableConditionalComments(true);				// enable IE conditional comments
		config.conditionalCommentExpression = "if !IE";			// treat HTML as non-IE browser
		config.setParseXmlTags(false);							// enable XML mode in parsing
		renderer.reset();
		return this;
	}

	/**
	 * Enables XHTML mode.
	 */
	public LagartoDOMBuilder enableXhtmlMode() {
		config.ignoreWhitespacesBetweenTags = false;			// collect all whitespaces
		config.setCaseSensitive(true);							// XHTML is case sensitive
		config.setEnableRawTextModes(false);					// all tags are parsed in the same way
		config.enabledVoidTags = true;							// list of void tags
		config.selfCloseVoidTags = true;						// self close void tags
		config.impliedEndTags = false;							// no implied tag ends
		config.setEnableConditionalComments(true);				// enable IE conditional comments
		config.conditionalCommentExpression = "if !IE";			// treat XHTML as non-IE browser
		config.setParseXmlTags(false);							// enable XML mode in parsing
		renderer.reset();
		return this;
	}

	/**
	 * Enables XML parsing mode.
	 */
	public LagartoDOMBuilder enableXmlMode() {
		config.ignoreWhitespacesBetweenTags = true;				// ignore whitespaces that are non content
		config.setCaseSensitive(true);							// XML is case sensitive
		config.setEnableRawTextModes(false);					// all tags are parsed in the same way
		config.enabledVoidTags = false;							// there are no void tags
		config.selfCloseVoidTags = false;						// don't self close empty tags (can be changed!)
		config.impliedEndTags = false;							// no implied tag ends
		config.setEnableConditionalComments(false);				// disable IE conditional comments
		config.conditionalCommentExpression = null;				// don't use
		config.setParseXmlTags(true);							// enable XML mode in parsing
		renderer.reset();
		return this;
	}

	// ---------------------------------------------------------------- parse

	/**
	 * Creates DOM tree from provided content.
	 */
	public Document parse(char[] content) {
		LagartoParser lagartoParser = new LagartoParser(content, true);
		return doParse(lagartoParser);
	}

	/**
	 * Creates DOM tree from the provided content.
	 */
	public Document parse(String content) {
		LagartoParser lagartoParser = new LagartoParser(content, true);
		return doParse(lagartoParser);
	}

	/**
	 * Parses the content using provided lagarto parser.
	 */
	protected Document doParse(LagartoParser lagartoParser) {
		lagartoParser.setConfig(config);

		LagartoDOMBuilderTagVisitor domBuilderTagVisitor =
				new LagartoDOMBuilderTagVisitor(this);

		lagartoParser.parse(domBuilderTagVisitor);

		return domBuilderTagVisitor.getDocument();
	}

}