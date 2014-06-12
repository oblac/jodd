// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.LagartoParserConfig;
import jodd.log.Logger;

/**
 * Additional configuration for {@link jodd.lagarto.dom.LagartoDOMBuilder}
 * based on {@link jodd.lagarto.LagartoParserConfig}.
 */
public class LagartoDomBuilderConfig extends LagartoParserConfig<LagartoDomBuilderConfig> {

	protected boolean ignoreWhitespacesBetweenTags;
	protected boolean ignoreComments;
	protected boolean selfCloseVoidTags;
	protected boolean collectErrors;
	protected String conditionalCommentExpression;
	protected boolean enabledVoidTags = true;
	protected boolean impliedEndTags;
	protected Logger.Level parsingErrorLogLevel = Logger.Level.WARN;

	protected boolean useFosterRules;
	protected boolean unclosedTagAsOrphanCheck;

	// ---------------------------------------------------------------- access

	public boolean isUnclosedTagAsOrphanCheck() {
		return unclosedTagAsOrphanCheck;
	}

	public LagartoDomBuilderConfig setUnclosedTagAsOrphanCheck(boolean unclosedTagAsOrphanCheck) {
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

	public LagartoDomBuilderConfig setUseFosterRules(boolean useFosterRules) {
		this.useFosterRules = useFosterRules;
		return this;
	}

	public boolean isIgnoreWhitespacesBetweenTags() {
		return ignoreWhitespacesBetweenTags;
	}

	/**
	 * Specifies if whitespaces between open/closed tags should be ignored.
	 */
	public LagartoDomBuilderConfig setIgnoreWhitespacesBetweenTags(boolean ignoreWhitespacesBetweenTags) {
		this.ignoreWhitespacesBetweenTags = ignoreWhitespacesBetweenTags;
		return this;
	}

	public boolean isIgnoreComments() {
		return ignoreComments;
	}

	/**
	 * Specifies if comments should be ignored in DOM tree.
	 */
	public LagartoDomBuilderConfig setIgnoreComments(boolean ignoreComments) {
		this.ignoreComments = ignoreComments;
		return this;
	}

	public boolean isEnabledVoidTags() {
		return enabledVoidTags;
	}

	/**
	 * Enables usage of void tags.
	 */
	public LagartoDomBuilderConfig setEnabledVoidTags(boolean enabledVoidTags) {
		this.enabledVoidTags = enabledVoidTags;
		return this;
	}

	public boolean isSelfCloseVoidTags() {
		return selfCloseVoidTags;
	}

	/**
	 * Specifies if void tags should be self closed.
	 */
	public LagartoDomBuilderConfig setSelfCloseVoidTags(boolean selfCloseVoidTags) {
		this.selfCloseVoidTags = selfCloseVoidTags;
		return this;
	}

	public boolean isCollectErrors() {
		return collectErrors;
	}

	/**
	 * Enables error collection during parsing.
	 */
	public LagartoDomBuilderConfig setCollectErrors(boolean collectErrors) {
		this.collectErrors = collectErrors;
		return this;
	}

	public String getConditionalCommentExpression() {
		return conditionalCommentExpression;
	}

	public LagartoDomBuilderConfig setConditionalCommentExpression(String conditionalCommentExpression) {
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
	public LagartoDomBuilderConfig setImpliedEndTags(boolean impliedEndTags) {
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
	public LagartoDomBuilderConfig setParsingErrorLogLevelName(String logLevel) {
		logLevel = logLevel.trim().toUpperCase();

		parsingErrorLogLevel = Logger.Level.valueOf(logLevel);

		return this;
	}

}