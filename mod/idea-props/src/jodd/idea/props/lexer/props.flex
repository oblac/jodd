package jodd.idea.props.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static org.jodd.idea.props.lexer.PropsTokenTypes.*;

%%

%class _PropsLexer
%implements FlexLexer
%unicode

%function advance
%type IElementType

// custom user code
%eof{  return;
%eof}

// macros
CRLF= \n | \r | \r\n
WHITE_SPACE_CHAR=[\ \n\r\t\f]
VALUE_CHARACTER=[^\n\r\f\\] | "\\"{CRLF} | "\\".
END_OF_LINE_COMMENT=("#"|";")[^\r\n]*
SECTION_START="["
SECTION_CHARACTER=[^:=\ \n\r\t\f\\\[\]]
SECTION_END=("]")[^\r\n]*
KEY_SEPARATOR=[\ \t]*[:=][\ \t]* | [\ \t]+
KEY_CHARACTER=[^:=\ \n\r\t\f\\] | "\\"{CRLF} | "\\".

%state IN_VALUE
%state IN_KEY_VALUE_SEPARATOR

%%

// definition

<YYINITIAL> {END_OF_LINE_COMMENT}        { yybegin(YYINITIAL); return END_OF_LINE_COMMENT; }

<YYINITIAL> {SECTION_START}{SECTION_CHARACTER}*{SECTION_END}	{yybegin(YYINITIAL); return SECTION_CHARACTERS; }

<YYINITIAL> {KEY_CHARACTER}+             { yybegin(IN_KEY_VALUE_SEPARATOR); return KEY_CHARACTERS; }
<IN_KEY_VALUE_SEPARATOR> {KEY_SEPARATOR} { yybegin(IN_VALUE); return KEY_VALUE_SEPARATOR; }
<IN_VALUE> {VALUE_CHARACTER}+            { yybegin(YYINITIAL); return VALUE_CHARACTERS; }

<IN_KEY_VALUE_SEPARATOR> {CRLF}{WHITE_SPACE_CHAR}*  { yybegin(YYINITIAL); return WHITE_SPACE; }
<IN_VALUE> {CRLF}{WHITE_SPACE_CHAR}*     { yybegin(YYINITIAL); return WHITE_SPACE; }
{WHITE_SPACE_CHAR}+                      { return WHITE_SPACE; }
.                                        { return BAD_CHARACTER; }