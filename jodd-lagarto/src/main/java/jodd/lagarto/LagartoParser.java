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

import jodd.util.ArraysUtil;
import jodd.util.CharArraySequence;
import jodd.util.CharUtil;
import jodd.util.StringPool;
import jodd.util.UnsafeUtil;
import jodd.net.HtmlDecoder;

import static jodd.util.CharUtil.equalsOne;
import static jodd.util.CharUtil.isAlpha;
import static jodd.util.CharUtil.isDigit;

/**
 * HTML/XML content parser/tokenizer using {@link TagVisitor} for callbacks.
 * Works by the HTML5 specs for tokenization, as described
 * on <a href="http://www.whatwg.org/specs/web-apps/current-work/multipage/tokenization.html">WhatWG</a>.
 * Differences from the specs:
 *
 * <ul>
 * <li>text is emitted as a block of text, and not character by character.</li>
 * <li>tags name case (and letter case of other entities) is not changed, but case-sensitive
 * information exist for matching.
 * <li>the whole tokenization process is implemented here, without going into the tree building.
 * This applies for switching to the RAWTEXT state.
 * </li>
 * <li>script tag is emitted separately</li>
 * <li>conditional comments added</li>
 * <li>xml states and callbacks added</li>
 * </ul>
 *
 * <p>
 * There are two ways how text is passed back to the visitor.
 * By default it is passed as <code>CharBuffer</code>, which
 * gives excellent performances. However, if you need more <code>Strings</code>
 * than enable it, and all text will be strings. This is faster
 * then first converting to char buffer and then to strings.
 */
public class LagartoParser extends Scanner {

	protected TagVisitor visitor;
	protected ParsedTag tag;
	protected ParsedDoctype doctype;
	protected long parsingTime;

	/**
	 * Creates parser on char array.
	 */
	public LagartoParser(final char[] charArray) {
		initialize(charArray);
	}

	/**
	 * Creates parser on a String.
	 */
	public LagartoParser(final String string) {
		initialize(UnsafeUtil.getChars(string));
	}

	/**
	 * Initializes parser.
	 */
	@Override
	protected void initialize(final char[] input) {
		super.initialize(input);
		this.tag = new ParsedTag();
		this.doctype = new ParsedDoctype();
		this.text = new char[1024];
		this.textLen = 0;
		this.parsingTime = -1;
	}

	// ---------------------------------------------------------------- configuration

	protected LagartoParserConfig config = new LagartoParserConfig();

	/**
	 * Returns {@link jodd.lagarto.LagartoParserConfig configuration} for the parser.
	 */
	public LagartoParserConfig getConfig() {
		return config;
	}

	/**
	 * Sets parser configuration.
	 */
	public void setConfig(final LagartoParserConfig config) {
		this.config = config;
	}

	// ---------------------------------------------------------------- parse

	protected boolean parsing;

	/**
	 * Parses content and callback provided {@link TagVisitor}.
	 */
	public void parse(final TagVisitor visitor) {
		tag.init(config.caseSensitive);

		this.parsingTime = System.currentTimeMillis();

		this.visitor = visitor;

		visitor.start();

		parsing = true;

		while (parsing) {
			state.parse();
		}

		emitText();

		visitor.end();

		this.parsingTime = System.currentTimeMillis() - parsingTime;
	}

	/**
	 * Returns parsing time in milliseconds.
	 */
	public long getParsingTime() {
		return parsingTime;
	}

	// ---------------------------------------------------------------- start & end

	/**
	 * Data state.
	 */
	protected State DATA_STATE =  new State() {
		@Override
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					emitText();
					parsing = false;
					return;
				}

				char c = input[ndx];

				if (c == '<') {
					emitText();
					state = TAG_OPEN;
					return;
				}

				if (c == '&') {
					consumeCharacterReference();
					continue;
				}

