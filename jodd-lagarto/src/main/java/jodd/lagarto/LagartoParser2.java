// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.util.ArraysUtil;
import jodd.util.CharUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;
import jodd.util.UnsafeUtil;

import java.nio.CharBuffer;

/**
 * HTML/XML content parser using {@link TagVisitor} for callbacks.
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

	/**
	 * Parses content and callback provided {@link TagVisitor}.
	 */
	public void parse(TagVisitor visitor) {
		this.visitor = visitor;

		parseStart();

		while (ndx < total) {
			parseText();
		}

		parseEnd();
	}

	// ---------------------------------------------------------------- start & end

	/**
	 * Fires up the start event with provided parsing context.
	 */
	protected void parseStart() {
		visitor.start(ctx);
	}

	/**
	 * Flushes remaining text and visits the end.
	 */
	protected void parseEnd() {
		flushText();

		visitor.end();
	}

	// ---------------------------------------------------------------- text

	/**
	 * The main loop, parses the text. Switches to different states.
	 */
	protected void parseText() {
		int textStart = ndx;

		skipUntil(TAG_START);

		visitText(textStart);

		if (enableConditionalComments) {
			if (match(COND_COMMENT_START)) {
				parseCCRevealedStart();
				return;
			}

			if (match(COND_COMMENT_ENDIF)) {
				parseCCEnd(null);
				return;
			}

			if (match(COMMENT_START)) {
				parseCCComment();
				return;
			}

		} else {
			if (match(COMMENT_START)) {
				parseComment();
				return;
			}
		}

		if (matchIgnoreCase(DOCTYPE_START)) {
			parseDoctype();
			return;
		}

		if (xmlMode) {
			if (matchIgnoreCase(CDATA_START)) {
				parseCData();
				return;
			}
			if (match(TAG_XML_START)) {
				parseTag(true);
				return;
			}
		} else {
			if (match(TAG_XML_COMM_START)) {
				parseXmlComment(false);
				return;
			}
			if (match(TAG_XML_START)) {
				parseXmlComment(true);
				return;
			}
		}

		if (match(TAG_START)) {		// WARN: must be the last condition, as it contains itself in above matching targets
			parseTag(false);
		}
	}

	protected int textStartNdx = -1;
	protected int textEndNdx = -1;

	/**
	 * Stores text buffer for visiting. Checks if it is appended to the previous buffer.
	 * If so, the buffer grows. Otherwise, previous buffer gets visited, and the new
	 * buffer gets stored.
	 */
	protected void visitText(int startNdx) {
		if (textStartNdx != -1) {
			if (startNdx == textEndNdx) {
				// continue visiting, join with previous buffer
				startNdx = textStartNdx;
			} else {
				// flush previous buffer
				flushText();
			}
		}

		textStartNdx = startNdx;
		textEndNdx = ndx;
	}

	/**
	 * Flushes {@link #visitText(int) stored text buffer}. Does nothing
	 * if buffer does not exist or it is empty. Must be called before
	 * every non-text visit method!
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

	// ---------------------------------------------------------------- tag

	/**
	 * Parses a tag.
	 */
	protected void parseTag(boolean isXmlTag) {
		int tagStart = ndx - 1;

		TagType tagType = TagType.START;

		if (match(TAG_SLASH)) {
			tagType = TagType.END;
		}

		skipAnyOf(WHITEPSPACES);

		CharSequence tagName = readUntilAnyOf(TAG_NAME_ENDS);

		if (tagName == null) {
			// ERR: nothing has been read as tag name, treat tag as text
			// todo EOF
			visitText(tagStart);
			return;
		}

		if (!isValidTagName(tagName)) {
			// ERR: not a valid tag name
			visitText(tagStart);
			return;
		}

		tag.startTag(tagName.toString());

		while (true) {
			skipAnyOf(WHITEPSPACES);

			if (isXmlTag) {
				if (match(TAG_XML_END)) {
					break;
				}
			} else {
				if (match(TAG_SLASH_END)) {
					tagType = TagType.SELF_CLOSING;
					break;    // end of tag
				}
				if (match(TAG_END)) {
					break;    // end of tag
				}
			}

			CharSequence attrName = readUntilAnyOf(ATTR_NAME_ENDS);

			if (attrName == null) {
				// ERR: no end tag, and no attribute name, continue as text
				if (isEOF()) {
					error("EOF: " + tagName);
					visitText(tagStart);
					return;
				}

				error("Invalid token: " + input[ndx]);
				ndx++;
				continue;
			}

			skipAnyOf(WHITEPSPACES);

			if (match(EQUALS)) {	// attribute value
				skipAnyOf(WHITEPSPACES);

				String attrValue = readAttributeValue();

				tag.addAttribute(attrName.toString(), attrValue);
			} else {
				tag.addAttribute(attrName.toString(), null);
			}
		}

		if (isXmlTag) {
			tagStart--;
			int len = ndx - tagStart;
			tag.defineTag(tagType, tagStart, len);

			visitXmlTag();
		} else {
			int len = ndx - tagStart;
			tag.defineTag(tagType, tagStart, len);

			if (equalsIgnoreCase(tagName, "SCRIPT")) {
				visitBlockTag(tagType, TAG_SCRIPT_END, 0);
			} else if (equalsIgnoreCase(tagName, "STYLE")) {
				visitBlockTag(tagType, TAG_STYLE_END, 1);
			} else if (equalsIgnoreCase(tagName, "XMP")) {
				visitBlockTag(tagType, TAG_XMP_END, 2);
			} else {
				visitTag(tagType);
			}
		}
	}

	/**
	 * Returns <code>true</code> if tag name is valid.
	 */
	protected boolean isValidTagName(CharSequence tagName) {
		return CharUtil.isAlpha(tagName.charAt(0));
	}

	/**
	 * Visits a tag.
	 */
	protected void visitTag(TagType tagType) {
		flushText();

		if (tagType.isStartingTag()) {
			tag.increaseDeepLevel();
		}

		visitor.tag(tag);

		if (tagType.isEndingTag()) {
			tag.decreaseDeepLevel();
		}
	}

	/**
	 * Visits a XML definition tag.
	 */
	protected void visitXmlTag() {
		flushText();

		tag.setTagMarks("<?", "?>");

		tag.increaseDeepLevel();

		visitor.xml(tag);

		tag.decreaseDeepLevel();
	}

	/**
	 * Visits special, block tags.
	 */
	protected void visitBlockTag(TagType tagType, char[] endTag, int tagId) {
		flushText();

		if (tagType.isEndingTag()) {
			//todo what to do?
			return;
		}

		// we need to grab everything until the end of tag
		CharSequence body = readUntilIgnoreCase(endTag);
		if (body == null) {
			body = EMPTY_CHAR_SEQUENCE;
		}

		tag.increaseDeepLevel();

		switch(tagId) {
			case 0:
				visitor.script(tag, body);
				break;
			case 1:
				visitor.style(tag, body);
				break;
			case 2:
				visitor.xmp(tag, body);
		}

		tag.decreaseDeepLevel();

		ndx = find(TAG_END);
		ndx++;
	}


	// ---------------------------------------------------------------- comment

	/**
	 * Parses simple comment.
	 */
	protected void parseComment() {
		flushText();

		CharSequence comment = readUntil(COMMENT_END);

		if (comment == null) {
			comment = EMPTY_CHAR_SEQUENCE;
		}

		visitor.comment(comment);

		ndx += COMMENT_END.length;
	}

	/**
	 * Parses conditional comments.
	 */
	protected void parseCCComment() {
		flushText();

		// match: <!--[if
		if (match(COND_COMMENT_IF)) {
			ndx -= 2;	// to include "if" in the expression

			CharSequence expression = readUntil(COND_COMMENT_END);
			ndx += COND_COMMENT_END.length;

			// check if this is the end of the conditional comment
			CharSequence additionalComment = null;

			{
				int commentEnd = find(COMMENT_END);
				if (commentEnd != -1) {
					int condCommentEndif = find(COND_COMMENT_ENDIF, commentEnd);

					if (condCommentEndif == -1) {
						// no, this is not the end, there is additional comment
						// additional comment includes everything including the COMMENT_END
						int to = commentEnd + COMMENT_END.length;
						additionalComment = charSequence(ndx, to);
						ndx = to;
					}
				}
			}

			visitor.condComment(expression, true, true, additionalComment);
			return;
		}

		// match: <!--xxx<![endif
		int commentEnd = find(COMMENT_END);
		int condCommentEndif = find(COND_COMMENT_ENDIF, commentEnd);

		if (condCommentEndif == -1) {
			// regular comment
			CharSequence comment = charSequence(ndx, commentEnd);
			if (comment == null) {
				comment = EMPTY_CHAR_SEQUENCE;
			}
			visitor.comment(comment);
			ndx = commentEnd + COMMENT_END.length;
		} else {
			// conditional comment end

			CharSequence additionalComment = charSequence(ndx - 4, condCommentEndif);
			ndx = condCommentEndif + COND_COMMENT_ENDIF.length;

			parseCCEnd(additionalComment);
		}
	}

	protected void parseCCRevealedStart() {
		flushText();

		ndx -= 2;	// move back over "if"

		CharSequence text = readUntil(COND_COMMENT_END);
		ndx += COND_COMMENT_END.length;

		visitor.condComment(text, true, false, null);
	}

	protected void parseCCEnd(CharSequence comment) {
		flushText();

		boolean hidden = false;

		if (match(COMMENT_END)) {
			hidden = true;
		} else {
			match(TAG_END);
		}

		visitor.condComment("endif", false, hidden, comment);
	}

	protected void parseXmlComment(boolean decrease) {
		flushText();

		if (decrease) {
			ndx--;
		}

		CharSequence comment = readUntil(TAG_END);

		ndx++;

		if (comment == null) {
			comment = EMPTY_CHAR_SEQUENCE;
		}

		visitor.comment(comment);
	}

	// ---------------------------------------------------------------- doctype

	protected void parseDoctype() {
		String name = null;
		boolean isPublic = false;
		String publicId = null;
		String uri = null;

		int i = 0;

		while(true) {
			skipAnyOf(WHITEPSPACES);

			if (match(TAG_END)) {
				break;
			}

			CharSequence attr = readAttributeValue();

			switch (i) {
				case 0:
					name = attr.toString();
					break;
				case 1:
					if (equalsIgnoreCase(attr, "PUBLIC")) {
						isPublic = true;
					}
					break;
				case 2:
					if (isPublic) {
						publicId = attr.toString();
					} else {
						uri = attr.toString();
						break;
					}
					break;
				case 3:
					uri = attr.toString();
					break;
			}

			i++;
		}
		flushText();

		visitor.doctype(name, publicId, uri);
	}

	// ---------------------------------------------------------------- CData

	protected void parseCData() {
		flushText();

		CharSequence charSequence = readUntil(CDATA_END);

		if (charSequence == null) {
			charSequence = EMPTY_CHAR_SEQUENCE;
		}

		visitor.cdata(charSequence);

		ndx += CDATA_END.length;
	}

	// ---------------------------------------------------------------- util

	/**
	 * Reads attribute value.
	 */
	protected String readAttributeValue() {
		String attrValue;

		if (match(ATTR_QUOTE_1)) {
			CharSequence value = readUntil(ATTR_QUOTE_1);
			if (value == null) {
				attrValue = StringPool.EMPTY;
			} else {
				attrValue = value.toString();
				attrValue = StringUtil.replace(attrValue, StringPool.QUOTE, StringPool.HTML_QUOTE);
			}
			ndx++;
		} else if (match(ATTR_QUOTE_2)) {
			CharSequence value = readUntil(ATTR_QUOTE_2);
			if (value == null) {
				attrValue = StringPool.EMPTY;
			} else {
				attrValue = value.toString();
			}
			ndx++;
		} else {
			CharSequence value = readUntilAnyOf(ATTR_NON_QUOTED_VALUE_ENDS);
			if (value == null) {
				return null;
			}
			attrValue = value.toString();
		}

		return attrValue;
	}

	// ---------------------------------------------------------------- error

	/**
	 * Prepares error message and reports it to the visitor.
	 */
	protected void error(String message) {
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

	// ---------------------------------------------------------------- const data

	private static final char[] WHITEPSPACES = "\n\r \t\b\f".toCharArray();
	private static final char[] QUOTES = "\"'".toCharArray();

	private static final char[] TAG_NAME_ENDS = ArraysUtil.join("/>=".toCharArray(), QUOTES, WHITEPSPACES);
	private static final char[] ATTR_NAME_ENDS = ArraysUtil.join("/>=".toCharArray(), QUOTES, WHITEPSPACES);
	private static final char[] ATTR_NON_QUOTED_VALUE_ENDS = ArraysUtil.join(">".toCharArray(), WHITEPSPACES);

	private static final char TAG_START = '<';
	private static final char TAG_SLASH = '/';
	private static final char TAG_END = '>';
	private static final char[] TAG_SLASH_END = "/>".toCharArray();
	private static final char[] TAG_XML_START = "<?".toCharArray();
	private static final char[] TAG_XML_END = "?>".toCharArray();
	private static final char[] TAG_XML_COMM_START = "<!".toCharArray();

	private static final char EQUALS = '=';
	private static final char ATTR_QUOTE_1 = '\'';
	private static final char ATTR_QUOTE_2 = '"';

	private static final char[] COMMENT_START = "<!--".toCharArray();
	private static final char[] COMMENT_END = "-->".toCharArray();

	private static final char[] DOCTYPE_START = "<!DOCTYPE".toCharArray();
	private static final char[] CDATA_START = "<![CDATA[".toCharArray();
	private static final char[] CDATA_END = "]]>".toCharArray();

	private static final char[] COND_COMMENT_IF = "[if".toCharArray();
	private static final char[] COND_COMMENT_ENDIF = "<![endif]".toCharArray();

	private static final char[] COND_COMMENT_START = "<![if".toCharArray();
	private static final char[] COND_COMMENT_END = "]>".toCharArray();

	private static final char[] TAG_SCRIPT_END = "</SCRIPT".toCharArray();
	private static final char[] TAG_STYLE_END = "</STYLE".toCharArray();
	private static final char[] TAG_XMP_END = "</XMP".toCharArray();

	private static final CharSequence EMPTY_CHAR_SEQUENCE = CharBuffer.wrap(new char[0]);
}