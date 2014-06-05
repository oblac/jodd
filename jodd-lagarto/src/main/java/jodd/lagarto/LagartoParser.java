// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.util.ArraysUtil;
import jodd.util.CharUtil;
import jodd.util.HtmlDecoder;
import jodd.util.StringPool;
import jodd.util.UnsafeUtil;

import java.nio.CharBuffer;

import static jodd.util.CharUtil.equalsOne;
import static jodd.util.CharUtil.isAlpha;
import static jodd.util.CharUtil.isDigit;

/**
 * HTML/XML content parser using {@link TagVisitor} for callbacks.
 * Differences from: http://www.w3.org/TR/html5/
 * <ul>
 * <li>tag name case (and other entities) is not changed
 * <li>tokenization continues without going into tree buidling</li>
 * <li>conditional comments added</li>
 * </ul>
 *
 * What should be changed in SPEC:
 * <ul>
 * <li>TOKENIZER is the one who should deal with all state changes,
 * 		not the tree builder!</li>
 * <li>Recognize two type of states, one that iterates and one that
 * doesn't</li>
 * <li>Order of error/state change must be always the same.</li>
 * <li>TOKENIZER should NOT change the tag names letter case.
 * Tokenizer should not change the source in any way.</li>
 * </ul>
 */
public class LagartoParser extends CharScanner {

	protected TagVisitor visitor;
	protected LagartoParserContext ctx;
	protected ParsedTag tag;
	protected ParsedDoctype doctype;

	/**
	 * Creates parser on char array.
	 */
	public LagartoParser(char[] charArray) {
			initialize(charArray);
		}

	/**
	 * Creates parser on a String.
	 */
	public LagartoParser(String string) {
			initialize(UnsafeUtil.getChars(string));
		}

	/**
	 * Initializes parser.
	 */
	protected void initialize(char[] input) {
		super.initialize(input);
		this.ctx = new LagartoParserContext();
		this.tag = new ParsedTag(input);
		this.doctype = new ParsedDoctype(input);
		this.text = new char[1024];
		this.textLen = 0;
	}

	// ---------------------------------------------------------------- properties

	protected boolean xmlMode = false;
	protected boolean enableConditionalComments = true;
	protected boolean calculatePosition;

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
	 * Returns <code>true</code> if XML mode is enabled.
	 */
	public boolean isXmlMode() {
		return xmlMode;
	}

	/**
	 * Enables XML mode when some XML-only events.
	 */
	public void setXmlMode(boolean xmlMode) {
		this.xmlMode = xmlMode;
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

	public boolean isCalculatePosition() {
		return calculatePosition;
	}

	// ---------------------------------------------------------------- parse

	protected boolean parsing;

	/**
	 * Parses content and callback provided {@link TagVisitor}.
	 */
	public void parse(TagVisitor visitor) {
		this.visitor = visitor;

		visitor.start(ctx);

		parsing = true;

		while (parsing) {
			state.parse();
		}

		emitText();

		visitor.end();
	}

	// ---------------------------------------------------------------- flags

	protected int rawTextStart;		// todo prodji sve varijable i vidi da li se koriste!
	protected int rawTextEnd;
	protected char[] rawTagName;
	protected int rcdataTagStart = -1;
	protected char[] rcdataTagName;


	// ---------------------------------------------------------------- start & end

	/**
	 * Data state.
	 */
	protected State DATA_STATE =  new State() {
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

	protected void consumeCharacterReference(char allowedChar) {
		ndx++;
		if (isEOF()) {
			return;
		}

		char c = input[ndx];

		if (c == allowedChar) {
			ndx--;
			return;
		}

		consumeCharacterReference();
	}

	protected void consumeCharacterReference() {
		int unconsumeNdx = ndx;

		ndx++;

		if (isEOF()) {
			return;
		}

		char c = input[ndx];

		if (equalsOne(c, CONTINUE_CHARS)) {
			ndx = unconsumeNdx;
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

	private void _consumeNumber(int unconsumeNdx) {
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
			if (xmlMode) {
				if (match(_XML)) {
					ndx += _XML.length - 1;
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
					tag.setName(substring(nameNdx, ndx));
					break;
				}

				if (c == '/') {
					state = SELF_CLOSING_START_TAG;
					tag.setName(substring(nameNdx, ndx));
					break;
				}

				if (c == '>') {
					state = DATA_STATE;
					tag.setName(substring(nameNdx, ndx));
					emitTag();
					break;
				}
			}
		}
	};

	protected State BEFORE_ATTRIBUTE_NAME = new State() {
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
				}

				textEmitChar(c);
			}
		}
	};

