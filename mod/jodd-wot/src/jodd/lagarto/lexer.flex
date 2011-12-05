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
	public void stateAttr()		{ yybegin(ATTR); }
	public void stateXmp() 		{ yybegin(XMP); }
	public void stateScript()   { yybegin(SCRIPT); }
	public void stateStyle()    { yybegin(STYLE); }

	// fast methods
	public final CharSequence xxtext() {
		return CharBuffer.wrap(zzBuffer, zzStartRead, zzMarkedPos - zzStartRead);
	}

	// empty ctor
	Lexer() {}

	int nextTagState;
	public int getNextTagState() {
		return nextTagState;
	}

	boolean parseSpecialHtmlTags = true;
	public void setParseSpecialHtmlTags(boolean parseSpecialHtmlTags) {
		this.parseSpecialHtmlTags = parseSpecialHtmlTags;
	}
%}

// additional lexer states
%state TAG, ATTR, XMP, SCRIPT, STYLE, XML_DECLARATION

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
	"<?"					{ nextTagState = -2; stateTag(); return Token.XML_DECLARATION; /* don't parse special names*/ }
	"<"                 	{ nextTagState = parseSpecialHtmlTags ? -1 : -2; stateTag(); return Token.LT; /* parse special names */}
}

<TAG> {
	[\n\r \t\b\f]+		{ return Token.WHITESPACE; }
	"xmp"				{ if (nextTagState == -1) nextTagState = XMP; return Token.WORD; }
	"script"			{ if (nextTagState == -1) nextTagState = SCRIPT; return Token.WORD; }
	"style"				{ if (nextTagState == -1) nextTagState = STYLE; return Token.WORD; }
	[^>\]/=\"\'\n\r \t\b\f\?]* { return Token.WORD; }
	.					{ yypushback(1); stateAttr(); return Token.WHITESPACE;}
}

<ATTR> {
	"/"                 { return Token.SLASH; }
	[\n\r \t\b\012]+    { return Token.WHITESPACE; }
	"="                 { return Token.EQUALS; }
	"\"" ~"\""          { return Token.QUOTE; }
	"'" ~"'"            { return Token.QUOTE; }
	[^>\]/=\"\'\n\r \t\b\f][^>\]/=\n\r \t\b\f]* { return Token.WORD; }
	">"                 { if (nextTagState < 0) nextTagState = YYINITIAL; yybegin(nextTagState); return Token.GT; }
	"?>"                { stateReset(); return Token.GT; }
}

<XMP> {
	~"</xmp" ~">"		{ stateReset(); return Token.TEXT; }
}
<SCRIPT> {
	~"</script" ~">"	{ stateReset(); return Token.TEXT; }
}
<STYLE> {
	~"</style" ~">"		{ stateReset(); return Token.TEXT; }
}

// fallback rule, when nothing else matches
.|\n                    { throw new LagartoException("Illegal character ["+ yytext() + ']', yystate(), line(), column());}

// end-of-file
<<EOF>>                 { return Token.EOF; }
