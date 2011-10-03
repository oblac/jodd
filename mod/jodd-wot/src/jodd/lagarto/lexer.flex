package jodd.lagarto;

import java.nio.CharBuffer;
%%

// lexer config
%class Lexer
%type Token
%function nextToken
%final
%unicode
%byaccj
%char
%ignorecase

// for debugging, adds overhead
//%line
//%column

// faster than %pack or %table
%switch
%buffer 4096

// additional methods
%{
	// position methods
	public int position() { return yychar; }
	public int length()   { return yylength(); }
	public int line()     { return -1; /*yyline;*/ }   	// for debugging
	public int column()   { return -1; /*yycolumn;*/ } 	// for debugging

	// state methods
	public void stateReset() 	{ yybegin(YYINITIAL); }
	public void stateTag()		{ yybegin(TAG); }
	public void stateXmp() 		{ yybegin(XMP); }
	public void stateScript()   { yybegin(SCRIPT); }

	// fast methods
	public final CharSequence xxtext() {
		return CharBuffer.wrap(zzBuffer, zzStartRead, zzMarkedPos - zzStartRead);
	}

	// empty ctor
	Lexer() {}
%}

// additional lexer states
%state TAG, XMP, SCRIPT, XML_DECLARATION

%%

<YYINITIAL> {
	"<!--" [^\[] ~"-->"		{ return Token.COMMENT; }
	"<!---->"           	{ return Token.COMMENT; }
	"<!" [^\[\-] ~">"     	{ return Token.DIRECTIVE; }
	"<![CDATA[" ~"]]>"  	{ return Token.CDATA; }
	"<!--[if" ~"]>"     	{ return Token.CONDITIONAL_COMMENT_START; }
	"<![if" ~"]>"       	{ return Token.CONDITIONAL_COMMENT_START; }
	"<![endif]>"        	{ return Token.CONDITIONAL_COMMENT_END; }
	"<![endif]-->"        	{ return Token.CONDITIONAL_COMMENT_END; }
	[^<]+               	{ return Token.TEXT; }
	"<?"					{ stateTag(); return Token.XML_DECLARATION; }
	"<"                 	{ stateTag(); return Token.LT; }
}

<TAG> {
	"/"                 { return Token.SLASH; }
	[\n\r \t\b\012]+    { return Token.WHITESPACE; }
	"="                 { return Token.EQUALS; }
	"\"" ~"\""          { return Token.QUOTE; }
	"'" ~"'"            { return Token.QUOTE; }
	[^>\]/=\"\'\n\r \t\b\012][^>\]/=\n\r \t\b\012]* { return Token.WORD; }
	">"                 { stateReset(); return Token.GT; }
	"?>"                { stateReset(); return Token.GT; }
}

<XMP> {
	~"</xmp" ~">"		{ stateReset(); return Token.TEXT; }
}

<SCRIPT> {
	~"</script" ~">"	{ stateReset(); return Token.TEXT; }
}

// fallback rule, when nothing else matches
.|\n                    { throw new LagartoException("Illegal character <"+ yytext() +">.", line(), column());}

// end-of-file
<<EOF>>                 { return Token.EOF; }