	protected State AFTER_ATTRIBUTE_VALUE_QUOTED = new State() {
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
		public void parse() {
			int tagEndNdx = find('>', ndx, total); 		// todo remove find

			if (tagEndNdx == -1) {
				tagEndNdx = total;
			}

			emitComment(ndx, tagEndNdx);

			state = DATA_STATE;
			ndx++;
		}
	};

	protected State MARKUP_DECLARATION_OPEN = new State() {
		public void parse() {
			ndx++;

			if (isEOF()) {
				errorEOF();
				state = BOGUS_COMMENT;
				return;
			}

			if (match(_COMMENT_DASH)) {
				state = COMMENT_START;
				ndx++;
				return;
			}

			if (matchCaseInsensitiveWithUpper(_DOCTYPE)) {
				state = DOCTYPE;
				ndx += _DOCTYPE.length - 1;
				return;
			}

			if (enableConditionalComments) {
				// CC: downlevel-revealed starting
				if (match(_CC_IF)) {
					int ccEndNdx = find(_CC_END, ndx + _CC_IF.length, total);

					if (ccEndNdx == -1) {
						// todo
					}

					CharSequence expression = charSequence(ndx + 1, ccEndNdx);

					visitor.condComment(expression, true, false, false);

					ndx = ccEndNdx + 1;
					state = DATA_STATE;
					return;
				}

				// CC: downlevel-* ending tag
				if (match(_CC_ENDIF)) {
					ndx += _CC_ENDIF.length;

					int ccEndNdx = find('>', ndx, total);

					if (ccEndNdx == -1) {
						// todo
					}

					if (match(_COMMENT_DASH, ccEndNdx - 2)) {
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

			if (xmlMode) {
				if (match(_CDATA)) {
					ndx += _CDATA.length - 1;

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

	protected State RAWTEXT = new State() {
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
		public void parse() {
			ndx++;

			if (isEOF()) {
				state = RAWTEXT;
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
						tag.setName(substring(rawtextEndTagNameStartNdx, ndx));
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
						tag.setName(substring(rawtextEndTagNameStartNdx, ndx));
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
						tag.setName(substring(rawtextEndTagNameStartNdx, ndx));
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

	protected State RCDATA = new State() {
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
				}

				textEmitChar(c);
			}
		}
	};

	protected State RCDATA_LESS_THAN_SIGN = new State() {
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
						tag.setName(substring(rcdataEndTagNameStartNdx, ndx));
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
						tag.setName(substring(rcdataEndTagNameStartNdx, ndx));
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
						tag.setName(substring(rcdataEndTagNameStartNdx, ndx));
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
		public void parse() {
			ndx++;
			commentStart = ndx;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				emitComment(commentStart, ndx);
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
		public void parse() {
			ndx++;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				emitComment(commentStart, ndx);
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
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					emitComment(commentStart, ndx);
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
		public void parse() {
			ndx++;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				emitComment(commentStart, ndx);
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
		public void parse() {
			ndx++;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				emitComment(commentStart, ndx);
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
		public void parse() {
			ndx++;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				emitComment(commentStart, ndx);
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
		public void parse() {
			int nameStartNdx = ndx;

			while (true) {
				ndx++;

				if (isEOF()) {
					errorEOF();
					state = DATA_STATE;
					doctype.setName(nameStartNdx, ndx);
					doctype.setQuirksMode(true);
					emitDoctype();
					return;
				}

				char c = input[ndx];

				if (equalsOne(c, TAG_WHITESPACES)) {
					state = AFTER_DOCUMENT_NAME;
					doctype.setName(nameStartNdx, ndx);
					return;
				}

				if (c == '>') {
					state = DATA_STATE;
					doctype.setName(nameStartNdx, ndx);
					emitDoctype();
					return;
				}
			}
		}
	};

	protected State AFTER_DOCUMENT_NAME = new State() {
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

				if (matchCaseInsensitiveWithUpper(_PUBLIC)) {		// todo check all matches usage if ignore case or not
					ndx += _PUBLIC.length - 1;
					state = AFTER_DOCTYPE_PUBLIC_KEYWORD;
					return;
				}
				if (matchCaseInsensitiveWithUpper(_SYSTEM)) {
					ndx += _SYSTEM.length - 1;
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
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					doctype.setPublicIdentifier(doctypeIdNameStart, ndx);
					errorEOF();
					state = DATA_STATE;
					doctype.setQuirksMode(true);
					emitDoctype();
				}

				char c = input[ndx];

				if (c == '\"') {
					doctype.setPublicIdentifier(doctypeIdNameStart, ndx);
					state = AFTER_DOCTYPE_PUBLIC_IDENTIFIER;
					return;
				}

				if (c == '>') {
					doctype.setPublicIdentifier(doctypeIdNameStart, ndx);
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
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					doctype.setPublicIdentifier(doctypeIdNameStart, ndx);
					errorEOF();
					state = DATA_STATE;
					doctype.setQuirksMode(true);
					emitDoctype();
				}

				char c = input[ndx];

				if (c == '\'') {
					doctype.setPublicIdentifier(doctypeIdNameStart, ndx);
					state = AFTER_DOCTYPE_PUBLIC_IDENTIFIER;
					return;
				}

				if (c == '>') {
					doctype.setPublicIdentifier(doctypeIdNameStart, ndx);
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
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					doctype.setSystemIdentifier(doctypeIdNameStart, ndx);
					errorEOF();
					state = DATA_STATE;
					doctype.setQuirksMode(true);
					emitDoctype();
				}

				char c = input[ndx];

				if (c == '\"') {
					doctype.setSystemIdentifier(doctypeIdNameStart, ndx);
					state = AFTER_DOCTYPE_SYSTEM_IDENTIFIER;
					return;
				}

				if (c == '>') {
					doctype.setSystemIdentifier(doctypeIdNameStart, ndx);
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
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					doctype.setSystemIdentifier(doctypeIdNameStart, ndx);
					errorEOF();
					state = DATA_STATE;
					doctype.setQuirksMode(true);
					emitDoctype();
				}

				char c = input[ndx];

				if (c == '\'') {
					doctype.setSystemIdentifier(doctypeIdNameStart, ndx);
					state = AFTER_DOCTYPE_SYSTEM_IDENTIFIER;
					return;
				}

				if (c == '>') {
					doctype.setSystemIdentifier(doctypeIdNameStart, ndx);
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

	protected State SCRIPT_DATA = new State() {
		public void parse() {

			while(true) {
				ndx++;

				if (isEOF()) {
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

	protected int scriptEndTagName = -1;

	protected State SCRIPT_DATA_END_TAG_OPEN = new State() {
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
		public void parse() {
			while (true) {
				ndx++;

				if (isEOF()) {
					state = SCRIPT_DATA;
					return;
				}

				char c = input[ndx];

				if (equalsOne(c, TAG_WHITESPACES)) {
					if (isAppropriateTagName(_SCRIPT, scriptEndTagName, ndx)) {
						state = BEFORE_ATTRIBUTE_NAME;
					} else {
						state = SCRIPT_DATA;
					}
					return;
				}
				if (c == '/') {
					if (isAppropriateTagName(_SCRIPT, scriptEndTagName, ndx)) {
						state = SELF_CLOSING_START_TAG;
					} else {
						state = SCRIPT_DATA;
					}
					return;
				}
				if (c == '>') {
					if (isAppropriateTagName(_SCRIPT, scriptEndTagName, ndx)) {
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

		protected State SCRIPT_DATA_ESCAPE_START = new State() {
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

		protected int doubleEscapedNdx = -1;

		protected State SCRIPT_DATA_ESCAPED_LESS_THAN_SIGN = new State() {
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
			public void parse() {
				while (true) {
					ndx++;

					if (isEOF()) {
						errorEOF();
						emitScript(scriptStartNdx, ndx);
						state = DATA_STATE;        // todo 8.2.4.22 -> order is not consistent, should be error first.
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
			public void parse() {
				ndx++;

				if (isEOF()) {
					state = SCRIPT_DATA_ESCAPED;
					return;
				}

				char c = input[ndx];

				if (isAlpha(c)) {
					//todo Create a new end tag token,
					state = SCRIPT_DATA_ESCAPED_END_TAG_NAME;
				}

				state = SCRIPT_DATA_ESCAPED;
			}
		};

		protected State SCRIPT_DATA_ESCAPED_END_TAG_NAME = new State() {
			public void parse() {
				while (true) {
					ndx++;

					if (isEOF()) {
						state = SCRIPT_DATA_ESCAPED;
						return;
					}

					char c = input[ndx];

					if (equalsOne(c, TAG_WHITESPACES)) {
						if (isAppropriateTagName(_SCRIPT, scriptEndTagName, ndx)) {
							state = BEFORE_ATTRIBUTE_NAME;
						} else {
							state = SCRIPT_DATA_ESCAPED;
						}
						return;
					}
					if (c == '/') {
						if (isAppropriateTagName(_SCRIPT, scriptEndTagName, ndx)) {
							state = SELF_CLOSING_START_TAG;
						} else {
							state = SCRIPT_DATA_ESCAPED;
						}
						return;
					}
					if (c == '>') {
						if (isAppropriateTagName(_SCRIPT, scriptEndTagName, ndx)) {
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
			public void parse() {
				while (true) {
					ndx++;

					if (isEOF()) {
						state = SCRIPT_DATA_ESCAPED;
						return;
					}

					char c = input[ndx];

					if (equalsOne(c, TAG_WHITESPACES_OR_END)) {
						if (isAppropriateTagName(_SCRIPT, doubleEscapedNdx, ndx)) {
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

		protected int doubleEscapedEndTag = -1;

		protected State SCRIPT_DATA_DOUBLE_ESCAPED_LESS_THAN_SIGN = new State() {
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
						if (isAppropriateTagName(_SCRIPT, doubleEscapedEndTag, ndx)) {
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

		protected void reset() {
			xmlAttrCount = 0;
			xmlAttrStartNdx = -1;
			version = encoding = standalone = null;
		}

		protected State XML_BETWEEN = new State() {
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
							if (match(_XML_VERSION)) {
								ndx += _XML_VERSION.length - 1;
								state = AFTER_XML_ATTRIBUTE_NAME;
								return;
							}
							break;
						case 1:
							if (match(_XML_ENCODING)) {
								ndx += _XML_ENCODING.length - 1;
								state = AFTER_XML_ATTRIBUTE_NAME;
								return;
							}
							break;
						case 2:
							if (match(_XML_STANDALONE)) {
								ndx += _XML_STANDALONE.length - 1;
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

					if (c == '\"') {
						state = XML_ATTRIBUTE_VALUE;
						return;
					}

					errorInvalidToken();
					state = DATA_STATE;
					return;
				}
			}
		};

		protected State XML_ATTRIBUTE_VALUE = new State() {
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

					if (c == '\"') {
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
			public void parse() {
				ndx++;

				int cdataEndNdx = find(_CDATA_END, ndx, total);

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

	/**
	 * Emits characters into the local buffer.
	 * Text will be emitted only on {@link #flushText()}.
	 */
	protected void textEmitChar(char c) {
		if (textLen == text.length) {
			ArraysUtil.resize(text, textLen << 1);
		}
		text[textLen] = c;
		textLen++;
	}

	/**
	 * Resets text buffer.
	 */
	protected void textStart() {
		textLen = 0;
	}

	protected void textEmitChars(int from, int to) {
		while (from < to) {
			textEmitChar(input[from]);
			from++;
		}
	}

	protected void textEmitChars(char[] buffer) {
		for (char aBuffer : buffer) {
			textEmitChar(aBuffer);
		}
	}

	protected CharBuffer textWrap() {	// todo detect 0
		char[] textToEmit = new char[textLen];
		System.arraycopy(text, 0, textToEmit, 0, textLen);
		return CharBuffer.wrap(textToEmit); 	// todo wrap or toString()
	}


	// ---------------------------------------------------------------- attr

	protected int attrStartNdx = -1;
	protected int attrEndNdx = -1;

	private void _addAttribute() {
		_addAttribute(substring(attrStartNdx, attrEndNdx), null);
	}

	private void _addAttributeWithValue() {
		_addAttribute(substring(attrStartNdx, attrEndNdx), textWrap().toString());
	}

	private void _addAttribute(String attrName, String attrValue) {
		if (tag.hasAttribute(attrName, false)) {
			_error("Ignored duplicated attribute: " + attrName);
		} else {
			tag.addAttribute(attrName, attrValue);
		}
		attrStartNdx = -1;
		attrEndNdx = -1;
		textLen = 0;
	}

	protected void emitTag() {
		tag.end(ndx + 1);

		if (tag.getType().isStartingTag()) {

			if (tag.matchTagName(_SCRIPT)) {
				scriptStartNdx = ndx + 1;
				state = SCRIPT_DATA;
				return;
			}

			// detect RAWTEXT tags

			for (char[] rawtextTagName : RAWTEXT_TAGS) {
				if (tag.matchTagName(rawtextTagName)) {
					state = RAWTEXT;
					rawTextStart = ndx + 1;
					rawTagName = rawtextTagName;
					break;
				}
			}

			// detect RCDATA tag

			for (char[] rcdataTextTagName : RCDATA_TAGS) {
				if (tag.matchTagName(rcdataTextTagName)) {
					state = RCDATA;
					rcdataTagStart = ndx + 1;
					rcdataTagName = rcdataTextTagName;
					break;
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
	protected void emitComment(int from, int to) {
		if (enableConditionalComments) {
			// CC: downlevel-hidden starting
			if (match(_CC_IF, from)) {
				int endBracketNdx = find(']', from + 3, to);

				CharSequence expression = charSequence(from + 1, endBracketNdx);

				ndx = endBracketNdx + 1;
				// todo check the '>'

				visitor.condComment(expression, true, true, false);

				state = DATA_STATE;
				return;
			}

			if (match(_CC_ENDIF2, to - _CC_ENDIF2.length)) {
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

	protected void emitScript(int from, int to) {
		tag.increaseDeepLevel();

		visitor.script(tag, substring(from, to));		// todo da li za script() treba specijalna visit metoda kao sto sada ima?

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

	protected void emitCData(CharSequence charSequence) {
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
	 * todo in the error message, add text that surrounds the error position
	 */
	protected void _error(String message) {
		int position = ndx;

		if (calculatePosition) {
			Position currentPosition = position();
			message = message
					.concat(StringPool.SPACE)
					.concat(currentPosition.toString());
		} else {
			message = message
					.concat(" [@")
					.concat(Integer.toString(position))
					.concat(StringPool.RIGHT_SQ_BRACKET);
		}

		visitor.error(message);
	}

	// ---------------------------------------------------------------- util

	private boolean isAppropriateTagName(char[] lowerCaseNameToMatch, int from, int to) {
		int len = to - from;

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

	// ---------------------------------------------------------------- const data

	protected State state = DATA_STATE;

	public static final char[] TAG_WHITESPACES = new char[] {'\t', '\n', '\r', ' '};	//todo why publici?
	private static final char[] TAG_WHITESPACES_OR_END = new char[] {'\t', '\n', '\r', ' ', '/', '>'};
	private static final char[] CONTINUE_CHARS = new char[] {'\t', '\n', '\r', ' ', '<', '&'};
	private static final char[] ATTR_INVALID_1 = new char[] {'\"', '\'', '<', '='};
	private static final char[] ATTR_INVALID_2 = new char[] {'\"', '\'', '<'};
	private static final char[] ATTR_INVALID_3 = new char[] {'<', '=', '`'};
	private static final char[] ATTR_INVALID_4 = new char[] {'"', '\'', '<', '=', '`'};
	private static final char[] _COMMENT_DASH = new char[] {'-', '-'};
	private static final char[] _DOCTYPE = new char[] {'D', 'O', 'C', 'T', 'Y', 'P', 'E'};
	private static final char[] _SCRIPT = new char[] {'s', 'c', 'r', 'i', 'p', 't'};
	private static final char[] _XMP = new char[] {'x', 'm', 'p'};
	private static final char[] _STYLE = new char[] {'s', 't', 'y', 'l', 'e'};
	private static final char[] _IFRAME = new char[] {'i', 'f', 'r', 'a', 'm', 'e'};
	private static final char[] _NOFRAMES = new char[] {'n', 'o', 'f', 'r', 'a', 'm', 'e', 's'};
	private static final char[] _NOEMBED = new char[] {'n', 'o', 'e', 'm', 'b', 'e', 'd'};
	private static final char[] _NOSCRIPT = new char[] {'n', 'o', 's', 'c', 'r', 'i', 'p', 't'};
	private static final char[] _TEXTAREA = new char[] {'t', 'e', 'x', 't', 'a', 'r', 'e', 'a'};
	private static final char[] _TITLE = new char[] {'t', 'i', 't', 'l', 'e'};
	private static final char[] _PUBLIC = new char[] {'P', 'U', 'B', 'L', 'I', 'C'};
	private static final char[] _SYSTEM = new char[] {'S', 'Y', 'S', 'T', 'E', 'M'};
	private static final char[] _CDATA = new char[] {'[', 'C', 'D', 'A', 'T', 'A', '['};
	private static final char[] _CDATA_END = new char[] {']', ']', '>'};

	private static final char[] _XML = new char[] {'?', 'x', 'm', 'l'};
	private static final char[] _XML_VERSION = new char[] {'v', 'e', 'r', 's', 'i', 'o', 'n'};
	private static final char[] _XML_ENCODING = new char[] {'e', 'n', 'c', 'o', 'd', 'i', 'n', 'g'};
	private static final char[] _XML_STANDALONE = new char[] {'s', 't', 'a', 'n', 'd', 'a', 'l', 'o', 'n', 'e'};

	private static final char[] _CC_IF = new char[] {'[', 'i', 'f', ' '};
	private static final char[] _CC_ENDIF = new char[] {'[', 'e', 'n', 'd', 'i', 'f', ']'};
	private static final char[] _CC_ENDIF2 = new char[] {'<', '!', '[', 'e', 'n', 'd', 'i', 'f', ']'};
	private static final char[] _CC_END = new char[] {']', '>'};


	// 'script' is handled by its states todo check this!
	private static final char[][] RAWTEXT_TAGS = new char[][] {		// CDATA
			_XMP, _STYLE, _IFRAME, _NOEMBED, _NOFRAMES, _NOSCRIPT,
	};

	private static final char[][] RCDATA_TAGS = new char[][] {
			_TEXTAREA, _TITLE
	};

	protected static final char REPLACEMENT_CHAR = '\uFFFD';
	protected static final char[] INVALID_CHARS = new char[] {'\u000B', '\uFFFE', '\uFFFF'};
	//, '\u1FFFE', '\u1FFFF', '\u2FFFE', '\u2FFFF', '\u3FFFE', '\u3FFFF', '\u4FFFE,
	//	'\u4FFFF', '\u5FFFE', '\u5FFFF', '\u6FFFE', '\u6FFFF', '\u7FFFE', '\u7FFFF', '\u8FFFE', '\u8FFFF', '\u9FFFE,
	//	'\u9FFFF', '\uAFFFE', '\uAFFFF', '\uBFFFE', '\uBFFFF', '\uCFFFE', '\uCFFFF', '\uDFFFE', '\uDFFFF', '\uEFFFE,
	//	'\uEFFFF', '\uFFFFE', '\uFFFFF', '\u10FFFE', '\u10FFFF',

	private static final CharSequence _ENDIF = "endif";

}