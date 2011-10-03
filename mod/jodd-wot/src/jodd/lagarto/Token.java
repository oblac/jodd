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
	DIRECTIVE,
	CDATA,
	XML_DECLARATION,
	LT,
	GT,
	CONDITIONAL_COMMENT_START,
	CONDITIONAL_COMMENT_END,
	EOF
}
