// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.util.CharUtil;
import jodd.util.StringPool;
import jodd.util.UnsafeUtil;

import static jodd.util.CharUtil.equalsOne;
import static jodd.util.CharUtil.isAlpha;

/**
 * HTML/XML content parser using {@link TagVisitor} for callbacks.
 * Differences from: http://www.w3.org/TR/html5/
 * <ul>
 * <li>no {@code &} parsing in DATA_STATE.
 * <li>no check for ` in some attributes state // todo check this
 * <li>tag name case is not changed
 */
public class LagartoParser2 extends CharScanner {

	protected TagVisitor visitor;
	protected LagartoParserContext ctx;
	protected ParsedTag tag;

	/**
	 * Creates parser on char array.
	 */
	public LagartoParser2(char[] charArray) {
			initialize(charArray);
		}

	/**
	 * Creates parser on a String.
	 */
	public LagartoParser2(String string) {
			initialize(UnsafeUtil.getChars(string));
		}

	/**
	 * Initializes parser.
	 */
	protected void initialize(char[] input) {
		super.initialize(input);
		textStartNdx = textEndNdx = -1;
		this.ctx = new LagartoParserContext();
		this.tag = new ParsedTag(input);
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

		flushText();

		visitor.end();
	}

	// ---------------------------------------------------------------- start & end


	/**
	 * Optimized data state.
	 */
	protected State DATA_STATE =  new State() {
		public void parse() {
			ndx++;

			if (isEOF()) {
				parsing = false;
				return;
			}

			int tagStartNdx = find('<');

			if (tagStartNdx == -1) {
				tagStartNdx = total;
			}

			emitText(ndx, tagStartNdx);
			ndx = tagStartNdx;

			if (!isEOF()) {
				state = TAG_OPEN;
			} else {
				parsing = false;
			}
		}
	};

	protected State TAG_OPEN = new State() {
		public void parse() {
			tag.start(ndx);

			ndx++;

			if (isEOF()) {
				errorEOF();
				state = DATA_STATE;
				emitText(ndx - 1, ndx);
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
			if (c == '?') {
				errorInvalidToken();
				state = BOGUS_COMMENT;
				return;
			}

			errorInvalidToken();
			state = DATA_STATE;
			emitText(ndx - 1, ndx);
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

	protected State AFTER_ATTRIBUTE_NAME  = new State() {
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
				if (c == '>') {
					errorInvalidToken();
					state = DATA_STATE;
					//todo emitText(ndx);
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
			attrValueStartNdx = ndx;

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

				if (c == '>') {
					_addAttributeWithValue();
					state = DATA_STATE;
					emitTag();
					return;
				}

				if (equalsOne(c, ATTR_INVALID_4)) {
					errorInvalidToken();
				}
			}
		}
	};

	protected State ATTR_VALUE_SINGLE_QUOTED = new State() {
		public void parse() {
			attrValueStartNdx = ndx + 1;

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
				// append
			}
		}
	};

	protected State ATTR_VALUE_DOUBLE_QUOTED = new State() {
		public void parse() {
			attrValueStartNdx = ndx + 1;

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
				// append
			}
		}
	};


