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

	private static final String TAG_NAME_XMP = "xmp";
	private static final String TAG_NAME_SCRIPT = "script";
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
			throw new LagartoException("Parsing error", ioex);
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
					parseComment();
					break;
				case CDATA:
					parseCDATA();
					break;
				case DIRECTIVE:
					parseDirective();
					break;
				case TEXT:
					int start = lexer.position();
					parseText(start, start + lexer.length());
					break;
				case LT:
					parseTag(token, TagType.OPEN);
					break;
				case XML_DECLARATION:
					parseTag(token, TagType.OPEN);
					break;
				case CONDITIONAL_COMMENT_START:
					parseCCStart();
					break;
				case CONDITIONAL_COMMENT_END:
					parseCCEnd();
					break;
				default:
					error("Unexpected token: " + token);
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
				throw new LagartoException("Parsing error!");
			}
			buffTextEnd = end;
		}
	}

	/**
	 * Parses HTML comments.
	 */
	protected void parseComment() throws IOException {
		flushText();
		int start = lexer.position() + 4;
		int end = start + lexer.length() - 7;
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
	 * Parses HTML directive.
	 */
	protected void parseDirective() throws IOException {
		flushText();
		int start = lexer.position() + 2;
		int end = start + lexer.length() - 3;
		visitor.directive(input.subSequence(start, end));
	}

	/**
	 * Parses conditional comment start.
	 */
	protected void parseCCStart() throws IOException {
		flushText();
		int start = lexer.position();
		int end = start + lexer.length() - 2;

		boolean isDownlevelHidden;
		if (input.charAt(start + 2) == '-') {
			start += 5;
			isDownlevelHidden = true;
		} else {
			start += 3;
			isDownlevelHidden = false;
		}

		visitor.condCommentStart(input.subSequence(start, end), isDownlevelHidden);
	}

	/**
	 * Parses conditional comment end.
	 */
	protected void parseCCEnd() throws IOException {
		flushText();
		int start = lexer.position();
		int end = start + lexer.length();

		boolean isDownlevelHidden;
		if (input.charAt(end - 2) == '-') {
			end -= 4;
			isDownlevelHidden = true;
		} else {
			end -= 2;
			isDownlevelHidden = false;
		}
		start += 3;

		visitor.condCommentEnd(input.subSequence(start, end), isDownlevelHidden);
	}

	/**
	 * Parse tag starting from "&lt;".
	 */
	protected void parseTag(Token tagToken, TagType type) throws IOException {
		int start = lexer.position();
		skipWhiteSpace();
		Token token;
		token = nextToken();

		if (token == Token.SLASH) {		// it is closing tag
			type = TagType.CLOSE;
			token = nextToken();
		}

		switch (token) {
			case WORD:					// tag name
				String tagName = text().toString();
				if (doParseTag(tagName)) {
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
				error("Invalid tag.");
				break;
		}
	}

	/**
	 * Returns <code>true</code> if tag has to be parsed.
	 */
	@SuppressWarnings({"UnusedParameters"})
	protected boolean doParseTag(String tagName) {
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
				case GT:
					break loop;
				case WORD:
					parseAttribute();
					break;
				case EOF:
					parseText(start, lexer.position());
					return;
				default:
					error("Invalid tag: " + tagName);
					break loop;
			}
		}

		token = nextToken();

		if (token == Token.SLASH) {			// an empty tag, no body
			type = TagType.EMPTY;
			token = nextToken();
		}

		switch (token) {
			default:
				error("Expected end of tag for: " + tagName);
				// continue as tag
//				onTag(type, tagName, start, lexer.position() - start + 1);
//				break;
			case GT:	// end of tag, process it
				flushText();

				int len = lexer.position() - start + 1;

				if (type.isOpeningTag()) {
					if (tagToken == Token.XML_DECLARATION) {
						tag.defineTag(type, start, len + 1);
						tag.setTagMarks("<?", "?>");
						tag.increaseDeepLevel();
						visitor.xml(tag);
						tag.decreaseDeepLevel();
						break;
					}
					if (tagName.equalsIgnoreCase(TAG_NAME_XMP)) {
						tag.defineTag(type, start, len);
						tag.increaseDeepLevel();
						parseXMP();
						tag.decreaseDeepLevel();
						break;
					}
					if (tagName.equalsIgnoreCase(TAG_NAME_SCRIPT)) {
						tag.defineTag(type, start, len);
						tag.increaseDeepLevel();
						parseScript();
						tag.decreaseDeepLevel();
						break;
					}
				}

				// default tag
				tag.defineTag(type, start, len);
				if (type.isOpeningTag()) {
					tag.increaseDeepLevel();
				}
				visitor.tag(tag);
				if (type.isClosingTag()) {
					tag.decreaseDeepLevel();
				}
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
				error("Invalid attribute value for: " + attributeName);
			}
		} else if (token == Token.SLASH || token == Token.GT || token == Token.WORD) {
			tag.addAttribute(attributeName, null);
			stepBack(token);
		} else if (token != Token.EOF) {
			error("Invalid attribute: " + attributeName);
		}
	}

	protected void parseXMP() throws IOException {
		lexer.stateXmp();
		int start = lexer.position() + 1;
		Token token = nextToken();
		if (token != Token.TEXT) {
			error("Invalid XMP tag.");
		}
		int end = start + lexer.length() - 6;
		visitor.xmp(tag, input.subSequence(start, end));
	}

	protected void parseScript() throws IOException {
		lexer.stateScript();
		int start = lexer.position() + 1;
		Token token = nextToken();
		if (token != Token.TEXT) {
			error("Invalid SCRIPT tag.");
		}
		int end = start + lexer.length() - 9;
		visitor.script(tag, input.subSequence(start, end));
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
		if (line != 1) {
			message += " Error at: " + line + ':' + column;
		}
		visitor.error(message);
	}

}