				textEmitChar(c);
			}
		}
	};

	protected void consumeCharacterReference(final char allowedChar) {
		ndx++;

		if (isEOF()) {
			return;
		}

		char c = input[ndx];

		if (c == allowedChar) {
			ndx--;
			return;
		}

		_consumeAttrCharacterReference();
	}

	protected void consumeCharacterReference() {
		ndx++;

		if (isEOF()) {
			return;
		}

		_consumeCharacterReference();
	}

	private void _consumeCharacterReference() {
		int unconsumeNdx = ndx - 1;

		char c = input[ndx];

		if (equalsOne(c, CONTINUE_CHARS)) {
			ndx = unconsumeNdx;
			textEmitChar('&');
			return;
		}

		if (c == '#') {
			_consumeNumber(unconsumeNdx);
		} else {
			String name = HtmlDecoder.detectName(input, ndx);

			if (name == null) {
				// this error is not quite as by the spec. The spec says that
				// only a sequence of alphanumeric chars ending with semicolon
				// gives na error
				errorCharReference();
				textEmitChar('&');
				ndx = unconsumeNdx;
				return;
			}

			// missing legacy attribute thing

			ndx += name.length();

			textEmitChars(HtmlDecoder.lookup(name));

			c = input[ndx];

			if (c != ';') {
				errorCharReference();
				ndx--;
			}
		}
	}

	private void _consumeAttrCharacterReference() {
		final int unconsumeNdx = ndx - 1;

		char c = input[ndx];

		if (equalsOne(c, CONTINUE_CHARS)) {
			ndx = unconsumeNdx;
			textEmitChar('&');
			return;
		}

		if (c == '#') {
			_consumeNumber(unconsumeNdx);
		} else {
			final String name = HtmlDecoder.detectName(input, ndx);

			if (name == null) {
				// this error is not quite as by the spec. The spec says that
				// only a sequence of alphanumeric chars ending with semicolon
				// gives na error
				errorCharReference();
				textEmitChar('&');
				ndx = unconsumeNdx;
				return;
			}

			// missing legacy attribute thing

			ndx += name.length();
			c = input[ndx];

			if (c == ';') {
				textEmitChars(HtmlDecoder.lookup(name));
			} else {
				textEmitChar('&');
				ndx = unconsumeNdx;
			}
		}
	}

	private void _consumeNumber(final int unconsumeNdx) {
		ndx++;

		if (isEOF()) {
			ndx = unconsumeNdx;
			return;
		}

		char c = input[ndx];

		int value = 0;
		int digitCount = 0;

		if (c == 'X' || c == 'x') {
			while (true) {
				ndx++;

				if (isEOF()) {
					ndx = unconsumeNdx;
					return;
				}

				c = input[ndx];

				if (isDigit(c)) {
					value *= 16;
					value += c - '0';
					digitCount++;
				} else if ((c >= 'a') && (c <= 'f')) {
					value *= 16;
					value += c - 'a' + 10;
					digitCount++;
				} else if ((c >= 'A') && (c <= 'F')) {
					value *= 16;
					value += c - 'A' + 10;
					digitCount++;
				} else {
					break;
				}
			}
		} else {
			while (isDigit(c)) {
				value *= 10;
				value += c - '0';

				ndx++;

				if (isEOF()) {
					ndx = unconsumeNdx;
					return;
				}

				c = input[ndx];
				digitCount++;
			}
		}

		if (digitCount == 0) {
			// no character matches the range
			errorCharReference();
			ndx = unconsumeNdx;
			return;
		}

		if (c != ';') {
			errorCharReference();
			ndx--;	// go back, as pointer is on the next char
		}

		boolean isErr = true;
		switch (value) {
			case 0: c = REPLACEMENT_CHAR; break;
			case 0x80: c = '\u20AC'; break;
			case 0x81: c = '\u0081'; break;
			case 0x82: c = '\u201A'; break;
			case 0x83: c = '\u0192'; break;
			case 0x84: c = '\u201E'; break;
			case 0x85: c = '\u2026'; break;
			case 0x86: c = '\u2020'; break;
			case 0x87: c = '\u2021'; break;
			case 0x88: c = '\u02C6'; break;
			case 0x89: c = '\u2030'; break;
			case 0x8A: c = '\u0160'; break;
			case 0x8B: c = '\u2039'; break;
			case 0x8C: c = '\u0152'; break;
			case 0x8D: c = '\u008D'; break;
			case 0x8E: c = '\u017D'; break;
			case 0x8F: c = '\u008F'; break;
			case 0x90: c = '\u0090'; break;
			case 0x91: c = '\u2018'; break;
			case 0x92: c = '\u2019'; break;
			case 0x93: c = '\u201C'; break;
			case 0x94: c = '\u201D'; break;
			case 0x95: c = '\u2022'; break;
			case 0x96: c = '\u2013'; break;
			case 0x97: c = '\u2014'; break;
			case 0x98: c = '\u02DC'; break;
			case 0x99: c = '\u2122'; break;
			case 0x9A: c = '\u0161'; break;
			case 0x9B: c = '\u203A'; break;
			case 0x9C: c = '\u0153'; break;
			case 0x9D: c = '\u009D'; break;
			case 0x9E: c = '\u017E'; break;
			case 0x9F: c = '\u0178'; break;
			default:
				isErr = false;
		}

		if (isErr) {
			errorCharReference();
			textEmitChar(c);
			return;
		}

		if (((value >= 0xD800) && (value <= 0xDFF)) || (value > 0x10FFFF)) {
			errorCharReference();
			textEmitChar(REPLACEMENT_CHAR);
			return;
		}

		c = (char) value;

		textEmitChar(c);

		if (
			((c >= 0x0001) && (c <= 0x0008)) ||
			((c >= 0x000D) && (c <= 0x001F)) ||
			((c >= 0x007F) && (c <= 0x009F)) ||
			((c >= 0xFDD0) && (c <= 0xFDEF))
		) {
			errorCharReference();
			return;
		}

		if (equalsOne(c, INVALID_CHARS)) {
			errorCharReference();
		}
	}

	protected State TAG_OPEN = new State() {
		@Override
		public void parse() {
			tag.start(ndx);

			ndx++;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				textEmitChar('<');
				return;
			}

			char c = input[ndx];

			if (c == '!') {
				state = MARKUP_DECLARATION_OPEN;
				return;
			}
			if (c == '/') {
				state = END_TAG_OPEN;
				return;
			}
			if (isAlpha(c)) {
				state = TAG_NAME;
				return;
			}
			if (config.parseXmlTags) {
				if (match(XML)) {
					ndx += XML.length - 1;
					if (xmlDeclaration == null) {
						xmlDeclaration = new XmlDeclaration();
					}
					state = xmlDeclaration.XML_BETWEEN;
					return;
				}
			}
			if (c == '?') {
				errorInvalidToken();
				state = BOGUS_COMMENT;
				return;
			}

			errorInvalidToken();
			state = DATA_STATE;
			textEmitChar('<');

			ndx--;
		}
	};

	protected State END_TAG_OPEN = new State() {
		@Override
		public void parse() {
			ndx++;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				return;
			}

			char c = input[ndx];

			if (isAlpha(c)) {
				tag.setType(TagType.END);
				state = TAG_NAME;
				return;
			}

			errorInvalidToken();
			state = BOGUS_COMMENT;
		}
	};

	protected State TAG_NAME = new State() {
		@Override
		public void parse() {
			int nameNdx = ndx;

			while (true) {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					return;
				}

				char c = input[ndx];

				if (equalsOne(c, TAG_WHITESPACES)) {
					state = BEFORE_ATTRIBUTE_NAME;
					tag.setName(charSequence(nameNdx, ndx));
					break;
				}

				if (c == '/') {
					state = SELF_CLOSING_START_TAG;
					tag.setName(charSequence(nameNdx, ndx));
					break;
				}

				if (c == '>') {
					state = DATA_STATE;
					tag.setName(charSequence(nameNdx, ndx));
					emitTag();
					break;
				}
			}
		}
	};

	protected State BEFORE_ATTRIBUTE_NAME = new State() {
		@Override
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					return;
				}

				char c = input[ndx];

				if (equalsOne(c, TAG_WHITESPACES)) {
					continue;
				}

				if (c == '/') {
					state = SELF_CLOSING_START_TAG;
					return;
				}

				if (c == '>') {
					state = DATA_STATE;
					emitTag();
					return;
				}

				if (equalsOne(c, ATTR_INVALID_1)) {
					errorInvalidToken();
				}

				state = ATTRIBUTE_NAME;
				return;
			}
		}
	};

	protected State ATTRIBUTE_NAME = new State() {
		@Override
		public void parse() {
			attrStartNdx = ndx;

			while (true) {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					return;
				}

				char c = input[ndx];

				if (equalsOne(c, TAG_WHITESPACES)) {
					attrEndNdx = ndx;
					state = AFTER_ATTRIBUTE_NAME;
					return;
				}

				if (c == '/') {
					attrEndNdx = ndx;
					_addAttribute();
					state = SELF_CLOSING_START_TAG;
					return;
				}

				if (c == '=') {
					attrEndNdx = ndx;
					state = BEFORE_ATTRIBUTE_VALUE;
					return;
				}

				if (c == '>') {
					state = DATA_STATE;
					attrEndNdx = ndx;
					_addAttribute();
					emitTag();
					return;
				}

				if (equalsOne(c, ATTR_INVALID_2)) {
					errorInvalidToken();
				}
			}
		}
	};

	protected State AFTER_ATTRIBUTE_NAME = new State() {
		@Override
		public void parse() {
			while(true) {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					return;
				}

				char c = input[ndx];

				if (equalsOne(c, TAG_WHITESPACES)) {
					continue;
				}

				if (c == '/') {
					state = SELF_CLOSING_START_TAG;
					return;
				}
				if (c == '=') {
					state = BEFORE_ATTRIBUTE_VALUE;
					return;
				}
				if (c == '>') {
					state = DATA_STATE;
					emitTag();
					return;
				}
				if (equalsOne(c, ATTR_INVALID_2)) {
					errorInvalidToken();
				}

				_addAttribute();
				state = ATTRIBUTE_NAME;
				return;
			}
		}
	};

	protected State BEFORE_ATTRIBUTE_VALUE = new State() {
		@Override
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					return;
				}

				char c = input[ndx];

				if (equalsOne(c, TAG_WHITESPACES)) {
					continue;
				}

				if (c == '\"') {
					state = ATTR_VALUE_DOUBLE_QUOTED;
					return;
				}
				if (c == '\'') {
					state = ATTR_VALUE_SINGLE_QUOTED;
					return;
				}
				if (c == '&') {
					state = ATTR_VALUE_UNQUOTED;
					ndx--;
					return;
				}
				if (c == '>') {
					_addAttribute();
					errorInvalidToken();
					state = DATA_STATE;
					emitTag();
					return;
				}
				if (equalsOne(c, ATTR_INVALID_3)) {
					errorInvalidToken();
				}

				state = ATTR_VALUE_UNQUOTED;
				return;
			}
		}
	};

	protected State ATTR_VALUE_UNQUOTED = new State() {
		@Override
		public void parse() {
			textStart();
			textEmitChar(input[ndx]);

			while (true) {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					return;
				}

				char c = input[ndx];

				if (equalsOne(c, TAG_WHITESPACES)) {
					_addAttributeWithValue();
					state = BEFORE_ATTRIBUTE_NAME;
					return;
				}

				if (c == '&') {
					consumeCharacterReference('>');
					continue;
				}

				if (c == '>') {
					_addAttributeWithValue();
					state = DATA_STATE;
					emitTag();
					return;
				}

				if (equalsOne(c, ATTR_INVALID_4)) {
					errorInvalidToken();
				}

				textEmitChar(c);
			}
		}
	};

	protected State ATTR_VALUE_SINGLE_QUOTED = new State() {
		@Override
		public void parse() {
			textStart();

			while (true) {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					return;
				}

				char c = input[ndx];

				if (c == '\'') {
					_addAttributeWithValue();
					state = AFTER_ATTRIBUTE_VALUE_QUOTED;
					return;
				}
				if (c == '&') {
					consumeCharacterReference('\'');
					continue;
				}

				textEmitChar(c);
			}
		}
	};

	protected State ATTR_VALUE_DOUBLE_QUOTED = new State() {
		@Override
		public void parse() {
			textStart();
			while (true) {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					return;
				}

				char c = input[ndx];

				if (c == '"') {
					_addAttributeWithValue();
					state = AFTER_ATTRIBUTE_VALUE_QUOTED;
					return;
				}

				if (c == '&') {
					consumeCharacterReference('\"');
					continue;
				}

				textEmitChar(c);
			}
		}
	};

	protected State AFTER_ATTRIBUTE_VALUE_QUOTED = new State() {
		@Override
		public void parse() {
			ndx++;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				return;
			}

			char c = input[ndx];

			if (equalsOne(c, TAG_WHITESPACES)) {
				state = BEFORE_ATTRIBUTE_NAME;
				return;
			}

			if (c == '/') {
				state = SELF_CLOSING_START_TAG;
				return;
			}

			if (c == '>') {
				state = DATA_STATE;
				emitTag();
				return;
			}

			errorInvalidToken();
			state = BEFORE_ATTRIBUTE_NAME;
			ndx--;
		}
	};

	protected State SELF_CLOSING_START_TAG = new State() {
		@Override
		public void parse() {
			ndx++;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				return;
			}

			char c = input[ndx];

			if (c == '>') {
				tag.setType(TagType.SELF_CLOSING);
				state = DATA_STATE;
				emitTag();
				return;
			}

			errorInvalidToken();

			state = BEFORE_ATTRIBUTE_NAME;
			ndx--;
		}
	};

	// ---------------------------------------------------------------- special

	protected State BOGUS_COMMENT = new State() {
		@Override
		public void parse() {
			int commentEndNdx = find('>', ndx, total);

			if (commentEndNdx == -1) {
				commentEndNdx = total;
			}

			emitComment(ndx, commentEndNdx);

			state = DATA_STATE;
			ndx = commentEndNdx;
		}
	};

	protected State MARKUP_DECLARATION_OPEN = new State() {
		@Override
		public void parse() {
			ndx++;

			if (isEOF()) {
				errorEOF();
				state = BOGUS_COMMENT;
				return;
			}

			if (match(COMMENT_DASH)) {
				state = COMMENT_START;
				ndx++;
				return;
			}

			if (matchUpperCase(T_DOCTYPE)) {
				state = DOCTYPE;
				ndx += T_DOCTYPE.length - 1;
				return;
			}

			if (config.enableConditionalComments) {
				// CC: downlevel-revealed starting
				if (match(CC_IF)) {
					int ccEndNdx = find(CC_END, ndx + CC_IF.length, total);

					if (ccEndNdx == -1) {
						ccEndNdx = total;
					}

					CharSequence expression = charSequence(ndx + 1, ccEndNdx);

					visitor.condComment(expression, true, false, false);

					ndx = ccEndNdx + 1;
					state = DATA_STATE;
					return;
				}

				// CC: downlevel-* ending tag
				if (match(CC_ENDIF)) {
					ndx += CC_ENDIF.length;

					int ccEndNdx = find('>', ndx, total);

					if (ccEndNdx == -1) {
						ccEndNdx = total;
					}

					if (match(COMMENT_DASH, ccEndNdx - 2)) {
						// downlevel-hidden ending tag
						visitor.condComment(_ENDIF, false, true, false);
					} else {
						visitor.condComment(_ENDIF, false, false, false);
					}

					ndx = ccEndNdx;
					state = DATA_STATE;
					return;
				}
			}

			if (config.parseXmlTags) {
				if (match(CDATA)) {
					ndx += CDATA.length - 1;

					if (xmlDeclaration == null) {
						xmlDeclaration = new XmlDeclaration();
					}

					state = xmlDeclaration.CDATA;
					return;
				}
			}

			errorInvalidToken();
			state = BOGUS_COMMENT;
		}
	};

	// ---------------------------------------------------------------- RAWTEXT

	protected int rawTextStart;
	protected int rawTextEnd;
	protected char[] rawTagName;

	protected State RAWTEXT = new State() {
		@Override
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					state = DATA_STATE;
					return;
				}

				char c = input[ndx];

				if (c == '<') {
					rawTextEnd = ndx;
					state = RAWTEXT_LESS_THAN_SIGN;
					return;
				}
			}
		}
	};

	protected State RAWTEXT_LESS_THAN_SIGN = new State() {
		@Override
		public void parse() {
			ndx++;

			if (isEOF()) {
				state =  RAWTEXT;
				return;
			}

			char c = input[ndx];

			if (c == '/') {
				state = RAWTEXT_END_TAG_OPEN;
				return;
			}

			state = RAWTEXT;
		}
	};

	protected State RAWTEXT_END_TAG_OPEN = new State() {
		@Override
		public void parse() {
			ndx++;

			if (isEOF()) {
				state = RAWTEXT;
				return;
			}

			char c = input[ndx];

			if (isAlpha(c)) {
				state = RAWTEXT_END_TAG_NAME;
				return;
			}

			state = RAWTEXT;
		}
	};

	protected State RAWTEXT_END_TAG_NAME = new State() {
		@Override
		public void parse() {
			int rawtextEndTagNameStartNdx = ndx;

			while (true) {
				ndx++;

				if (isEOF()) {
					state = RAWTEXT;
					return;
				}

				char c = input[ndx];

				if (equalsOne(c, TAG_WHITESPACES)) {
					if (isAppropriateTagName(rawTagName, rawtextEndTagNameStartNdx, ndx)) {
						textEmitChars(rawTextStart, rawTextEnd);
						emitText();

						state = BEFORE_ATTRIBUTE_NAME;
						tag.start(rawTextEnd);
						tag.setName(charSequence(rawtextEndTagNameStartNdx, ndx));
						tag.setType(TagType.END);
					} else {
						state = RAWTEXT;
					}
					return;
				}

				if (c == '/') {
					if (isAppropriateTagName(rawTagName, rawtextEndTagNameStartNdx, ndx)) {
						textEmitChars(rawTextStart, rawTextEnd);
						emitText();

						state = SELF_CLOSING_START_TAG;
						tag.start(rawTextEnd);
						tag.setName(charSequence(rawtextEndTagNameStartNdx, ndx));
						tag.setType(TagType.SELF_CLOSING);
					} else {
						state = RAWTEXT;
					}
					return;
				}

				if (c == '>') {
					if (isAppropriateTagName(rawTagName, rawtextEndTagNameStartNdx, ndx)) {
						textEmitChars(rawTextStart, rawTextEnd);
						emitText();

						state = DATA_STATE;
						tag.start(rawTextEnd);
						tag.setName(charSequence(rawtextEndTagNameStartNdx, ndx));
						tag.setType(TagType.END);
						tag.end(ndx);
						emitTag();
					} else {
						state = RAWTEXT;
					}
					return;
				}
				if (isAlpha(c)) {
					continue;
				}

				state = RAWTEXT;
				return;
			}
		}
	};

	// ---------------------------------------------------------------- RCDATA

	protected int rcdataTagStart = -1;
	protected char[] rcdataTagName;

	protected State RCDATA = new State() {
		@Override
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					state = DATA_STATE;
					return;
				}

				char c = input[ndx];

				if (c == '<') {
					rcdataTagStart = ndx;
					state = RCDATA_LESS_THAN_SIGN;
					return;
				}

				if (c == '&') {
					consumeCharacterReference();
					continue;
				}

				textEmitChar(c);
			}
		}
	};

	protected State RCDATA_LESS_THAN_SIGN = new State() {
		@Override
		public void parse() {
			ndx++;

			if (isEOF()) {
				state = RCDATA;
				return;
			}

			char c = input[ndx];

			if (c == '/') {
				state = RCDATA_END_TAG_OPEN;
				return;
			}

			state = RCDATA;
			textEmitChar('<');
			textEmitChar(c);
		}
	};

	protected State RCDATA_END_TAG_OPEN = new State() {
		@Override
		public void parse() {
			ndx++;

			if (isEOF()) {
				state = RCDATA;
				return;
			}

			char c = input[ndx];

			if (isAlpha(c)) {
				state = RCDATA_END_TAG_NAME;
				return;
			}

			state = RCDATA;
			textEmitChar('<');
			textEmitChar('/');
			textEmitChar(c);
		}
	};

	protected State RCDATA_END_TAG_NAME = new State() {
		@Override
		public void parse() {
			int rcdataEndTagNameStartNdx = ndx;

			while (true) {
				ndx++;

				if (isEOF()) {
					state = RCDATA;
					return;
				}

				char c = input[ndx];

				if (equalsOne(c, TAG_WHITESPACES)) {
					if (isAppropriateTagName(rcdataTagName, rcdataEndTagNameStartNdx, ndx)) {
						emitText();

						state = BEFORE_ATTRIBUTE_NAME;
						tag.start(rcdataTagStart);
						tag.setName(charSequence(rcdataEndTagNameStartNdx, ndx));
						tag.setType(TagType.END);
					} else {
						state = RCDATA;
					}
					return;
				}

				if (c == '/') {
					if (isAppropriateTagName(rcdataTagName, rcdataEndTagNameStartNdx, ndx)) {
						emitText();

						state = SELF_CLOSING_START_TAG;
						tag.start(rcdataTagStart);
						tag.setName(charSequence(rcdataEndTagNameStartNdx, ndx));
						tag.setType(TagType.SELF_CLOSING);
					} else {
						state = RCDATA;
					}
					return;
				}

				if (c == '>') {
					if (isAppropriateTagName(rcdataTagName, rcdataEndTagNameStartNdx, ndx)) {
						emitText();

						state = DATA_STATE;
						tag.start(rcdataTagStart);
						tag.setName(charSequence(rcdataEndTagNameStartNdx, ndx));
						tag.setType(TagType.END);
						tag.end(ndx);
						emitTag();
					} else {
						state = RCDATA;
					}
					return;
				}

				if (isAlpha(c)) {
					continue;
				}

				state = RCDATA;
				return;
			}
		}
	};

	// ---------------------------------------------------------------- comments

	protected int commentStart;

	protected State COMMENT_START = new State() {
		@Override
		public void parse() {
			ndx++;
			commentStart = ndx;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				emitComment(commentStart, total);
				return;
			}

			char c = input[ndx];

			if (c == '-') {
				state = COMMENT_START_DASH;
				return;
			}

			if (c == '>') {
				errorInvalidToken();
				state = DATA_STATE;
				emitComment(commentStart, ndx);
				return;
			}

			state = COMMENT;
		}
	};

	protected State COMMENT_START_DASH = new State() {
		@Override
		public void parse() {
			ndx++;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				emitComment(commentStart, total);
				return;
			}

			char c = input[ndx];

			if (c == '-') {
				state = COMMENT_END;
				return;
			}
			if (c == '>') {
				errorInvalidToken();
				state = DATA_STATE;
				emitComment(commentStart, ndx);
			}

			state = COMMENT;
		}
	};

	protected State COMMENT = new State() {
		@Override
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					emitComment(commentStart, total);
					return;
				}

				char c = input[ndx];

				if (c == '-') {
					state = COMMENT_END_DASH;
					return;
				}
			}
		}
	};

	protected State COMMENT_END_DASH = new State() {
		@Override
		public void parse() {
			ndx++;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				emitComment(commentStart, total);
				return;
			}

			char c = input[ndx];

			if (c == '-') {
				state = COMMENT_END;
				return;
			}

			state = COMMENT;
		}
	};

	protected State COMMENT_END = new State() {
		@Override
		public void parse() {
			ndx++;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				emitComment(commentStart, total);
				return;
			}

			char c = input[ndx];

			if (c == '>') {
				state = DATA_STATE;
				emitComment(commentStart, ndx - 2);
				return;
			}

			if (c == '!') {
				errorInvalidToken();
				state = COMMENT_END_BANG;
				return;
			}

			if (c == '-') {
				errorInvalidToken();
			} else {
				errorInvalidToken();
				state = COMMENT;
			}
		}
	};

	protected State COMMENT_END_BANG = new State() {
		@Override
		public void parse() {
			ndx++;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				emitComment(commentStart, total);
				return;
			}

			char c = input[ndx];

			if (c == '-') {
				state = COMMENT_END_DASH;
				return;
			}
			if (c == '>') {
				state = DATA_STATE;
				emitComment(commentStart, ndx - 3);
				return;
			}
			state = COMMENT;
		}
	};

	// ---------------------------------------------------------------- DOCTYPE

	protected State DOCTYPE = new State() {
		@Override
		public void parse() {
			ndx++;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				doctype.setQuirksMode(true);
				emitDoctype();
				return;
			}

			char c = input[ndx];

			if (equalsOne(c, TAG_WHITESPACES)) {
				state = BEFORE_DOCTYPE_NAME;
				return;
			}

			errorInvalidToken();
			state = BEFORE_DOCTYPE_NAME;
			ndx--;
		}
	};

	protected State BEFORE_DOCTYPE_NAME = new State() {
		@Override
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					doctype.setQuirksMode(true);
					emitDoctype();
					return;
				}

				char c = input[ndx];

				if (equalsOne(c, TAG_WHITESPACES)) {
					continue;
				}

				if (c == '>') {
					errorInvalidToken();
					state = DATA_STATE;
					doctype.setQuirksMode(true);
					emitDoctype();
					return;
				}

				state = DOCTYPE_NAME;
				return;
			}
		}
	};

	protected State DOCTYPE_NAME = new State() {
		@Override
		public void parse() {
			int nameStartNdx = ndx;

			while (true) {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					doctype.setName(charSequence(nameStartNdx, ndx));
					doctype.setQuirksMode(true);
					emitDoctype();
					return;
				}

				char c = input[ndx];

				if (equalsOne(c, TAG_WHITESPACES)) {
					state = AFTER_DOCUMENT_NAME;
					doctype.setName(charSequence(nameStartNdx, ndx));
					return;
				}

				if (c == '>') {
					state = DATA_STATE;
					doctype.setName(charSequence(nameStartNdx, ndx));
					emitDoctype();
					return;
				}
			}
		}
	};

	protected State AFTER_DOCUMENT_NAME = new State() {
		@Override
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					doctype.setQuirksMode(true);
					emitDoctype();
					return;
				}

				char c = input[ndx];

				if (equalsOne(c, TAG_WHITESPACES)) {
					continue;
				}

				if (c == '>') {
					state = DATA_STATE;
					emitDoctype();
					return;
				}

				if (matchUpperCase(A_PUBLIC)) {
					ndx += A_PUBLIC.length - 1;
					state = AFTER_DOCTYPE_PUBLIC_KEYWORD;
					return;
				}
				if (matchUpperCase(A_SYSTEM)) {
					ndx += A_SYSTEM.length - 1;
					state = AFTER_DOCTYPE_SYSTEM_KEYWORD;
					return;
				}

				errorInvalidToken();
				state = BOGUS_DOCTYPE;
				doctype.setQuirksMode(true);
				return;
			}
		}
	};

	protected int doctypeIdNameStart;

	protected State AFTER_DOCTYPE_PUBLIC_KEYWORD = new State() {
		@Override
		public void parse() {
			ndx++;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				doctype.setQuirksMode(true);
				emitDoctype();
				return;
			}

			char c = input[ndx];

			if (equalsOne(c, TAG_WHITESPACES)) {
				state = BEFORE_DOCTYPE_PUBLIC_IDENTIFIER;
				return;
			}

			if (c == '\"') {
				errorInvalidToken();
				doctypeIdNameStart = ndx + 1;
				state = DOCTYPE_PUBLIC_IDENTIFIER_DOUBLE_QUOTED;
				return;
			}

			if (c == '\'') {
				errorInvalidToken();
				doctypeIdNameStart = ndx + 1;
				state = DOCTYPE_PUBLIC_IDENTIFIER_SINGLE_QUOTED;
				return;
			}

			if (c == '>') {
				errorInvalidToken();
				state = DATA_STATE;
				doctype.setQuirksMode(true);
				emitDoctype();
				return;
			}

			errorInvalidToken();
			state = BOGUS_DOCTYPE;
			doctype.setQuirksMode(true);
		}
	};

	protected State BEFORE_DOCTYPE_PUBLIC_IDENTIFIER = new State() {
		@Override
		public void parse() {
			while(true) {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					emitDoctype();
					return;
				}

				char c = input[ndx];

				if (equalsOne(c, TAG_WHITESPACES)) {
					continue;
				}

				if (c == '\"') {
					doctypeIdNameStart = ndx + 1;
					state = DOCTYPE_PUBLIC_IDENTIFIER_DOUBLE_QUOTED;
					return;
				}

				if (c == '\'') {
					doctypeIdNameStart = ndx + 1;
					state = DOCTYPE_PUBLIC_IDENTIFIER_SINGLE_QUOTED;
					return;
				}

				if (c == '>') {
					errorInvalidToken();
					state = DATA_STATE;
					doctype.setQuirksMode(true);
					emitDoctype();
					return;
				}

				errorInvalidToken();
				doctype.setQuirksMode(true);
				state = BOGUS_DOCTYPE;
				return;
			}
		}
	};

	protected State DOCTYPE_PUBLIC_IDENTIFIER_DOUBLE_QUOTED = new State() {
		@Override
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					doctype.setPublicIdentifier(charSequence(doctypeIdNameStart, ndx));
					errorEOF();
					state = DATA_STATE;
					doctype.setQuirksMode(true);
					emitDoctype();
				}

				char c = input[ndx];

				if (c == '\"') {
					doctype.setPublicIdentifier(charSequence(doctypeIdNameStart, ndx));
					state = AFTER_DOCTYPE_PUBLIC_IDENTIFIER;
					return;
				}

				if (c == '>') {
					doctype.setPublicIdentifier(charSequence(doctypeIdNameStart, ndx));
					errorInvalidToken();
					state = DATA_STATE;
					doctype.setQuirksMode(true);
					emitDoctype();
					return;
				}
			}
		}
	};

	protected State DOCTYPE_PUBLIC_IDENTIFIER_SINGLE_QUOTED = new State() {
		@Override
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					doctype.setPublicIdentifier(charSequence(doctypeIdNameStart, ndx));
					errorEOF();
					state = DATA_STATE;
					doctype.setQuirksMode(true);
					emitDoctype();
				}

				char c = input[ndx];

				if (c == '\'') {
					doctype.setPublicIdentifier(charSequence(doctypeIdNameStart, ndx));
					state = AFTER_DOCTYPE_PUBLIC_IDENTIFIER;
					return;
				}

				if (c == '>') {
					doctype.setPublicIdentifier(charSequence(doctypeIdNameStart, ndx));
					errorInvalidToken();
					state = DATA_STATE;
					doctype.setQuirksMode(true);
					emitDoctype();
					return;
				}
			}
		}
	};

	protected State AFTER_DOCTYPE_PUBLIC_IDENTIFIER = new State() {
		@Override
		public void parse() {
			ndx++;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				doctype.setQuirksMode(true);
				emitDoctype();
				return;
			}

			char c = input[ndx];

			if (equalsOne(c, TAG_WHITESPACES)) {
				state = BETWEEN_DOCTYPE_PUBLIC_AND_SYSTEM_IDENTIFIERS;
				return;
			}

			if (c == '>') {
				state = DATA_STATE;
				emitDoctype();
				return;
			}

			if (c == '\"') {
				errorInvalidToken();
				doctypeIdNameStart = ndx + 1;
				state = DOCTYPE_SYSTEM_IDENTIFIER_DOUBLE_QUOTED;
				return;
			}

			if (c == '\'') {
				errorInvalidToken();
				doctypeIdNameStart = ndx + 1;
				state = DOCTYPE_SYSTEM_IDENTIFIER_SINGLE_QUOTED;
				return;
			}

			errorInvalidToken();
			doctype.setQuirksMode(true);
			state = BOGUS_DOCTYPE;
		}
	};

	protected State BETWEEN_DOCTYPE_PUBLIC_AND_SYSTEM_IDENTIFIERS = new State() {
		@Override
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					emitDoctype();
					return;
				}

				char c = input[ndx];

				if (equalsOne(c, TAG_WHITESPACES)) {
					continue;
				}

				if (c == '>') {
					state = DATA_STATE;
					emitDoctype();
					return;
				}

				if (c == '\"') {
					doctypeIdNameStart = ndx + 1;
					state = DOCTYPE_SYSTEM_IDENTIFIER_DOUBLE_QUOTED;
					return;
				}

				if (c == '\'') {
					doctypeIdNameStart = ndx + 1;
					state = DOCTYPE_SYSTEM_IDENTIFIER_SINGLE_QUOTED;
					return;
				}

				errorInvalidToken();
				doctype.setQuirksMode(true);
				state = BOGUS_DOCTYPE;
				return;
			}
		}
	};


	protected State BOGUS_DOCTYPE = new State() {
		@Override
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					state = DATA_STATE;
					emitDoctype();
					return;
				}

				char c = input[ndx];

				if (c == '>') {
					state = DATA_STATE;
					emitDoctype();
					return;
				}
			}
		}
	};

	protected State AFTER_DOCTYPE_SYSTEM_KEYWORD = new State() {
		@Override
		public void parse() {
			ndx++;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				doctype.setQuirksMode(true);
				emitDoctype();
				return;
			}

			char c = input[ndx];

			if (equalsOne(c, TAG_WHITESPACES)) {
				state = BEFORE_DOCTYPE_SYSTEM_IDENTIFIER;
				return;
			}

			if (c == '\"') {
				errorInvalidToken();
				doctypeIdNameStart = ndx + 1;
				state = DOCTYPE_SYSTEM_IDENTIFIER_DOUBLE_QUOTED;
				return;
			}

			if (c == '\'') {
				errorInvalidToken();
				doctypeIdNameStart = ndx + 1;
				state = DOCTYPE_SYSTEM_IDENTIFIER_SINGLE_QUOTED;
				return;
			}

			if (c == '>') {
				errorInvalidToken();
				state = DATA_STATE;
				doctype.setQuirksMode(true);
				emitDoctype();
				return;
			}

			errorInvalidToken();
			state = BOGUS_DOCTYPE;
			doctype.setQuirksMode(true);
		}
	};

	protected State BEFORE_DOCTYPE_SYSTEM_IDENTIFIER = new State() {
		@Override
		public void parse() {
			while(true) {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					emitDoctype();
					return;
				}

				char c = input[ndx];

				if (equalsOne(c, TAG_WHITESPACES)) {
					continue;
				}

				if (c == '\"') {
					doctypeIdNameStart = ndx + 1;
					state = DOCTYPE_SYSTEM_IDENTIFIER_DOUBLE_QUOTED;
					return;
				}

				if (c == '\'') {
					doctypeIdNameStart = ndx + 1;
					state = DOCTYPE_SYSTEM_IDENTIFIER_SINGLE_QUOTED;
					return;
				}

				if (c == '>') {
					errorInvalidToken();
					state = DATA_STATE;
					doctype.setQuirksMode(true);
					emitDoctype();
					return;
				}

				errorInvalidToken();
				doctype.setQuirksMode(true);
				state = BOGUS_DOCTYPE;
				return;
			}
		}
	};

	protected State DOCTYPE_SYSTEM_IDENTIFIER_DOUBLE_QUOTED = new State() {
		@Override
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					doctype.setSystemIdentifier(charSequence(doctypeIdNameStart, ndx));
					errorEOF();
					state = DATA_STATE;
					doctype.setQuirksMode(true);
					emitDoctype();
				}

				char c = input[ndx];

				if (c == '\"') {
					doctype.setSystemIdentifier(charSequence(doctypeIdNameStart, ndx));
					state = AFTER_DOCTYPE_SYSTEM_IDENTIFIER;
					return;
				}

				if (c == '>') {
					doctype.setSystemIdentifier(charSequence(doctypeIdNameStart, ndx));
					errorInvalidToken();
					state = DATA_STATE;
					doctype.setQuirksMode(true);
					emitDoctype();
					return;
				}
			}
		}
	};

	protected State DOCTYPE_SYSTEM_IDENTIFIER_SINGLE_QUOTED = new State() {
		@Override
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					doctype.setSystemIdentifier(charSequence(doctypeIdNameStart, ndx));
					errorEOF();
					state = DATA_STATE;
					doctype.setQuirksMode(true);
					emitDoctype();
				}

				char c = input[ndx];

				if (c == '\'') {
					doctype.setSystemIdentifier(charSequence(doctypeIdNameStart, ndx));
					state = AFTER_DOCTYPE_SYSTEM_IDENTIFIER;
					return;
				}

				if (c == '>') {
					doctype.setSystemIdentifier(charSequence(doctypeIdNameStart, ndx));
					errorInvalidToken();
					state = DATA_STATE;
					doctype.setQuirksMode(true);
					emitDoctype();
					return;
				}
			}
		}
	};

	protected State AFTER_DOCTYPE_SYSTEM_IDENTIFIER = new State() {
		@Override
		public void parse() {
			while(true) {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					doctype.setQuirksMode(true);
					emitDoctype();
					return;
				}

				char c = input[ndx];

				if (equalsOne(c, TAG_WHITESPACES)) {
					continue;
				}

				if (c == '>') {
					state = DATA_STATE;
					emitDoctype();
					return;
				}

				errorInvalidToken();
				state = BOGUS_DOCTYPE;
				// does NOT set the quirks mode!
			}
		}
	};


	// ---------------------------------------------------------------- SCRIPT

	protected int scriptStartNdx = -1;
	protected int scriptEndNdx = -1;
	protected int scriptEndTagName = -1;

	protected State SCRIPT_DATA = new State() {
		@Override
		public void parse() {

			while(true) {
				ndx++;

				if (isEOF()) {
					emitScript(scriptStartNdx, total);
					state = DATA_STATE;
					return;
				}

				char c = input[ndx];

				if (c == '<') {
					scriptEndNdx = ndx;
					state = SCRIPT_DATA_LESS_THAN_SIGN;
					return;
				}
			}
		}
	};

	protected State SCRIPT_DATA_LESS_THAN_SIGN = new State() {
		@Override
		public void parse() {
			ndx++;

			if (isEOF()) {
				state = SCRIPT_DATA;
				ndx--;
				return;
			}

			char c = input[ndx];

			if (c == '/') {
				state = SCRIPT_DATA_END_TAG_OPEN;
				return;
			}
			if (c == '!') {
				if (scriptEscape == null) {
					// create script escape states only if really needed
					scriptEscape = new ScriptEscape();
				}
				state = scriptEscape.SCRIPT_DATA_ESCAPE_START;
				return;
			}
			state = SCRIPT_DATA;
		}
	};

	protected State SCRIPT_DATA_END_TAG_OPEN = new State() {
		@Override
		public void parse() {
			ndx++;

			if (isEOF()) {
				state = SCRIPT_DATA;
				ndx--;
				return;
			}

			char c = input[ndx];

			if (isAlpha(c)) {
				state = SCRIPT_DATA_END_TAG_NAME;
				scriptEndTagName = ndx;
				return;
			}

			state = SCRIPT_DATA;
		}
	};

	protected State SCRIPT_DATA_END_TAG_NAME = new State() {
		@Override
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					state = SCRIPT_DATA;
					return;
				}

				char c = input[ndx];

				if (equalsOne(c, TAG_WHITESPACES)) {
					if (isAppropriateTagName(T_SCRIPT, scriptEndTagName, ndx)) {
						state = BEFORE_ATTRIBUTE_NAME;
					} else {
						state = SCRIPT_DATA;
					}
					return;
				}
				if (c == '/') {
					if (isAppropriateTagName(T_SCRIPT, scriptEndTagName, ndx)) {
						state = SELF_CLOSING_START_TAG;
					} else {
						state = SCRIPT_DATA;
					}
					return;
				}
				if (c == '>') {
					if (isAppropriateTagName(T_SCRIPT, scriptEndTagName, ndx)) {
						state = DATA_STATE;
						emitScript(scriptStartNdx, scriptEndNdx);
					} else {
						state = SCRIPT_DATA;
					}
					return;
				}
				if (isAlpha(c)) {
					continue;
				}
				state = SCRIPT_DATA;
				return;
			}
		}
	};

	// ---------------------------------------------------------------- SCRIPT ESCAPE

	protected ScriptEscape scriptEscape = null;

	/**
	 * Since escaping states inside the SCRIPT tag are rare, we want to use them
	 * lazy, only when really needed. Therefore, they are all grouped inside separate
	 * class that will be instantiated only if needed.
	 */
	protected class ScriptEscape {

		protected int doubleEscapedNdx = -1;
		protected int doubleEscapedEndTag = -1;

		protected State SCRIPT_DATA_ESCAPE_START = new State() {
			@Override
			public void parse() {
				ndx++;

				if (isEOF()) {
					state = SCRIPT_DATA;
					ndx--;
					return;
				}

				char c = input[ndx];

				if (c == '-') {
					state = SCRIPT_DATA_ESCAPE_START_DASH;
					return;
				}

				state = SCRIPT_DATA;
			}
		};

		protected State SCRIPT_DATA_ESCAPE_START_DASH = new State() {
			@Override
			public void parse() {
				ndx++;

				if (isEOF()) {
					state = SCRIPT_DATA;
					return;
				}

				char c = input[ndx];

				if (c == '-') {
					state = SCRIPT_DATA_ESCAPED_DASH_DASH;
					return;
				}

				state = SCRIPT_DATA;
			}
		};

		protected State SCRIPT_DATA_ESCAPED_DASH_DASH = new State() {
			@Override
			public void parse() {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					return;
				}

				char c = input[ndx];

				if (c == '-') {
					return;
				}

				if (c == '<') {
					state = SCRIPT_DATA_ESCAPED_LESS_THAN_SIGN;
					return;
				}

				if (c == '>') {
					state = SCRIPT_DATA;
					return;
				}

				state = SCRIPT_DATA_ESCAPED;
			}
		};

		protected State SCRIPT_DATA_ESCAPED_LESS_THAN_SIGN = new State() {
			@Override
			public void parse() {
				ndx++;

				if (isEOF()) {
					state = SCRIPT_DATA_ESCAPED;
					return;
				}

				char c = input[ndx];

				if (c == '/') {
					doubleEscapedNdx = -1;
					state = SCRIPT_DATA_ESCAPED_END_TAG_OPEN;
					return;
				}

				if (isAlpha(c)) {
					doubleEscapedNdx = ndx;
					state = SCRIPT_DATA_DOUBLE_ESCAPE_START;
					return;
				}

				state = SCRIPT_DATA_ESCAPED;
			}
		};

		protected State SCRIPT_DATA_ESCAPED = new State() {
			@Override
			public void parse() {
				while (true) {
					ndx++;

					if (isEOF()) {
						errorEOF();
						emitScript(scriptStartNdx, total);
						state = DATA_STATE;
						return;
					}

					char c = input[ndx];

					if (c == '-') {
						state = SCRIPT_DATA_ESCAPED_DASH;
						break;
					}

					if (c == '<') {
						state = SCRIPT_DATA_ESCAPED_LESS_THAN_SIGN;
						return;
					}
				}
			}
		};


		protected State SCRIPT_DATA_ESCAPED_DASH = new State() {
			@Override
			public void parse() {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					return;
				}

				char c = input[ndx];

				if (c == '-') {
					state = SCRIPT_DATA_ESCAPED_DASH_DASH;
					return;
				}

				if (c == '<') {
					state = SCRIPT_DATA_ESCAPED_DASH_DASH;
					return;
				}

				state = SCRIPT_DATA_ESCAPED;
			}
		};

		protected State SCRIPT_DATA_ESCAPED_END_TAG_OPEN = new State() {
			@Override
			public void parse() {
				ndx++;

				if (isEOF()) {
					state = SCRIPT_DATA_ESCAPED;
					return;
				}

				char c = input[ndx];

				if (isAlpha(c)) {
					// todo Create a new end tag token?
					state = SCRIPT_DATA_ESCAPED_END_TAG_NAME;
				}

				state = SCRIPT_DATA_ESCAPED;
			}
		};

		protected State SCRIPT_DATA_ESCAPED_END_TAG_NAME = new State() {
			@Override
			public void parse() {
				while (true) {
					ndx++;

					if (isEOF()) {
						state = SCRIPT_DATA_ESCAPED;
						return;
					}

					char c = input[ndx];

					if (equalsOne(c, TAG_WHITESPACES)) {
						if (isAppropriateTagName(T_SCRIPT, scriptEndTagName, ndx)) {
							state = BEFORE_ATTRIBUTE_NAME;
						} else {
							state = SCRIPT_DATA_ESCAPED;
						}
						return;
					}
					if (c == '/') {
						if (isAppropriateTagName(T_SCRIPT, scriptEndTagName, ndx)) {
							state = SELF_CLOSING_START_TAG;
						} else {
							state = SCRIPT_DATA_ESCAPED;
						}
						return;
					}
					if (c == '>') {
						if (isAppropriateTagName(T_SCRIPT, scriptEndTagName, ndx)) {
							state = DATA_STATE;
							emitTag();
						} else {
							state = SCRIPT_DATA_ESCAPED;
						}
						return;
					}
					if (isAlpha(c)) {
						continue;
					}
					state = SCRIPT_DATA_ESCAPED;
					return;
				}
			}
		};

		// ---------------------------------------------------------------- SCRIPT DOUBLE ESCAPE

		protected State SCRIPT_DATA_DOUBLE_ESCAPE_START = new State() {
			@Override
			public void parse() {
				while (true) {
					ndx++;

					if (isEOF()) {
						state = SCRIPT_DATA_ESCAPED;
						return;
					}

					char c = input[ndx];

					if (equalsOne(c, TAG_WHITESPACES_OR_END)) {
						if (isAppropriateTagName(T_SCRIPT, doubleEscapedNdx, ndx)) {
							state = SCRIPT_DATA_DOUBLE_ESCAPED;
						} else {
							state = SCRIPT_DATA_ESCAPED;
						}
						return;
					}

					if (isAlpha(c)) {
						continue;
					}
					state = SCRIPT_DATA_ESCAPED;
					return;
				}
			}
		};

		protected State SCRIPT_DATA_DOUBLE_ESCAPED = new State() {
			@Override
			public void parse() {
				while (true) {
					ndx++;

					if (isEOF()) {
						errorEOF();
						state = DATA_STATE;
						return;
					}

					char c = input[ndx];

					if (c == '-') {
						state = SCRIPT_DATA_DOUBLE_ESCAPED_DASH;
						return;
					}

					if (c == '<') {
						state = SCRIPT_DATA_DOUBLE_ESCAPED_LESS_THAN_SIGN;
						return;
					}
				}
			}
		};

		protected State SCRIPT_DATA_DOUBLE_ESCAPED_DASH = new State() {
			@Override
			public void parse() {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					return;
				}

				char c = input[ndx];

				if (c == '-') {
					state = SCRIPT_DATA_DOUBLE_ESCAPED_DASH_DASH;
					return;
				}
				if (c == '<') {
					state = SCRIPT_DATA_DOUBLE_ESCAPED_LESS_THAN_SIGN;
					return;
				}
				state = SCRIPT_DATA_DOUBLE_ESCAPED;
			}
		};

		protected State SCRIPT_DATA_DOUBLE_ESCAPED_DASH_DASH = new State() {
			@Override
			public void parse() {
				while (true) {
					ndx++;

					if (isEOF()) {
						errorEOF();
						state = DATA_STATE;
						return;
					}

					char c = input[ndx];

					if (c == '-') {
						continue;
					}

					if (c == '<') {
						state = SCRIPT_DATA_DOUBLE_ESCAPED_LESS_THAN_SIGN;
						return;
					}
					if (c == '>') {
						state = SCRIPT_DATA;
						return;
					}
					state = SCRIPT_DATA_DOUBLE_ESCAPED;
					return;
				}
			}
		};

		protected State SCRIPT_DATA_DOUBLE_ESCAPED_LESS_THAN_SIGN = new State() {
			@Override
			public void parse() {
				ndx++;

				if (isEOF()) {
					state = SCRIPT_DATA_DOUBLE_ESCAPED;
					return;
				}

				char c = input[ndx];

				if (c == '/') {
					state = SCRIPT_DATA_DOUBLE_ESCAPE_END;
					return;
				}

				state = SCRIPT_DATA_DOUBLE_ESCAPED;
			}
		};

		protected State SCRIPT_DATA_DOUBLE_ESCAPE_END = new State() {
			@Override
			public void parse() {
				doubleEscapedEndTag = ndx + 1;

				while (true) {
					ndx++;

					if (isEOF()) {
						state = SCRIPT_DATA_DOUBLE_ESCAPED;
						return;
					}

					char c = input[ndx];

					if (equalsOne(c, TAG_WHITESPACES_OR_END)) {
						if (isAppropriateTagName(T_SCRIPT, doubleEscapedEndTag, ndx)) {
							state = SCRIPT_DATA_ESCAPED;
						} else {
							state = SCRIPT_DATA_DOUBLE_ESCAPED;
						}
						return;
					}
					if (isAlpha(c)) {
						continue;
					}

					state = SCRIPT_DATA_DOUBLE_ESCAPED;
					return;
				}
			}
		};
	}

	// ---------------------------------------------------------------- xml

	protected XmlDeclaration xmlDeclaration = null;

	protected class XmlDeclaration {

		protected int xmlAttrCount = 0;
		protected int xmlAttrStartNdx = -1;
		protected CharSequence version;
		protected CharSequence encoding;
		protected CharSequence standalone;
		protected char attrQuote;

		protected void reset() {
			xmlAttrCount = 0;
			xmlAttrStartNdx = -1;
			version = encoding = standalone = null;
		}

		protected State XML_BETWEEN = new State() {
			@Override
			public void parse() {

				while (true) {
					ndx++;

					if (isEOF()) {
						errorEOF();
						state = DATA_STATE;
						return;
					}

					char c = input[ndx];

					if (equalsOne(c, TAG_WHITESPACES)) {
						continue;
					}

					if (c == '?') {
						state = XML_CLOSE;
						return;
					}

					switch (xmlAttrCount) {
						case 0:
							if (match(XML_VERSION)) {
								ndx += XML_VERSION.length - 1;
								state = AFTER_XML_ATTRIBUTE_NAME;
								return;
							}
							break;
						case 1:
							if (match(XML_ENCODING)) {
								ndx += XML_ENCODING.length - 1;
								state = AFTER_XML_ATTRIBUTE_NAME;
								return;
							}
							break;
						case 2:
							if (match(XML_STANDALONE)) {
								ndx += XML_STANDALONE.length - 1;
								state = AFTER_XML_ATTRIBUTE_NAME;
								return;
							}
							break;
					}

					errorInvalidToken();
					state = DATA_STATE;
				}
			}
		};

		protected State AFTER_XML_ATTRIBUTE_NAME = new State() {
			@Override
			public void parse() {
				while(true) {
					ndx++;

					if (isEOF()) {
						errorEOF();
						state = DATA_STATE;
						return;
					}

					char c = input[ndx];

					if (equalsOne(c, TAG_WHITESPACES)) {
						continue;
					}

					if (c == '=') {
						state = BEFORE_XML_ATTRIBUTE_VALUE;
						return;
					}

					errorInvalidToken();
					state = DATA_STATE;
					return;
				}
			}
		};

		protected State BEFORE_XML_ATTRIBUTE_VALUE = new State() {
			@Override
			public void parse() {
				while(true) {
					ndx++;

					if (isEOF()) {
						errorEOF();
						state = DATA_STATE;
						return;
					}

					char c = input[ndx];

					if (equalsOne(c, TAG_WHITESPACES)) {
						continue;
					}

					if (c == '\"' || c == '\'') {
						state = XML_ATTRIBUTE_VALUE;
						attrQuote = c;
						return;
					}

					errorInvalidToken();
					state = DATA_STATE;
					return;
				}
			}
		};

		protected State XML_ATTRIBUTE_VALUE = new State() {
			@Override
			public void parse() {
				xmlAttrStartNdx = ndx + 1;

				while(true) {
					ndx++;

					if (isEOF()) {
						errorEOF();
						state = DATA_STATE;
						return;
					}

					char c = input[ndx];

					if (c == attrQuote) {
						CharSequence value = charSequence(xmlAttrStartNdx, ndx);

						switch (xmlAttrCount) {
							case 0: version = value; break;
							case 1: encoding = value; break;
							case 2: standalone = value; break;
						}

						xmlAttrCount++;

						state = XML_BETWEEN;
						return;
					}
				}
			}
		};


		protected State XML_CLOSE = new State() {
			@Override
			public void parse() {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					return;
				}

				char c = input[ndx];

				if (c == '>') {
					emitXml();
					state = DATA_STATE;
					return;
				}

				errorInvalidToken();
				state = DATA_STATE;
			}
		};

		// ---------------------------------------------------------------- CDATA

		protected State CDATA = new State() {
			@Override
			public void parse() {
				ndx++;

				int cdataEndNdx = find(CDATA_END, ndx, total);

				if (cdataEndNdx == -1) {
					cdataEndNdx = total;
				}

				CharSequence cdata = charSequence(ndx, cdataEndNdx);

				emitCData(cdata);

				ndx = cdataEndNdx + 2;

				state = DATA_STATE;
			}
		};

	}

	// ---------------------------------------------------------------- text

	protected char[] text;
	protected int textLen;

	private void ensureCapacity() {
		if (textLen == text.length) {
			text = ArraysUtil.resize(text, textLen << 1);
		}
	}

	private void ensureCapacity(final int growth) {
		int desiredLen = textLen + growth;
		if (desiredLen > text.length) {
			text = ArraysUtil.resize(text, Math.max(textLen << 1, desiredLen));
		}
	}

	/**
	 * Emits characters into the local text buffer.
	 */
	protected void textEmitChar(final char c) {
		ensureCapacity();
		text[textLen++] = c;
	}

	/**
	 * Resets text buffer.
	 */
	protected void textStart() {
		textLen = 0;
	}

	protected void textEmitChars(int from, final int to) {
		ensureCapacity(to - from);
		while (from < to) {
			text[textLen++] = input[from++];
		}
	}

	protected void textEmitChars(final char[] buffer) {
		ensureCapacity(buffer.length);
		for (char aBuffer : buffer) {
			text[textLen++] = aBuffer;
		}
	}

	protected CharSequence textWrap() {
		if (textLen == 0) {
			return CharArraySequence.EMPTY;
		}
		return new String(text, 0, textLen);    // todo use charSequence pointer instead!
	}

	// ---------------------------------------------------------------- attr

	protected int attrStartNdx = -1;
	protected int attrEndNdx = -1;

	private void _addAttribute() {
		_addAttribute(charSequence(attrStartNdx, attrEndNdx), null);
	}

	private void _addAttributeWithValue() {
		_addAttribute(charSequence(attrStartNdx, attrEndNdx), textWrap().toString());
	}

	private void _addAttribute(final CharSequence attrName, final CharSequence attrValue) {
		if (tag.getType() == TagType.END) {
			_error("Ignored end tag attribute");
		} else {
			if (tag.hasAttribute(attrName)) {
				_error("Ignored duplicated attribute: " + attrName);
			} else {
				tag.addAttribute(attrName, attrValue);
			}
		}

		attrStartNdx = -1;
		attrEndNdx = -1;
		textLen = 0;
	}

	protected void emitTag() {
		tag.end(ndx + 1);

		if (config.calculatePosition) {
			tag.setPosition(position(tag.getTagPosition()));
		}

		if (tag.getType().isStartingTag()) {

			if (matchTagName(T_SCRIPT)) {
				scriptStartNdx = ndx + 1;
				state = SCRIPT_DATA;
				return;
			}

			// detect RAWTEXT tags

			if (config.enableRawTextModes) {
				for (char[] rawtextTagName : RAWTEXT_TAGS) {
					if (matchTagName(rawtextTagName)) {
						tag.setRawTag(true);
						state = RAWTEXT;
						rawTextStart = ndx + 1;
						rawTagName = rawtextTagName;
						break;
					}
				}

				// detect RCDATA tag

				for (char[] rcdataTextTagName : RCDATA_TAGS) {
					if (matchTagName(rcdataTextTagName)) {
						state = RCDATA;
						rcdataTagStart = ndx + 1;
						rcdataTagName = rcdataTextTagName;
						break;
					}
				}
			}

			tag.increaseDeepLevel();
		}

		visitor.tag(tag);

		if (tag.getType().isEndingTag()) {
			tag.decreaseDeepLevel();
		}
	}

	/**
	 * Emits a comment. Also checks for conditional comments!
	 */
	protected void emitComment(final int from, final int to) {
		if (config.enableConditionalComments) {
			// CC: downlevel-hidden starting
			if (match(CC_IF, from)) {
				int endBracketNdx = find(']', from + 3, to);

				CharSequence expression = charSequence(from + 1, endBracketNdx);

				ndx = endBracketNdx + 1;

				char c = input[ndx];

				if (c != '>') {
					errorInvalidToken();
				}

				visitor.condComment(expression, true, true, false);

				state = DATA_STATE;
				return;
			}

			if (to > CC_ENDIF2.length && match(CC_ENDIF2, to - CC_ENDIF2.length)) {
				// CC: downlevel-hidden ending
				visitor.condComment(_ENDIF, false, true, true);

				state = DATA_STATE;
				return;
			}
		}

		CharSequence comment = charSequence(from, to);

		visitor.comment(comment);

		commentStart = -1;
	}

	/**
	 * Emits text if there is some content.
	 */
	protected void emitText() {
		if (textLen != 0) {
			visitor.text(textWrap());
		}
		textLen = 0;
	}

	protected void emitScript(final int from, final int to) {
		tag.increaseDeepLevel();

		tag.setRawTag(true);
		visitor.script(tag, charSequence(from, to));

		tag.decreaseDeepLevel();
		scriptStartNdx = -1;
		scriptEndNdx = -1;
	}

	protected void emitDoctype() {
		visitor.doctype(doctype);

		doctype.reset();
	}

	protected void emitXml() {
		visitor.xml(xmlDeclaration.version, xmlDeclaration.encoding, xmlDeclaration.standalone);

		xmlDeclaration.reset();
	}

	protected void emitCData(final CharSequence charSequence) {
		visitor.cdata(charSequence);
	}

	// ---------------------------------------------------------------- error

	protected void errorEOF() {
		_error("Parse error: EOF");
	}

	protected void errorInvalidToken() {
		_error("Parse error: invalid token");
	}

	protected void errorCharReference() {
		_error("Parse error: invalid character reference");
	}

	/**
	 * Prepares error message and reports it to the visitor.
	 */
	protected void _error(String message) {
		if (config.calculatePosition) {
			Position currentPosition = position(ndx);
			message = message
					.concat(StringPool.SPACE)
					.concat(currentPosition.toString());
		} else {
			message = message
					.concat(" [@")
					.concat(Integer.toString(ndx))
					.concat(StringPool.RIGHT_SQ_BRACKET);
		}

		visitor.error(message);
	}

	// ---------------------------------------------------------------- util

	private boolean isAppropriateTagName(final char[] lowerCaseNameToMatch, final int from, final int to) {
		final int len = to - from;

		if (len != lowerCaseNameToMatch.length) {
			return false;
		}

		for (int i = from, k = 0; i < to; i++, k++) {
			char c = input[i];

			c = CharUtil.toLowerAscii(c);

			if (c != lowerCaseNameToMatch[k]) {
				return false;
			}
		}
		return true;
	}

	private boolean matchTagName(final char[] tagNameLowercase) {
		final CharSequence charSequence = tag.getName();

		int length = tagNameLowercase.length;
		if (charSequence.length() != length) {
			return false;
		}

		for (int i = 0; i < length; i++) {
			char c = charSequence.charAt(i);

			c = CharUtil.toLowerAscii(c);

			if (c != tagNameLowercase[i]) {
				return false;
			}
		}

		return true;
	}

	// ---------------------------------------------------------------- state

	protected State state = DATA_STATE;
	
	// ---------------------------------------------------------------- names
	
	private static final char[] TAG_WHITESPACES = new char[] {'\t', '\n', '\r', ' '};
	private static final char[] TAG_WHITESPACES_OR_END = new char[] {'\t', '\n', '\r', ' ', '/', '>'};
	private static final char[] CONTINUE_CHARS = new char[] {'\t', '\n', '\r', ' ', '<', '&'};

	private static final char[] ATTR_INVALID_1 = new char[] {'\"', '\'', '<', '='};
	private static final char[] ATTR_INVALID_2 = new char[] {'\"', '\'', '<'};
	private static final char[] ATTR_INVALID_3 = new char[] {'<', '=', '`'};
	private static final char[] ATTR_INVALID_4 = new char[] {'"', '\'', '<', '=', '`'};

	private static final char[] COMMENT_DASH = new char[] {'-', '-'};

	private static final char[] T_DOCTYPE = new char[] {'D', 'O', 'C', 'T', 'Y', 'P', 'E'};
	private static final char[] T_SCRIPT = new char[] {'s', 'c', 'r', 'i', 'p', 't'};
	private static final char[] T_XMP = new char[] {'x', 'm', 'p'};
	private static final char[] T_STYLE = new char[] {'s', 't', 'y', 'l', 'e'};
	private static final char[] T_IFRAME = new char[] {'i', 'f', 'r', 'a', 'm', 'e'};
	private static final char[] T_NOFRAMES = new char[] {'n', 'o', 'f', 'r', 'a', 'm', 'e', 's'};
	private static final char[] T_NOEMBED = new char[] {'n', 'o', 'e', 'm', 'b', 'e', 'd'};
	private static final char[] T_NOSCRIPT = new char[] {'n', 'o', 's', 'c', 'r', 'i', 'p', 't'};
	private static final char[] T_TEXTAREA = new char[] {'t', 'e', 'x', 't', 'a', 'r', 'e', 'a'};
	private static final char[] T_TITLE = new char[] {'t', 'i', 't', 'l', 'e'};

	private static final char[] A_PUBLIC = new char[] {'P', 'U', 'B', 'L', 'I', 'C'};
	private static final char[] A_SYSTEM = new char[] {'S', 'Y', 'S', 'T', 'E', 'M'};

	private static final char[] CDATA = new char[] {'[', 'C', 'D', 'A', 'T', 'A', '['};
	private static final char[] CDATA_END = new char[] {']', ']', '>'};

	private static final char[] XML = new char[] {'?', 'x', 'm', 'l'};
	private static final char[] XML_VERSION = new char[] {'v', 'e', 'r', 's', 'i', 'o', 'n'};
	private static final char[] XML_ENCODING = new char[] {'e', 'n', 'c', 'o', 'd', 'i', 'n', 'g'};
	private static final char[] XML_STANDALONE = new char[] {'s', 't', 'a', 'n', 'd', 'a', 'l', 'o', 'n', 'e'};

	private static final char[] CC_IF = new char[] {'[', 'i', 'f', ' '};
	private static final char[] CC_ENDIF = new char[] {'[', 'e', 'n', 'd', 'i', 'f', ']'};
	private static final char[] CC_ENDIF2 = new char[] {'<', '!', '[', 'e', 'n', 'd', 'i', 'f', ']'};
	private static final char[] CC_END = new char[] {']', '>'};

	// CDATA
	private static final char[][] RAWTEXT_TAGS = new char[][] {
			T_XMP, T_STYLE, T_IFRAME, T_NOEMBED, T_NOFRAMES, T_NOSCRIPT, T_SCRIPT
	};

	private static final char[][] RCDATA_TAGS = new char[][] {
			T_TEXTAREA, T_TITLE
	};

	private static final char REPLACEMENT_CHAR = '\uFFFD';

	private static final char[] INVALID_CHARS = new char[] {'\u000B', '\uFFFE', '\uFFFF'};
	//, '\u1FFFE', '\u1FFFF', '\u2FFFE', '\u2FFFF', '\u3FFFE', '\u3FFFF', '\u4FFFE,
	//	'\u4FFFF', '\u5FFFE', '\u5FFFF', '\u6FFFE', '\u6FFFF', '\u7FFFE', '\u7FFFF', '\u8FFFE', '\u8FFFF', '\u9FFFE,
	//	'\u9FFFF', '\uAFFFE', '\uAFFFF', '\uBFFFE', '\uBFFFF', '\uCFFFE', '\uCFFFF', '\uDFFFE', '\uDFFFF', '\uEFFFE,
	//	'\uEFFFF', '\uFFFFE', '\uFFFFF', '\u10FFFE', '\u10FFFF',

	private static final CharSequence _ENDIF = "endif";

}