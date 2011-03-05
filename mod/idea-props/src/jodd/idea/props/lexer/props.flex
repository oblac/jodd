package jodd.idea.props.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static jodd.idea.props.lexer.PropsTokenTypes.*;

%%

%class _PropsLexer
%implements FlexLexer
%unicode

%function advance
%type IElementType

// custom user code, executed only once
// when the end of file is reached
%eof{
	return;
%eof}

// macros
CRLF = \n | \r | \r\n
LINE = [^\n\r]
SPACE = [\ \t]
WHITE_SPACE = {CRLF} | {SPACE} | \f
WORD=[^\n\r\ \t\f]*

// rules
END_OF_LINE_COMMENT = ("#" | ";") {LINE}*

SECTION = "[" {WORD} "]"

KEY = [^:=\n\r\ \t\f\\] | "\\"{CRLF} | "\\".
KEY_SEPARATOR = {SPACE}* [:=] {SPACE}* | {SPACE}+
VALUE = [^\n\r\f\\"${"] | "\\"{CRLF} | "\\${" | "\\".

MACRO="${" {WORD} "}"

// states
%state IN_VALUE
%state IN_KEY_VALUE_SEPARATOR

%%

// end of line
<YYINITIAL> {END_OF_LINE_COMMENT}			{ yybegin(YYINITIAL); return TOKEN_EOL_COMMENT; }

// section
<YYINITIAL> {SECTION} {WHITE_SPACE}*		{yybegin(YYINITIAL); return TOKEN_SECTION; }

// property
<YYINITIAL> {KEY}+							{ yybegin(IN_KEY_VALUE_SEPARATOR); return TOKEN_KEY; }
<IN_KEY_VALUE_SEPARATOR> {KEY_SEPARATOR}	{ yybegin(IN_VALUE); return TOKEN_KEY_VALUE_SEPARATOR; }
<IN_VALUE> {
	{VALUE}*								{ return TOKEN_VALUE; }
	{MACRO}*								{ return TOKEN_MACRO; }
	{CRLF}									{ yybegin(YYINITIAL); return WHITE_SPACE; }
}

// special cases
<IN_KEY_VALUE_SEPARATOR> {CRLF}{WHITE_SPACE}* 	{ yybegin(YYINITIAL); return WHITE_SPACE; }
<IN_VALUE> {CRLF}{WHITE_SPACE}*					{ yybegin(YYINITIAL); return WHITE_SPACE; }

// general
{WHITE_SPACE}+								{ return WHITE_SPACE; }
.											{ return BAD_CHARACTER; }