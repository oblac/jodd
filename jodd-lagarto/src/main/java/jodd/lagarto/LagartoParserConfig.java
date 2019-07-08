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

package jodd.lagarto;

/**
 * Configuration for {@link jodd.lagarto.LagartoParser}.
 */
public class LagartoParserConfig<T extends LagartoParserConfig<T>> {

	protected boolean parseXmlTags = false;
	protected boolean enableConditionalComments = true;
	protected boolean caseSensitive = false;
	protected boolean calculatePosition = false;
	protected boolean enableRawTextModes = true;

	public boolean isEnableConditionalComments() {
		return enableConditionalComments;
	}

	@SuppressWarnings("unchecked")
	protected T _this() {
		return (T) this;
	}

	/**
	 * Enables detection of IE conditional comments. If not enabled,
	 * downlevel-hidden cond. comments will be treated as regular comment,
	 * while revealed cond. comments will be treated as an error.
	 */
	public T setEnableConditionalComments(final boolean enableConditionalComments) {
		this.enableConditionalComments = enableConditionalComments;
		return _this();
	}

	/**
	 * Returns <code>true</code> if parsing of XML tags is enabled.
	 */
	public boolean isParseXmlTags() {
		return parseXmlTags;
	}

	/**
	 * Enables parsing of XML tags.
	 */
	public T setParseXmlTags(final boolean parseXmlTags) {
		this.parseXmlTags = parseXmlTags;
		return _this();
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * Sets the case-sensitive flag for various matching.
	 */
	public T setCaseSensitive(final boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
		return _this();
	}

	public boolean isCalculatePosition() {
		return calculatePosition;
	}

	/**
	 * Resolves current position on parsing errors
	 * and for DOM elements. Note: this makes processing SLOW!
	 * JFlex may be used to track current line and row, but that brings
	 * overhead, and can't be easily disabled. By enabling this property,
	 * position will be calculated manually only on errors.
	 */
	public T setCalculatePosition(final boolean calculatePosition) {
		this.calculatePosition = calculatePosition;
		return _this();
	}

	public boolean isEnableRawTextModes() {
		return enableRawTextModes;
	}

	/**
	 * Enables RAW (CDATA) and RCDATA text mode while parsing.
	 */
	public T setEnableRawTextModes(final boolean enableRawTextModes) {
		this.enableRawTextModes = enableRawTextModes;
		return _this();
	}

}