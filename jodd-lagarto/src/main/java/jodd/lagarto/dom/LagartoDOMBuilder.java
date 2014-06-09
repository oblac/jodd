// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.LagartoParser;
import jodd.log.Logger;
import jodd.util.StringUtil;

/**
 * Lagarto DOM builder creates DOM tree from HTML, XHTML or XML content.
 */
public class LagartoDOMBuilder implements DOMBuilder {

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

	// ---------------------------------------------------------------- flags

	protected boolean parseSpecialTagsAsCdata;
	protected boolean enableConditionalComments;
	protected boolean calculatePosition;
	protected boolean ignoreWhitespacesBetweenTags;
	protected boolean caseSensitive;
	protected boolean ignoreComments;
	protected boolean selfCloseVoidTags;
	protected boolean collectErrors;
	protected String conditionalCommentExpression;
	protected String[] voidTags = HTML5_VOID_TAGS;
	protected boolean impliedEndTags;
	protected boolean xmlMode;
	protected LagartoNodeHtmlRenderer renderer = new LagartoNodeHtmlRenderer();
	protected Logger.Level parsingErrorLogLevel = Logger.Level.WARN;

	// special flags
	protected boolean useFosterRules;
	protected boolean unclosedTagAsOrphanCheck;

	public boolean isUnclosedTagAsOrphanCheck() {
		return unclosedTagAsOrphanCheck;
	}

	public LagartoDOMBuilder setUnclosedTagAsOrphanCheck(boolean unclosedTagAsOrphanCheck) {
		this.unclosedTagAsOrphanCheck = unclosedTagAsOrphanCheck;
		return this;
	}

	/**
	 * Returns <code>true</code> if {@link HtmlFosterRules foster rules}
	 * should be used.
	 */
	public boolean isUseFosterRules() {
		return useFosterRules;
	}

	public LagartoDOMBuilder setUseFosterRules(boolean useFosterRules) {
		this.useFosterRules = useFosterRules;
		return this;
	}

	public boolean isParseSpecialTagsAsCdata() {
		return parseSpecialTagsAsCdata;
	}

	public LagartoDOMBuilder setParseSpecialTagsAsCdata(boolean parseSpecialTagsAsCdata) {
		this.parseSpecialTagsAsCdata = parseSpecialTagsAsCdata;
		return this;
	}

	public boolean isEnableConditionalComments() {
		return enableConditionalComments;
	}

	public LagartoDOMBuilder setEnableConditionalComments(boolean enableConditionalComments) {
		this.enableConditionalComments = enableConditionalComments;
		return this;
	}

	public boolean isCalculatePosition() {
		return calculatePosition;
	}

	public LagartoDOMBuilder setCalculatePosition(boolean calculatePosition) {
		this.calculatePosition = calculatePosition;
		return this;
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
	public LagartoDOMBuilder setRenderer(LagartoNodeHtmlRenderer renderer) {
		this.renderer = renderer;
		return this;
	}

	public boolean isIgnoreWhitespacesBetweenTags() {
		return ignoreWhitespacesBetweenTags;
	}

	/**
	 * Specifies if whitespaces between open/closed tags should be ignored.
	 */
	public LagartoDOMBuilder setIgnoreWhitespacesBetweenTags(boolean ignoreWhitespacesBetweenTags) {
		this.ignoreWhitespacesBetweenTags = ignoreWhitespacesBetweenTags;
		return this;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * Specifies if tag names are case sensitive.
	 */
	public LagartoDOMBuilder setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
		return this;
	}

	public boolean isIgnoreComments() {
		return ignoreComments;
	}

	/**
	 * Specifies if comments should be ignored in DOM tree.
	 */
	public LagartoDOMBuilder setIgnoreComments(boolean ignoreComments) {
		this.ignoreComments = ignoreComments;
		return this;
	}

	public String[] getVoidTags() {
		return voidTags;
	}

	/**
	 * Sets void tags. If <code>null</code>, void tags are not used.
	 */
	public LagartoDOMBuilder setVoidTags(String... voidTags) {
		this.voidTags = voidTags;
		return this;
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
	public LagartoDOMBuilder setSelfCloseVoidTags(boolean selfCloseVoidTags) {
		this.selfCloseVoidTags = selfCloseVoidTags;
		return this;
	}

	public boolean isCollectErrors() {
		return collectErrors;
	}

	/**
	 * Enables error collection during parsing.
	 */
	public LagartoDOMBuilder setCollectErrors(boolean collectErrors) {
		this.collectErrors = collectErrors;
		return this;
	}

	public String getConditionalCommentExpression() {
		return conditionalCommentExpression;
	}

	public LagartoDOMBuilder setConditionalCommentExpression(String conditionalCommentExpression) {
		this.conditionalCommentExpression = conditionalCommentExpression;
		return this;
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
	public LagartoDOMBuilder setImpliedEndTags(boolean impliedEndTags) {
		this.impliedEndTags = impliedEndTags;
		return this;
	}

	/**
	 * Returns parsing error log level.
	 */
	public Logger.Level getParsingErrorLogLevel() {
		return parsingErrorLogLevel;
	}

	/**
	 * Sets parsing error log level as a name.
	 */
	public void setParsingErrorLogLevelName(String logLevel) {
		logLevel = logLevel.trim().toUpperCase();

		parsingErrorLogLevel = Logger.Level.valueOf(logLevel);
	}

	public boolean isXmlMode() {
		return xmlMode;
	}

	public void setXmlMode(boolean xmlMode) {
		this.xmlMode = xmlMode;
	}

	// ---------------------------------------------------------------- quick settings

	/**
	 * Enables debug mode. Performances are lost.
	 */
	public LagartoDOMBuilder enableDebug() {
		collectErrors = true;
		calculatePosition = true;
		return this;
	}

	/**
	 * Disables debug mode.
	 */
	public LagartoDOMBuilder disableDebug() {
		collectErrors = false;
		calculatePosition = false;
		return this;
	}

	/**
	 * Enables {@link #enableHtmlMode() html mode} with additional
	 * and somewhat experimental rules.
	 */
	public LagartoDOMBuilder enableHtmlPlusMode() {
		enableHtmlMode();
		useFosterRules = true;
		unclosedTagAsOrphanCheck = true;
		return this;
	}


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
		xmlMode = false;								// enable XML mode in parsing
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
		xmlMode = false;								// enable XML mode in parsing
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
		xmlMode = true;									// enable XML mode in parsing
		renderer.reset();
		return this;
	}

	// ---------------------------------------------------------------- parse

	/**
	 * Creates DOM tree from provided content.
	 */
	public Document parse(char[] content) {
		LagartoParser lagartoParser = new LagartoParser(content);
		return doParse(lagartoParser);
	}

	/**
	 * Creates DOM tree from the provided content.
	 */
	public Document parse(String content) {
		LagartoParser lagartoParser = new LagartoParser(content);
		return doParse(lagartoParser);
	}

	/**
	 * Parses the content using provided lagarto parser.
	 */
	protected Document doParse(LagartoParser lagartoParser) {
		// parser flags
		//lagartoParser.setParseSpecialTagsAsCdata(parseSpecialTagsAsCdata);		// todo add flag for NOT HAVING THE RAW
		lagartoParser.setEnableConditionalComments(enableConditionalComments);
		lagartoParser.setCalculatePosition(calculatePosition);
		lagartoParser.setXmlMode(xmlMode);

		LagartoDOMBuilderTagVisitor domBuilderTagVisitor =
				new LagartoDOMBuilderTagVisitor(this);

		lagartoParser.parse(domBuilderTagVisitor);

		return domBuilderTagVisitor.getDocument();
	}

}