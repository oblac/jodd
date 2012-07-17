// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.io.CharBufferReader;
import jodd.log.Log;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.io.IOException;
import java.nio.CharBuffer;

/**
 * Parses HTML text and invokes {@link TagVisitor}.
 */
public class LagartoParser {

	private static final Log log = Log.getLogger(LagartoParser.class);

	private static final String HTML_QUOTE = "&quot;";

	private final CharSequence input;
	private final Lexer lexer;
	private final ParsedTag tag;
	private TagVisitor visitor;

	private Token lastToken = Token.UNKNOWN;
	private CharSequence lastText;

	private boolean buffering;
	private int buffTextStart;
	private int buffTextEnd;

	public LagartoParser(char[] charArray) {
		this(CharBuffer.wrap(charArray));
	}

	public LagartoParser(CharSequence charSequence) {
		this(CharBuffer.wrap(charSequence));
	}

	public LagartoParser(CharBuffer input) {
		this.input = input;
		this.lexer = new Lexer(new CharBufferReader(input));
		this.tag = new ParsedTag(input);
	}

	// ---------------------------------------------------------------- properties

	protected boolean enableConditionalComments = true;

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

	public void setParseSpecialTagsAsCdata(boolean parseSpecialTagsAsCdata) {
		lexer.parseSpecialTagsAsCdata = parseSpecialTagsAsCdata;
	}

	public boolean isParseSpecialTagsAsCdata() {
		return lexer.parseSpecialTagsAsCdata;
	}


	// ---------------------------------------------------------------- parse

	/**
	 * Parses provided content.
	 */
	public void parse(TagVisitor visitor) {
		this.visitor = visitor;

		long time = 0;
		if (log.isDebugEnabled()) {
			log.debug("parsing started");
			time = System.currentTimeMillis();
		}
		try {
			_parse();
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
		if (log.isDebugEnabled()) {
			if (time != 0) {
				time = System.currentTimeMillis() - time;
			}
			log.debug("parsing done in " + time + "ms.");
		}
	}

	// ---------------------------------------------------------------- main loop

	/**
	 * Main parsing loop that process lexer tokens.
	 */
	protected void _parse() throws IOException{
		visitor.start();
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
					error("Unexpected root token: " + token);
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
			visitor.text(input.subSequence(buffTextStart, buffTextEnd));
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

		int start = lexer.position() + 4;		// skip "<!--"
		int end = start + lexer.length() - 7;	// skip "-->"

		if (
				(enableConditionalComments) &&
				(LagartoParserUtil.regionStartWith(input, start, end, "[if"))
		){
			// conditional comment start

			int expressionEnd = LagartoParserUtil.regionIndexOf(input, start + 3, end, ']');

			int ccend = expressionEnd + 2;

			CharSequence additionalComment = null;

			// cc start tag ends either with "]>" or at very next "-->"

			int commentEnd = LagartoParserUtil.regionIndexOf(input, ccend, end, "<![endif]");

			if (commentEnd == -1) {
				additionalComment = input.subSequence(ccend, end + 3);
			}

			visitor.condComment(input.subSequence(start + 1, expressionEnd), true, true, additionalComment);

			// calculate push back to the end of the starting tag

			if (additionalComment == null) {
				int pushBack = lexer.position() + lexer.length();

				pushBack -= ccend;

				lexer.yypushback(pushBack);
			}

			return;
		}

		visitor.comment(input.subSequence(start, end));
	}

	/**
	 * Parses CDATA.
	 */
	protected void parseCDATA() throws IOException {
		flushText();
		int start = lexer.position() + 9;
		int end = start + lexer.length() - 12;
		visitor.cdata(input.subSequence(start, end));
	}

	/**
	 * Parses HTML DOCTYPE directive.
	 */
	protected void parseDoctype() throws IOException {
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
					name = text().toString();
					break;
				case 1:
					if (text().toString().equals("PUBLIC")) {
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
		visitor.doctype(name, publicId, uri);
	}

	/**
	 * Parses revealed conditional comment start.
	 * Downlevel-hidden conditional comment is detected in
	 * {@link #parseCommentOrConditionalComment()}.
	 */
	protected void parseRevealedCCStart() throws IOException {
		flushText();

		if (enableConditionalComments == false) {
			error("Conditional comments disabled");
			return;
		}

		int start = lexer.position();
		int end = start + lexer.length();
		int textStart = start;
		int textEnd = end;

		int i = start + 2;
		while (i < end) {
			if (input.charAt(i) == '[') {
				i++;
				textStart = i;
				continue;
			}
			if (input.charAt(i) == ']') {
				textEnd = i;
				break;
			}
			i++;
		}

		visitor.condComment(input.subSequence(textStart, textEnd), true, false, null);
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
			if (input.charAt(i) == '[') {
				i++;
				textStart = i;
				continue;
			}
			if (input.charAt(i) == ']') {
				textEnd = i;
				break;
			}
			i++;
		}

		boolean isDownlevelHidden = (end - textEnd) == 4;
		boolean hasExtra = (textStart - start) > 3;

		if (enableConditionalComments == false) {
			if (isDownlevelHidden) {
				visitor.comment(input.subSequence(start, end));
			} else {
				error("Conditional comments disabled");
			}
			return;
		}

		CharSequence additionalComment = null;
		if (hasExtra) {
			additionalComment = input.subSequence(start, textStart - 3);
		}

		visitor.condComment(input.subSequence(textStart, textEnd), false, isDownlevelHidden, additionalComment);
	}

	/**
	 * Parse tag starting from "&lt;".
	 */
	protected void parseTag(Token tagToken, TagType type) throws IOException {
		int start = lexer.position();
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
				String tagName = text().toString();
				if (acceptTag(tagName)) {
					parseTagAndAttributes(tagToken, tagName, type, start);
				} else {
					// step back and parse tag as text
					lexer.stateReset();
					stepBack(lexer.nextToken());
					parseText(start, lexer.position());
				}
				break;
			case GT:	// illegal tag (<>), consume it as text
				parseText(start, lexer.position() + 1);
				break;
			case EOF:	// eof, consume it as text
				parseText(start, lexer.position());
				break;
			default:
				error("Invalid token in tag <" + text() + '>');
				// step back and parse tag as text
				lexer.stateReset();
				stepBack(lexer.nextToken());
				parseText(start, lexer.position());
				break;
		}
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
				case SLASH:
					type = TagType.SELF_CLOSING;	// an empty tag, no body
					nextToken();
					break loop;
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
				default:
					// unexpected token, try to skip it!
					String tokenText = text().toString();
					if (tokenText == null) {
						tokenText = lexer.yytext();
					}
					error("Tag <" + tagName + "> invalid token: " + tokenText);
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
			error("Unmatched tag <" + tagName + "?>");
		} else if (tagToken == Token.XML_LT && token == Token.GT) {
			token = Token.XML_GT;
			error("Unmatched tag <?" + tagName + '>');
		}

