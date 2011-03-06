// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Lexer token types.
 */
public interface PropsTokenTypes {

	IElementType WHITE_SPACE = TokenType.WHITE_SPACE;
	IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;

	IElementType TOKEN_SECTION = new PropsToken("SECTION");
	IElementType TOKEN_EOL_COMMENT = new PropsToken("EOL_COMMENT");
	IElementType TOKEN_KEY = new PropsToken("KEY");
	IElementType TOKEN_VALUE = new PropsToken("VALUE");
	IElementType TOKEN_KEY_VALUE_SEPARATOR = new PropsToken("KEY_VALUE_SEPARATOR");
	IElementType TOKEN_MACRO = new PropsToken("MACRO");
	IElementType TOKEN_PROFILE = new PropsToken("PROFILE");

	TokenSet COMMENTS = TokenSet.create(TOKEN_EOL_COMMENT);
	TokenSet WHITESPACES = TokenSet.create(WHITE_SPACE);
}
