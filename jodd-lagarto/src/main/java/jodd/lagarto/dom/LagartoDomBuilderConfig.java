// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.LagartoParserConfig;
import jodd.log.Logger;
import jodd.util.StringUtil;

/**
 * Additional configuration for {@link jodd.lagarto.dom.LagartoDOMBuilder}
 * based on {@link jodd.lagarto.LagartoParserConfig}.
 */
public class LagartoDomBuilderConfig extends LagartoParserConfig {

	/**
	 * Default void tags.
	 * http://dev.w3.org/html5/spec/Overview.html#void-elements
	 */
	public static final String[] HTML5_VOID_TAGS = {
			"area", "base", "br", "col", "embed", "hr", "img", "input",
			"keygen", "link", "menuitem", "meta", "param", "source",
			"track", "wbr"};

	protected boolean ignoreWhitespacesBetweenTags;
	protected boolean ignoreComments;
	protected boolean selfCloseVoidTags;
	protected boolean collectErrors;
	protected String conditionalCommentExpression;
	protected String[] voidTags = HTML5_VOID_TAGS;
	protected boolean impliedEndTags;
	protected Logger.Level parsingErrorLogLevel = Logger.Level.WARN;

	protected boolean useFosterRules;
	protected boolean unclosedTagAsOrphanCheck;

	// ---------------------------------------------------------------- access

	public boolean isUnclosedTagAsOrphanCheck() {
		return unclosedTagAsOrphanCheck;
	}

	public void setUnclosedTagAsOrphanCheck(boolean unclosedTagAsOrphanCheck) {
		this.unclosedTagAsOrphanCheck = unclosedTagAsOrphanCheck;
	}

	/**
	 * Returns <code>true</code> if {@link HtmlFosterRules foster rules}
	 * should be used.
	 */
	public boolean isUseFosterRules() {
		return useFosterRules;
	}

	public void setUseFosterRules(boolean useFosterRules) {
		this.useFosterRules = useFosterRules;
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

}