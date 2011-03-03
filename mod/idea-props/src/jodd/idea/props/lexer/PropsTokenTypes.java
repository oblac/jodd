// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.idea.props.lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import jodd.idea.props.psi.PropsElementType;

/**
 * Lexer token types.
 */
public interface PropsTokenTypes {

	IElementType WHITE_SPACE = TokenType.WHITE_SPACE;
	IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;

	IElementType SECTION_CHARACTERS = new PropsElementType("SECTION_CHARACTERS");
	IElementType END_OF_LINE_COMMENT = new PropsElementType("END_OF_LINE_COMMENT");
	IElementType KEY_CHARACTERS = new PropsElementType("KEY_CHARACTERS");
	IElementType VALUE_CHARACTERS = new PropsElementType("VALUE_CHARACTERS");
	IElementType KEY_VALUE_SEPARATOR = new PropsElementType("KEY_VALUE_SEPARATOR");

	TokenSet COMMENTS = TokenSet.create(END_OF_LINE_COMMENT);
	TokenSet WHITESPACES = TokenSet.create(WHITE_SPACE);
}
