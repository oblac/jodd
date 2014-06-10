// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

/**
 * Configuration for {@link jodd.lagarto.LagartoParser}.
 * todo add fluent interface
 */
public class LagartoParserConfig {

	protected boolean parseXmlTags = false;
	protected boolean enableConditionalComments = true;
	protected boolean caseSensitive = false;
	protected boolean calculatePosition = false;
	protected boolean enableRawTextModes = true;

	public boolean isEnableConditionalComments() {
		return enableConditionalComments;
	}

	/**
	 * Enables detection of IE conditional comments. If not enabled,
	 * downlevel-hidden cond. comments will be treated as regular comment,
	 * while revealed cond. comments will be treated as an error.
	 */
	public void setEnableConditionalComments(boolean enableConditionalComments) {
		this.enableConditionalComments = enableConditionalComments;
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
	public void setParseXmlTags(boolean parseXmlTags) {
		this.parseXmlTags = parseXmlTags;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * Sets the case-sensitive flag for various matching.
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public boolean isCalculatePosition() {
		return calculatePosition;
	}

	/**
	 * Resolves current position on {@link #error(String)} parsing errors}
	 * and for DOM elements. Note: this makes processing SLOW!
	 * JFlex may be used to track current line and row, but that brings
	 * overhead, and can't be easily disabled. By enabling this property,
	 * position will be calculated manually only on errors.
	 */
	public void setCalculatePosition(boolean calculatePosition) {
		this.calculatePosition = calculatePosition;
	}

	public boolean isEnableRawTextModes() {
		return enableRawTextModes;
	}

	/**
	 * Enables RAW (CDATA) and RCDATA text mode while parsing.
	 */
	public void setEnableRawTextModes(boolean enableRawTextModes) {
		this.enableRawTextModes = enableRawTextModes;
	}

}