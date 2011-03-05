// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.highlighter;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.StringEscapesTokenTypes;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import jodd.idea.props.lexer.PropsLexer;
import jodd.idea.props.lexer.PropsTokenTypes;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

/**
 * Syntax highlighter defines token colors.
 */
public class PropsSyntaxHighlighter extends SyntaxHighlighterBase {

	private static final Map<IElementType, TextAttributesKey> keys1;
	private static final Map<IElementType, TextAttributesKey> keys2;

	@NotNull
	public Lexer getHighlightingLexer() {
		return new PropsLexer();
	}

	public static final TextAttributesKey PROP_KEY = TextAttributesKey.createTextAttributesKey(
			"PROPS.KEY",
			SyntaxHighlighterColors.KEYWORD.getDefaultAttributes()
	);
	public static final TextAttributesKey PROP_VALUE = TextAttributesKey.createTextAttributesKey(
			"PROPS.VALUE",
			SyntaxHighlighterColors.STRING.getDefaultAttributes()
	);
	public static final TextAttributesKey PROP_COMMENT = TextAttributesKey.createTextAttributesKey(
			"PROPS.LINE_COMMENT",
			SyntaxHighlighterColors.LINE_COMMENT.getDefaultAttributes()
	);
	public static final TextAttributesKey PROP_KEY_VALUE_SEPARATOR = TextAttributesKey.createTextAttributesKey(
			"PROPS.KEY_VALUE_SEPARATOR",
			SyntaxHighlighterColors.OPERATION_SIGN.getDefaultAttributes()
	);
	public static final TextAttributesKey PROP_VALID_STRING_ESCAPE = TextAttributesKey.createTextAttributesKey(
			"PROPS.VALID_STRING_ESCAPE",
			SyntaxHighlighterColors.VALID_STRING_ESCAPE.getDefaultAttributes()
	);
	public static final TextAttributesKey PROP_INVALID_STRING_ESCAPE = TextAttributesKey.createTextAttributesKey(
			"PROPS.INVALID_STRING_ESCAPE",
			SyntaxHighlighterColors.INVALID_STRING_ESCAPE.getDefaultAttributes()
	);
	public static final TextAttributesKey PROP_SECTION = TextAttributesKey.createTextAttributesKey(
			"PROPS.SECTION",
			new TextAttributes(new Color(0x660066), null, null, null, Font.BOLD)
	);
	public static final TextAttributesKey PROP_MACRO = TextAttributesKey.createTextAttributesKey(
			"PROPS.MACRO",
			new TextAttributes(new Color(0x003366), null, null, null, Font.BOLD)
	);

	static {
		keys1 = new HashMap<IElementType, TextAttributesKey>();
		keys2 = new HashMap<IElementType, TextAttributesKey>();

		keys1.put(PropsTokenTypes.TOKEN_VALUE, PROP_VALUE);
		keys1.put(PropsTokenTypes.TOKEN_EOL_COMMENT, PROP_COMMENT);
		keys1.put(PropsTokenTypes.TOKEN_KEY, PROP_KEY);
		keys1.put(PropsTokenTypes.TOKEN_KEY_VALUE_SEPARATOR, PROP_KEY_VALUE_SEPARATOR);
		keys1.put(PropsTokenTypes.TOKEN_SECTION, PROP_SECTION);
		keys1.put(PropsTokenTypes.TOKEN_MACRO, PROP_MACRO);

		keys1.put(StringEscapesTokenTypes.VALID_STRING_ESCAPE_TOKEN, PROP_VALID_STRING_ESCAPE);

		// in fact all back-slashed escapes are allowed
		keys1.put(StringEscapesTokenTypes.INVALID_CHARACTER_ESCAPE_TOKEN, PROP_INVALID_STRING_ESCAPE);
		keys1.put(StringEscapesTokenTypes.INVALID_UNICODE_ESCAPE_TOKEN, PROP_INVALID_STRING_ESCAPE);
	}

	@NotNull
	public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
		return pack(keys1.get(tokenType), keys2.get(tokenType));
	}

	public static final Map<TextAttributesKey, Pair<String, HighlightSeverity>> DISPLAY_NAMES = new HashMap<TextAttributesKey, Pair<String, HighlightSeverity>>(7);

	static {
		DISPLAY_NAMES.put(PROP_KEY, new Pair<String, HighlightSeverity>("Property key", null));
		DISPLAY_NAMES.put(PROP_VALUE, new Pair<String, HighlightSeverity>("Property value", null));
		DISPLAY_NAMES.put(PROP_KEY_VALUE_SEPARATOR, new Pair<String, HighlightSeverity>("Key value separator", null));
		DISPLAY_NAMES.put(PROP_COMMENT, new Pair<String, HighlightSeverity>("Comment", null));
		DISPLAY_NAMES.put(PROP_VALID_STRING_ESCAPE, new Pair<String, HighlightSeverity>("String escape", null));
		DISPLAY_NAMES.put(PROP_INVALID_STRING_ESCAPE, new Pair<String, HighlightSeverity>("String escape (invalid)", HighlightSeverity.WARNING));
		DISPLAY_NAMES.put(PROP_SECTION, new Pair<String, HighlightSeverity>("Section", null));
		DISPLAY_NAMES.put(PROP_MACRO, new Pair<String, HighlightSeverity>("Macro", null));
	}
}