	protected State AFTER_ATTRIBUTE_VALUE_QUOTED = new State() {
		public void parse() {
			attrValueStartNdx = ndx;

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
			}
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
			int tagEndNdx = find('>');

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

			if (match(COMMENT_DASH, false)) {
				state = COMMENT_START;
				ndx++;
				return;
			}

			//if (match(DOCTYPE))		// todo DOCTYPE

			errorInvalidToken();
			state = BOGUS_COMMENT;
		}
	};

	// ---------------------------------------------------------------- RAWTEXT

	protected int rawTextStart;		// todo prodji sve varijable i vidi da li se koriste!
	protected char[] rawTagName;
	protected int rawTextEnd;

	protected State RAWTEXT = new State() {
		public void parse() {
			rawTextStart = ndx + 1;

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

			if (!isEOF()) {
				char c = input[ndx];

				if (c == '/') {
					state = RAWTEXT_END_TAG_OPEN;
					return;
				}
			}

			state = RAWTEXT;
		}
	};

	protected State RAWTEXT_END_TAG_OPEN = new State() {
		public void parse() {
			ndx++;

			if (!isEOF()) {
				char c = input[ndx];
				if (isAlpha(c)) {
					state = RAWTEXT_END_TAG_NAME;
					return;
				}
			}

			state = RAWTEXT;
		}
	};

	int rawtextEndTagNameStartNdx = -1;

	protected State RAWTEXT_END_TAG_NAME = new State() {
		public void parse() {
			rawtextEndTagNameStartNdx = ndx;

			while (true) {
				ndx++;

				if (!isEOF()) {
					char c = input[ndx];

					if (equalsOne(c, TAG_WHITESPACES)) {
						if (isAppropriateTagName(rawTagName, rawtextEndTagNameStartNdx, ndx)) {
							emitText(rawTextStart, rawTextEnd);
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
							emitText(rawTextStart, rawTextEnd);
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
							emitText(rawTextStart, rawTextEnd);
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
				}

				state = RAWTEXT;
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
					scriptEscape = new SriptEscape();
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
					if (isAppropriateTagName(SCRIPT, scriptEndTagName, ndx)) {
						state = BEFORE_ATTRIBUTE_NAME;
					} else {
						state = SCRIPT_DATA;
					}
					return;
				}
				if (c == '/') {
					if (isAppropriateTagName(SCRIPT, scriptEndTagName, ndx)) {
						state = SELF_CLOSING_START_TAG;
					} else {
						state = SCRIPT_DATA;
					}
					return;
				}
				if (c == '>') {
					if (isAppropriateTagName(SCRIPT, scriptEndTagName, ndx)) {
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

	protected SriptEscape scriptEscape = null;

	/**
	 * Since escaping states inside the SCRIPT tag are rare, we want to use them
	 * lazy, only when really needed. Therefore, they are all grouped inside separate
	 * class that will be instantiated only if needed.
	 */
	protected class SriptEscape {

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
						if (isAppropriateTagName(SCRIPT, scriptEndTagName, ndx)) {
							state = BEFORE_ATTRIBUTE_NAME;
						} else {
							state = SCRIPT_DATA_ESCAPED;
						}
						return;
					}
					if (c == '/') {
						if (isAppropriateTagName(SCRIPT, scriptEndTagName, ndx)) {
							state = SELF_CLOSING_START_TAG;
						} else {
							state = SCRIPT_DATA_ESCAPED;
						}
						return;
					}
					if (c == '>') {
						if (isAppropriateTagName(SCRIPT, scriptEndTagName, ndx)) {
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
						if (isAppropriateTagName(SCRIPT, doubleEscapedNdx, ndx)) {
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

				if (!isEOF()) {                        // todo implement this pattern everywhere possible to reduce code!
					char c = input[ndx];

					if (c == '/') {
						state = SCRIPT_DATA_DOUBLE_ESCAPE_END;
						return;
					}
				}

				state = SCRIPT_DATA_DOUBLE_ESCAPED;
			}
		};

		protected State SCRIPT_DATA_DOUBLE_ESCAPE_END = new State() {
			public void parse() {
				doubleEscapedEndTag = ndx + 1;

				while (true) {
					ndx++;

					if (!isEOF()) {
						char c = input[ndx];

						if (equalsOne(c, TAG_WHITESPACES_OR_END)) {
							if (isAppropriateTagName(SCRIPT, doubleEscapedEndTag, ndx)) {
								state = SCRIPT_DATA_ESCAPED;
							} else {
								state = SCRIPT_DATA_DOUBLE_ESCAPED;
							}
							return;
						}
						if (isAlpha(c)) {
							continue;
						}
					}

					state = SCRIPT_DATA_DOUBLE_ESCAPED;
					return;
				}
			}
		};
	}

	// ---------------------------------------------------------------- emit

	protected int textStartNdx = -1;
	protected int textEndNdx = -1;
	protected int attrStartNdx = -1;
	protected int attrEndNdx = -1;
	protected int attrValueStartNdx = -1;

	private void _addAttribute() {
		tag.addAttribute(substring(attrStartNdx, attrEndNdx), null);
		attrStartNdx = -1;
		attrEndNdx = -1;
	}

	private void _addAttributeWithValue() {
		tag.addAttribute(substring(attrStartNdx, attrEndNdx), substring(attrValueStartNdx, ndx));
		attrStartNdx = -1;
		attrEndNdx = -1;
		attrValueStartNdx = -1;
	}

	protected void emitTag() {
		flushText();

		tag.end(ndx + 1);

		if (tag.getType().isStartingTag()) {

			if (tag.matchTagName(SCRIPT)) {
				scriptStartNdx = ndx + 1;
				state = SCRIPT_DATA;
				return;
			}

			// detect RAWTEXT tags

			if (tag.matchTagName(XMP)) {
				state = RAWTEXT;
				rawTagName = XMP;
			}

			tag.increaseDeepLevel();
		}

		visitor.tag(tag);

		if (tag.getType().isEndingTag()) {
			tag.decreaseDeepLevel();
		}
	}

	protected void emitComment(int from, int to) {
		flushText();

		CharSequence comment = charSequence(from, to);
		visitor.comment(comment);

		commentStart = -1;
	}


	/**
	 * Consumes text from current index to given index.
	 * Pointer moves to a new location.
	 */
	protected void emitText(int from, int to) {
		if (textStartNdx != -1) {
			if (from != textEndNdx) {
				flushText();	// previous block is not continuous, flush it
				textStartNdx = from;
			}
		} else {
			textStartNdx = from;
		}
		textEndNdx = to;
	}

	/**
	 * Flushes text buffer. Does nothing if buffer does not exist
	 * or it is empty. Must be called before every non-text visit method!
	 */
	protected void flushText() {
		if (textStartNdx == -1) {
			return;		// nothing to flush
		}

		if (textStartNdx != textEndNdx) {
			visitor.text(charSequence(textStartNdx, textEndNdx));
		}

		textStartNdx = -1;
	}

	protected void emitScript(int from, int to) {
		flushText();

		tag.increaseDeepLevel();

		visitor.script(tag, substring(from, to));		// todo da li ovo treba specijalno?

		tag.decreaseDeepLevel();
		scriptStartNdx = -1;
		scriptEndNdx = -1;
	}

	// ---------------------------------------------------------------- error

	protected void errorEOF() {
		_error("Parse error: EOF");
	}

	protected void errorInvalidToken() {
		_error("Parse error: invalid token");
	}

	/**
	 * Prepares error message and reports it to the visitor.
	 * todo add text that surrounds the error position
	 */
	protected void _error(String message) {
		flushText();

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

	public static final char[] TAG_WHITESPACES = new char[] {'\t', '\n', '\r', ' '};
	private static final char[] ATTR_INVALID_1 = new char[] {'\"', '\'', '<', '='};
	private static final char[] ATTR_INVALID_2 = new char[] {'\"', '\'', '<'};
	private static final char[] ATTR_INVALID_3 = new char[] {'<', '=', '`'};
	private static final char[] ATTR_INVALID_4 = new char[] {'"', '\'', '<', '=', '`'};
	private static final char[] COMMENT_DASH = new char[] {'-', '-'};
	private static final char[] DOCTYPE = new char[] {'D', 'O', 'C', 'T', 'Y', 'P', 'E'};
	private static final char[] SCRIPT = new char[] {'s', 'c', 'r', 'i', 'p', 't'};
	private static final char[] XMP = new char[] {'x', 'm', 'p'};
	private static final char[] TAG_WHITESPACES_OR_END = new char[] {'\t', '\n', '\r', ' ', '/', '>'};

}