		switch (token) {
			default:
				error("Expected end of tag for <" + tagName + '>');
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
		String attributeName = text().toString();
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
					attributeValue = StringUtil.replace(attributeValue, StringPool.QUOTE, HTML_QUOTE);
				}
				tag.addAttribute(attributeName, attributeValue);
			} else if (token == Token.WORD) {
				String attributeValue = text().toString();
				while (true) {
					Token next = nextToken();
					if (next == Token.WORD || next == Token.EQUALS || next == Token.SLASH) {
						attributeValue += text();	// rare!
					} else {
						stepBack(next);
						break;
					}
				}
				tag.addAttribute(attributeName, attributeValue);
			} else if (token == Token.SLASH || token == Token.GT) {
				stepBack(token);
			} else if (token != Token.EOF) {
				error("Invalid attribute: " + attributeName);
			}
		} else if (token == Token.SLASH || token == Token.GT || token == Token.WORD) {
			tag.addAttribute(attributeName, null);
			stepBack(token);
		} else if (token == Token.QUOTE) {
			error("Orphan attribute: " + text());
			tag.addAttribute(attributeName, null);
		} else if (token != Token.EOF) {
			error("Invalid attribute: " + attributeName);
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
				visitor.xmp(tag, input.subSequence(start, end - 6));
				break;
			case Lexer.SCRIPT:
				visitor.script(tag, input.subSequence(start, end - 9));
				break;
			case Lexer.STYLE:
				visitor.style(tag, input.subSequence(start, end - 8));
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

	// ---------------------------------------------------------------- error

	/**
	 * Prepares error message and reports it to the visitor.
	 */
	protected void error(String message) {
		int line = lexer.line();
		int column = lexer.column();
		if (line != -1) {
			message += " Error at: " + line + ':' + column;
		}
		visitor.error(message);
	}

}
