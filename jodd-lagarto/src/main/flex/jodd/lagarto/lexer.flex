package jodd.lagarto;

import java.nio.CharBuffer;
%%

// lexer config
%class Lexer
%type Token
%function nextToken
%abstract
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
	void stateReset() 	{ yybegin(YYINITIAL); }
	void stateTag()		{ yybegin(TAG); }
	void stateAttr()		{ yybegin(ATTR); }
	void stateXmp() 		{ yybegin(XMP); }
	void stateScript()   { yybegin(SCRIPT); }
	void stateStyle()    { yybegin(STYLE); }
	void stateDoctype()  { yybegin(DOCTYPE); }

	// fast methods
	public final CharSequence xxtext() {
		return CharBuffer.wrap(zzBuffer, zzStartRead, zzMarkedPos - zzStartRead);
	}
	public final String yytext(int startIndex) {
		startIndex += zzStartRead;
		return new String(zzBuffer, startIndex, zzMarkedPos - startIndex);
	}
	public final String yytext(int startIndex, int endIndexOffset) {
		startIndex += zzStartRead;
		return new String(zzBuffer, startIndex, zzMarkedPos - endIndexOffset - startIndex);
	}

	int nextTagState;
	int getNextTagState() {
		return nextTagState;
	}

	boolean parseSpecialTagsAsCdata = true;
	public void setParseSpecialTagsAsCdata(boolean parseSpecialTagsAsCdata) {
		this.parseSpecialTagsAsCdata = parseSpecialTagsAsCdata;
	}
%}

// additional lexer states
%state TAG, ATTR, XMP, SCRIPT, STYLE, XML_DECLARATION, DOCTYPE

%%

<YYINITIAL> {
	(
		"<![if"
		[^\]]*
		"]>"
	)                       { return Token.CONDITIONAL_COMMENT_START; }
	(
		("<!--" [^>]*)?
		"<![endif]"
		(">" | "-->")
	)         				{ return Token.CONDITIONAL_COMMENT_END; }
	"<!--" ~"-->"			{ return Token.COMMENT; }
	"<!DOCTYPE"				{ stateDoctype(); return Token.DOCTYPE; }
	"<![CDATA[" ~"]]>"  	{ return Token.CDATA; }
	[^<]+               	{ return Token.TEXT; }
	"<?"					{ nextTagState = YYINITIAL; stateTag(); return Token.XML_LT; }
	"<"                 	{ nextTagState = YYINITIAL; stateTag(); return Token.LT; }
}

<TAG> {
	[\n\r \t\b\f]+		{ return Token.WHITESPACE; }
	"xmp"				{ if (parseSpecialTagsAsCdata) nextTagState = XMP; stateAttr(); return Token.WORD; }
	"script"			{ if (parseSpecialTagsAsCdata) nextTagState = SCRIPT; stateAttr(); return Token.WORD; }
	"style"				{ if (parseSpecialTagsAsCdata) nextTagState = STYLE; stateAttr(); return Token.WORD; }
	[^>\]/=\"\'\n\r \t\b\f\?]* { stateAttr(); return Token.WORD; }
	.					{ yypushback(1); stateAttr(); return Token.WHITESPACE;}
}

<ATTR> {
	"/"                 { return Token.SLASH; }
	[\n\r \t\b\f]+      { return Token.WHITESPACE; }
	"="                 { return Token.EQUALS; }
	"\"" ~"\""          { return Token.QUOTE; }
	"'" ~"'"            { return Token.QUOTE; }
	[^>\]/=\"\'\n\r \t\b\f][^>\]/=\n\r \t\b\f]* { return Token.WORD; }
	"?>"                { stateReset(); return Token.XML_GT; }
	">"                 { yybegin(nextTagState); return Token.GT; }
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
<DOCTYPE> {
	[\n\r \t\b\f]+ 			{ return Token.WHITESPACE; }
	"\"" ~"\""          	{ return Token.WORD; }
	[^>\]/=\"\'\n\r \t\b\f][^>\]/=\n\r \t\b\f]*		{ return Token.WORD; }
	">"                		{ stateReset(); return Token.GT; }
}

// fallback rule, when nothing else matches
.|\n                    { throw new LagartoException("Illegal character ["+ yytext() + ']', yystate(), line(), column());}

// end-of-file
<<EOF>>                 { return Token.EOF; }
