// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.util.StringPool;
import jodd.util.StringUtil;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

import java.io.IOException;

import static jodd.lagarto.LagartoParserUtil.subSequence;

/**
 * Lagarto HTML/XML parser engine. Usage consist of two steps:
 * <ul>
 * <li>{@link #initialize(char[])} initialization} with provided content</li>
 * <li>actual {@link #parse(TagVisitor) parsing} the content</li>
 * </ul>
 */
public abstract class LagartoParserEngine {
	private static final Logger log = LoggerFactory.getLogger(LagartoParserEngine.class);

	private static final char[] COMMENT_IF = "[if".toCharArray();
	private static final char[] COMMENT_ENDIF = "<![endif]".toCharArray();

	private char[] input;
	private LagartoLexer lexer;
	private ParsedTag tag;
	private LagartoParserContext ctx;
	private TagVisitor visitor;

	private Token lastToken = Token.UNKNOWN;
	private CharSequence lastText;

	private boolean buffering;
	private int buffTextStart;
	private int buffTextEnd;

	// ---------------------------------------------------------------- init

	/**
	 * Initializes parser engine by providing the content.
	 */
	protected void initialize(char[] input) {
		this.input = input;
		this.lexer = new LagartoLexer(input);
		this.tag = new ParsedTag(lexer);
		this.ctx = new LagartoParserContext();

		this.buffering = false;
		this.buffTextStart = 0;
		this.buffTextEnd = 0;
		this.lastText = null;
		this.lastToken = Token.UNKNOWN;
	}

