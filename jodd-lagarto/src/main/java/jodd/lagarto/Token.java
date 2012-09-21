// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

/**
 * Lexer tokens.
 */
public enum Token {
	UNKNOWN,
	SLASH,
	WHITESPACE,
	EQUALS,
	QUOTE,
	WORD,
	TEXT,
	COMMENT,
	DOCTYPE,
	CDATA,
	XML_LT,
	XML_GT,
	LT,
	GT,
	CONDITIONAL_COMMENT_START,
	CONDITIONAL_COMMENT_END,
	EOF
}
