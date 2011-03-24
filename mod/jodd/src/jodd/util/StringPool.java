// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Pool of <code>String</code> constants to prevent repeating of
 * hard-coded <code>String</code> literals in the code.
 * Due to fact that these are <code>public static final</code>
 * they will be inlined by java compiler and
 * reference to this class will be dropped.
 * There is <b>no</b> performance gain of using this pool.
 * Read: http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.5
 * <ul>
 *     <li>Literal strings within the same class in the same package represent references to the same <code>String</code> object.
 *     <li>Literal strings within different classes in the same package represent references to the same <code>String</code> object.
 *     <li>Literal strings within different classes in different packages likewise represent references to the same <code>String</code> object.
 *     <li>Strings computed by constant expressions are computed at compile time and then treated as if they were literals.
 *     <li>Strings computed by concatenation at run time are newly created and therefore distinct.
 * </ul>
 */
public interface StringPool {

	String AMPERSAND        = "&";
	String AND              = "and";
	String AT               = "@";
	String ASTERISK         = "*";
	String STAR             = ASTERISK;
	String BACK_SLASH       = "\\";
	String COLON            = ":";
	String COMMA            = ",";
	String DASH             = "-";
	String DOLLAR           = "$";
	String DOT              = ".";
	String DOTDOT           = "..";
	String DOT_CLASS        = ".class";
	String DOT_JAVA         = ".java";
	String EMPTY            = "";
	String EQUALS           = "=";
	String FALSE            = "false";
	String SLASH            = "/";
	String HASH             = "#";
	String HAT              = "^";
	String LEFT_BRACE       = "{";
	String LEFT_BRACKET     = "(";
	String LEFT_CHEV        = "<";
	String NEWLINE          = "\n";
	String N                = "n";
	String NO               = "no";
	String NULL             = "null";
	String OFF              = "off";
	String ON               = "on";
	String PERCENT          = "%";
	String PIPE             = "|";
	String PLUS             = "+";
	String QUESTION_MARK    = "?";
	String EXCLAMATION_MARK = "!";
	String QUOTE            = "\"";
	String RETURN           = "\r";
	String TAB              = "\t";
	String RIGHT_BRACE      = "}";
	String RIGHT_BRACKET    = ")";
	String RIGHT_CHEV       = ">";
	String SEMICOLON        = ";";
	String SINGLE_QUOTE     = "'";
	String SPACE            = " ";
	String LEFT_SQ_BRACKET  = "[";
	String RIGHT_SQ_BRACKET = "]";
	String TRUE             = "true";
	String UNDERSCORE       = "_";
	String UTF_8            = "UTF-8";
	String ISO_8859_1       = "ISO-8859-1";
	String Y                = "y";
	String YES              = "yes";
	String ONE 				= "1";
	String ZERO				= "0";
	String DOLLAR_LEFT_BRACE= "${";
}