	// ---------------------------------------------------------------- properties

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
	 * Resolves current position on {@link #error(StringBuilder)} parsing errors}
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

	// ---------------------------------------------------------------- lexer properties

	protected boolean parseSpecialTagsAsCdata = true;

	/**
	 * Specifies if special tags should be parsed as CDATA block.
	 */
	public void setParseSpecialTagsAsCdata(boolean parseSpecialTagsAsCdata) {
		this.parseSpecialTagsAsCdata = parseSpecialTagsAsCdata;
	}

	public boolean isParseSpecialTagsAsCdata() {
		return this.parseSpecialTagsAsCdata;
	}

	// ---------------------------------------------------------------- parse

	/**
	 * Parses provided content.
	 */
	protected void parse(TagVisitor visitor) {
		this.visitor = visitor;

		this.ctx.startTime = System.currentTimeMillis();
		log.debug("parsing started");

		try {
			parse();
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		} finally {
			this.ctx.endTime = System.currentTimeMillis();
			this.ctx.elapsedTime = this.ctx.endTime - this.ctx.startTime;

			if (log.isDebugEnabled()) {
				log.debug("parsing done in " + this.ctx.elapsedTime + "ms.");
			}
		}
	}

	// ---------------------------------------------------------------- main loop

	/**
	 * Main parsing loop that process lexer tokens from input.
	 */
	protected void parse() throws IOException{
		// set lexer properties
		lexer.setParseSpecialTagsAsCdata(this.parseSpecialTagsAsCdata);

		// start
		visitor.start(ctx);

		while (true) {
			Token token = nextToken();
			switch (token) {
				case EOF:
					flushText();
					visitor.end();
					return;
				case COMMENT:
					parseCommentOrConditionalComment();
					break;
				case CDATA:
					parseCDATA();
					break;
				case DOCTYPE:
					parseDoctype();
					break;
				case TEXT:
					int start = lexer.position();
					parseText(start, start + lexer.length());
					break;
				case LT:
					parseTag(token, TagType.START);
					break;
				case XML_LT:
					parseTag(token, TagType.START);
					break;
				case CONDITIONAL_COMMENT_START:
					parseRevealedCCStart();
					break;
				case CONDITIONAL_COMMENT_END:
					parseCCEnd();
					break;
				default:
					error(new StringBuilder("Unexpected root token: ").append(token));
					break;
			}
		}
	}

	// ---------------------------------------------------------------- main methods

	/**
	 * Flushes buffered text and stops buffering.
	 */
	protected void flushText() {
		if (buffering) {
			ctx.offset = buffTextStart;
			visitor.text(subSequence(input, buffTextStart, buffTextEnd));
			buffering = false;
		}
	}

	/**
	 * Buffers the parsed text. Buffered text will be consumed
	 * on the very next {@link #flushText()}.
	 */
	protected void parseText(int start, int end) {
		if (!buffering) {
			buffering = true;
			buffTextStart = start;
			buffTextEnd = end;
		} else {
			if (buffTextEnd != start) {
				throw new LagartoException();
			}
			buffTextEnd = end;
		}
	}

	/**
	 * Parses HTML comments. Detect IE hidden conditional comments, too.
	 */
	protected void parseCommentOrConditionalComment() throws IOException {
		flushText();

		int lexerPosition = lexer.position();
		int start = lexerPosition + 4;		// skip "<!--"
		int end = start + lexer.length() - 7;	// skip "-->"

		if (
				(enableConditionalComments) &&
				(LagartoParserUtil.regionStartWith(input, start, end, COMMENT_IF))
		){
			// conditional comment start

			int expressionEnd = LagartoParserUtil.regionIndexOf(input, start + 3, end, ']');

			int ccend = expressionEnd + 2;

			CharSequence additionalComment = null;

			// cc start tag ends either with "]>" or at very next "-->"

			int commentEnd = LagartoParserUtil.regionIndexOf(input, ccend, end, COMMENT_ENDIF);

			if (commentEnd == -1) {
				additionalComment = subSequence(input, ccend, end + 3);
			}

			ctx.offset = lexerPosition;
			visitor.condComment(subSequence(input, start + 1, expressionEnd), true, true, additionalComment);

			// calculate push back to the end of the starting tag

			if (additionalComment == null) {
				int pushBack = lexer.position() + lexer.length();

				pushBack -= ccend;

				lexer.yypushback(pushBack);
			}

			return;
		}

		ctx.offset = lexerPosition;
		visitor.comment(subSequence(input, start, end));
	}

	/**
	 * Parses CDATA.
	 */
	protected void parseCDATA() throws IOException {
		flushText();

		int position = lexer.position();

		int start = position + 9;
		int end = start + lexer.length() - 12;

		ctx.offset = position;
		visitor.cdata(subSequence(input, start, end));
	}

	/**
	 * Parses HTML DOCTYPE directive.
	 */
	protected void parseDoctype() throws IOException {
		int start = lexer.position();

		flushText();
		skipWhiteSpace();

		String name = null;
		boolean isPublic = false;
		String publicId = null;
		String uri = null;

		int i = 0;
		while (true) {
			skipWhiteSpace();
			Token token = nextToken();
			if (token == Token.GT) {
				break;
			}

			switch (i) {
				case 0:
					name = textString();
					break;
				case 1:
					if (textString().equals("PUBLIC")) {
						isPublic = true;
					}
					break;
				case 2:
					if (isPublic) {
						publicId = lexer.yytext(1, 1);
					} else {
						uri = lexer.yytext(1, 1);
						break;
					}
					break;
				case 3:
					uri = lexer.yytext(1, 1);
					break;
			}
			i++;
		}

		ctx.offset = start;
		visitor.doctype(name, publicId, uri);
	}

	/**
	 * Parses revealed conditional comment start.
	 * Downlevel-hidden conditional comment is detected in
	 * {@link #parseCommentOrConditionalComment()}.
	 */
	protected void parseRevealedCCStart() throws IOException {
		flushText();

		if (!enableConditionalComments) {
			error(new StringBuilder("Conditional comments disabled"));
			return;
		}

		int start = lexer.position();
		int end = start + lexer.length();
		int textStart = start;
		int textEnd = end;

		int i = start + 2;
		while (i < end) {
			if (input[i] == '[') {
				i++;
				textStart = i;
				continue;
			}
			if (input[i] == ']') {
				textEnd = i;
				break;
			}
			i++;
		}

		ctx.offset = start;
		visitor.condComment(subSequence(input, textStart, textEnd), true, false, null);
	}

	/**
	 * Parses conditional comment end.
	 */
	protected void parseCCEnd() throws IOException {
		flushText();

		int start = lexer.position();
		int end = start + lexer.length();
		int textStart = start;
		int textEnd = end;

		int i = start + 2;
		while (i < end) {
			if (input[i] == '[') {
				i++;
				textStart = i;
				continue;
			}
			if (input[i] == ']') {
				textEnd = i;
				break;
			}
			i++;
		}

		boolean isDownlevelHidden = (end - textEnd) == 4;
		boolean hasExtra = (textStart - start) > 3;

		if (!enableConditionalComments) {
			if (isDownlevelHidden) {
				// +4 and -3 to skip the <!-- and the --> the same way the parseCommentOrConditionalComment() method does.
				ctx.offset = start;
				visitor.comment(subSequence(input, start + 4, end - 3));
			} else {
				error(new StringBuilder("Conditional comments disabled"));
			}
			return;
		}

		CharSequence additionalComment = null;
		if (hasExtra) {
			additionalComment = subSequence(input, start, textStart - 3);
		}

		ctx.offset = start;
		visitor.condComment(subSequence(input, textStart, textEnd), false, isDownlevelHidden, additionalComment);
	}

	/**
	 * Parse tag starting from "&lt;".
	 */
	protected void parseTag(Token tagToken, TagType type) throws IOException {
		final int start = lexer.position();

		try {
			skipWhiteSpace();
			Token token;
			token = nextToken();

			// if token is not a special tag ensure
			// not to scan for special tag name from now on
			if (lexer.nextTagState == -1) {
				lexer.nextTagState = -2;
			}

			if (token == Token.SLASH) {		// it is closing tag
				type = TagType.END;
				token = nextToken();
			}

			switch (token) {
				case WORD:					// tag name
					String tagName = textString();
					if (acceptTag(tagName)) {
						parseTagAndAttributes(tagToken, tagName, type, start);
					} else {
						// step back and parse tag as text
						parseAsText(start);
					}
					break;
				case GT:	// illegal tag (<>), consume it as text
					parseText(start, lexer.position() + 1);
					break;
				case EOF:	// eof, consume it as text
					parseText(start, lexer.position());
					break;
				default:
					error(new StringBuilder("Invalid token in tag <").append(text()).append('>'));

					// step back and parse tag as text
					parseAsText(start);
					break;
			}
		} catch (LagartoException lex) {
			// if exception occurs during tag parsing
			// step back and parse tag as text
			error(new StringBuilder(lex.getMessage()));

			parseAsText(start);
		}
	}

	/**
	 * Resets current state, steps back and parses content
	 * as text, starting from provided position.
	 */
	protected void parseAsText(int start) throws IOException {
		lexer.stateReset();
		stepBack(lexer.nextToken());
		parseText(start, lexer.position());
	}

	/**
	 * Returns <code>true</code> if some tag has to be parsed.
	 * User may override this method to gain more control over what should be parsed.
	 * May be used in situations where only few specific tags has to be parsed
	 * (e.g. just title and body).
	 */
	@SuppressWarnings({"UnusedParameters"})
	protected boolean acceptTag(String tagName) {
		return true;
	}

	/**
	 * Parses full tag.
	 */
	protected void parseTagAndAttributes(Token tagToken, String tagName, TagType type, int start) throws IOException {
		tag.startTag(tagName);

		Token token;

loop:	while (true) {
			skipWhiteSpace();
			token = nextToken();
			stepBack(token);

			switch (token) {
				case GT:
					break loop;
				case XML_GT:
					break loop;
				case WORD:
					parseAttribute();
					break;
				case EOF:
					parseText(start, lexer.position());
					return;
				case SLASH:
					nextToken();
					// check if next token is GT
					Token nextToken = nextToken();
					stepBack(nextToken);
					if (nextToken == Token.GT) {
						type = TagType.SELF_CLOSING;	// an empty tag, no body
						break loop;
					}
					break;
				default:
					// unexpected token, try to skip it!
					String tokenText;
					CharSequence charSequence = text();
					if (charSequence != null) {
						tokenText = charSequence.toString();
					} else {
						tokenText = lexer.yytext();
					}

					error(new StringBuilder("Tag <").append(tagName).append("> invalid token: ").append(tokenText));
					nextToken();	// there was a stepBack, so move forward
					if (tokenText.length() > 1) {
						lexer.yypushback(tokenText.length() - 1);
					}
					break;
			}
		}

		token = nextToken();

		// since the same method is used for XML directive and for TAG, check
		if (tagToken == Token.LT && token == Token.XML_GT) {
			token = Token.GT;
			error(new StringBuilder("Unmatched tag <").append(tagName).append("?>"));
		} else if (tagToken == Token.XML_LT && token == Token.GT) {
			token = Token.XML_GT;
			error(new StringBuilder("Unmatched tag <?").append(tagName).append('>'));
		}

		switch (token) {
			default:
				error(new StringBuilder("Expected end of tag for <").append(tagName).append('>'));
				// continue as tag
//				onTag(type, tagName, start, lexer.position() - start + 1);
//				break;
			case GT:	// end of tag, process it
				flushText();

				int len = lexer.position() - start + 1;

				if (type.isStartingTag()) {
					// parse special tags
					final int nextTagState = lexer.getNextTagState();
					if (nextTagState > 0) {
						tag.defineTag(type, start, len);
						tag.increaseDeepLevel();
						parseSpecialTag(nextTagState);
						tag.decreaseDeepLevel();
						break;
					}
				}

				// default tag
				tag.defineTag(type, start, len);
				if (type.isStartingTag()) {
					tag.increaseDeepLevel();
				}

				ctx.offset = start;
				visitor.tag(tag);

				if (type.isEndingTag()) {
					tag.decreaseDeepLevel();
				}
				break;
			case XML_GT:
				flushText();
				int len2 = lexer.position() - start + 2;
				tag.defineTag(type, start, len2);
				tag.setTagMarks("<?", "?>");
				tag.increaseDeepLevel();

				ctx.offset = start;
				visitor.xml(tag);

				tag.decreaseDeepLevel();
				break;
			case EOF:
				parseText(start, lexer.position());
				break;
		}
	}

	/**
	 * Parses single attribute.
	 */
	protected void parseAttribute() throws IOException {
		nextToken();
		String attributeName = textString();
		skipWhiteSpace();

		Token token = nextToken();
		if (token == Token.EQUALS) {
			skipWhiteSpace();
			token = nextToken();

			if (token == Token.QUOTE) {
				CharSequence charSequence = text();
				char quote = charSequence.charAt(0);

				charSequence = charSequence.subSequence(1, charSequence.length() - 1);
				String attributeValue = charSequence.toString();
				if (quote == '\'') {
					attributeValue = StringUtil.replace(attributeValue, StringPool.QUOTE, StringPool.HTML_QUOTE);
				}
				tag.addAttribute(attributeName, attributeValue);
			} else if (token == Token.WORD || token == Token.SLASH) {
				// attribute value is not quoted, take everything until the space or tag end as a value
				String attributeValue = textString();
				while (true) {
					Token next = nextToken();
					if (next != Token.WHITESPACE && next != Token.GT) {
						attributeValue += text();	// rare, keep joining attribute value with tokens
					} else {
						stepBack(next);
						break;
					}
				}
				tag.addAttribute(attributeName, attributeValue);
			} else if (token == Token.GT) {
				stepBack(token);
			} else if (token != Token.EOF) {
				error(new StringBuilder("Invalid attribute: ").append(attributeName));
			}
		} else if (token == Token.SLASH || token == Token.GT || token == Token.WORD) {
			tag.addAttribute(attributeName, null);
			stepBack(token);
		} else if (token == Token.QUOTE) {
			error(new StringBuilder("Orphan attribute: ").append(text()));
			tag.addAttribute(attributeName, null);
		} else if (token != Token.EOF) {
			error(new StringBuilder("Invalid attribute: ").append(attributeName));
		}
	}

	/**
	 * Parses special tags.
	 */
	protected void parseSpecialTag(int state) throws IOException {

		int start = lexer.position() + 1;
		nextToken();
		int end = start + lexer.length();
		switch(state) {
			case Lexer.XMP:
				ctx.offset = tag.getTagPosition();
				visitor.xmp(tag, subSequence(input, start, end - 6));
				break;
			case Lexer.SCRIPT:
				ctx.offset = tag.getTagPosition();
				visitor.script(tag, subSequence(input, start, end - 9));
				break;
			case Lexer.STYLE:
				ctx.offset = tag.getTagPosition();
				visitor.style(tag, subSequence(input, start, end - 8));
				break;
		}
	}

	// ---------------------------------------------------------------- utilities

	/**
	 * Step back lexer position.
	 */
	private void stepBack(Token next) {
		if (lastToken != Token.UNKNOWN) {
			throw new LagartoException("Only one step back allowed.");
		}
		lastToken = next;
		if (next == Token.WORD || next == Token.QUOTE || next == Token.SLASH || next == Token.EQUALS) {
			lastText = lexer.xxtext();
		} else {
			lastText = null;
		}
	}

	/**
	 * Returns the next token from lexer or previously fetched token.
	 */
	protected Token nextToken() throws IOException {
		Token next;
		if (lastToken == Token.UNKNOWN) {
			next = lexer.nextToken();
		} else {
			next = lastToken;
			lastToken = Token.UNKNOWN;
		}
		return next;
	}

	/**
	 * Skips all whitespace tokens.
	 */
	protected void skipWhiteSpace() throws IOException {
		while (true) {
			Token next;
			next = nextToken();
			if (next != Token.WHITESPACE) {
				stepBack(next);
				break;
			}
		}
	}

	/**
	 * Returns current text.
	 */
	protected CharSequence text() {
		if (lastToken == Token.UNKNOWN) {
			return lexer.xxtext();
		} else {
			return lastText;
		}
	}

	/**
	 * Returns current text as a <code>String</code>
	 * without creating a <code>CharSequence</code> first.
	 */
	protected String textString() {
		if (lastToken == Token.UNKNOWN) {
			return lexer.yytext();
		} else {
			return lastText.toString();
		}
	}

	// ---------------------------------------------------------------- error

	/**
	 * Prepares error message and reports it to the visitor.
	 */
	protected void error(StringBuilder message) {
		int line = lexer.line();
		int column = lexer.column();

		if (line != -1) {
			// position is detected by jflex
			message.append(" [").append(line).append(':').append(column).append(']');
		} else {
			int position = lexer.position();

			if (calculatePosition) {
				LagartoLexer.Position currentPosition = lexer.currentPosition();
				message.append(' ').append(currentPosition.toString());
			} else {
				message.append(" [@").append(position).append(']');
			}
		}
		visitor.error(message.toString());
	}

}