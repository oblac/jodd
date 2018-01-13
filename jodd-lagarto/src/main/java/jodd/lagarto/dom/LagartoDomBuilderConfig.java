// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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
	protected float condCommentIEVersion = 10;
	protected boolean enabledVoidTags = true;
	protected boolean impliedEndTags;
	protected Logger.Level parsingErrorLogLevel = Logger.Level.WARN;

	protected boolean useFosterRules;
	protected boolean unclosedTagAsOrphanCheck;

	protected LagartoHtmlRenderer lagartoHtmlRenderer = new LagartoHtmlRenderer();

	// ---------------------------------------------------------------- access

	public boolean isUnclosedTagAsOrphanCheck() {
		return unclosedTagAsOrphanCheck;
	}

	public LagartoDomBuilderConfig setUnclosedTagAsOrphanCheck(final boolean unclosedTagAsOrphanCheck) {
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

	public LagartoDomBuilderConfig setUseFosterRules(final boolean useFosterRules) {
		this.useFosterRules = useFosterRules;
		return this;
	}

	public boolean isIgnoreWhitespacesBetweenTags() {
		return ignoreWhitespacesBetweenTags;
	}

	/**
	 * Specifies if whitespaces between open/closed tags should be ignored.
	 */
	public LagartoDomBuilderConfig setIgnoreWhitespacesBetweenTags(final boolean ignoreWhitespacesBetweenTags) {
		this.ignoreWhitespacesBetweenTags = ignoreWhitespacesBetweenTags;
		return this;
	}

	public boolean isIgnoreComments() {
		return ignoreComments;
	}

	/**
	 * Specifies if comments should be ignored in DOM tree.
	 */
	public LagartoDomBuilderConfig setIgnoreComments(final boolean ignoreComments) {
		this.ignoreComments = ignoreComments;
		return this;
	}

	public boolean isEnabledVoidTags() {
		return enabledVoidTags;
	}

	/**
	 * Enables usage of void tags.
	 */
	public LagartoDomBuilderConfig setEnabledVoidTags(final boolean enabledVoidTags) {
		this.enabledVoidTags = enabledVoidTags;
		return this;
	}

	public boolean isSelfCloseVoidTags() {
		return selfCloseVoidTags;
	}

	/**
	 * Specifies if void tags should be self closed.
	 */
	public LagartoDomBuilderConfig setSelfCloseVoidTags(final boolean selfCloseVoidTags) {
		this.selfCloseVoidTags = selfCloseVoidTags;
		return this;
	}

	public boolean isCollectErrors() {
		return collectErrors;
	}

	/**
	 * Enables error collection during parsing.
	 */
	public LagartoDomBuilderConfig setCollectErrors(final boolean collectErrors) {
		this.collectErrors = collectErrors;
		return this;
	}

	public float getCondCommentIEVersion() {
		return condCommentIEVersion;
	}

	public LagartoDomBuilderConfig setCondCommentIEVersion(final float condCommentIEVersion) {
		this.condCommentIEVersion = condCommentIEVersion;
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
	public LagartoDomBuilderConfig setImpliedEndTags(final boolean impliedEndTags) {
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

	public LagartoHtmlRenderer getLagartoHtmlRenderer() {
		return lagartoHtmlRenderer;
	}

	public void setLagartoHtmlRenderer(final LagartoHtmlRenderer lagartoHtmlRenderer) {
		this.lagartoHtmlRenderer = lagartoHtmlRenderer;
	}
}