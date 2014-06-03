// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

/**
 * Handler that receives callbacks as content is parsed.
 */
public interface TagVisitor {

	// ---------------------------------------------------------------- state

	/**
	 * Invoked on very beginning of the visiting. Provides
	 * {@link LagartoParserContext parser context} that
	 * gives some information during the parsing process.
	 */
	void start(LagartoParserContext parserContext);

	/**
	 * Invoked at the end, after all content is visited.
	 */
	void end();


	// ---------------------------------------------------------------- parsing

	/**
	 * Invoked on {@link Tag tag} (open, close or empty).
	 * <p>
	 * Warning: the passed tag instance <b>should not</b> be kept beyond
	 * this method as the parser reuse it!</p>
	 */
	void tag(Tag tag);

	/**
	 * Invoked on <b>script</b> tag.
	 */
	void script(Tag tag, CharSequence body);

	/**
	 * Invoked on comment.
	 */
	void comment(CharSequence comment);

	/**
	 * Invoked on text i.e. anything other than a tag.
	 */
	void text(CharSequence text);

	// ---------------------------------------------------------------- special

	/**
	 * Invoked on CDATA sequence.
	 */
	void cdata(CharSequence cdata);

	/**
	 * Invoked on <b>xml</b> declaration.
	 */
	void xml(Tag tag);

	/**
	 * Invoked on DOCTYPE directive.
	 */
	void doctype(Doctype doctype);

	/**
	 * Invoked on IE conditional comment. The <code>expression</code> if unmodified expression.
	 * <code>comment</code> is optional additional comment and may be <code>null</code>.
	 */
	void condComment(CharSequence expression, boolean isStartingTag, boolean isHidden, CharSequence comment);

	// ---------------------------------------------------------------- errors

	/**
	 * Warn about parsing error. Usually, parser will try to continue.
	 * @param message parsing error message
	 */
	void error(String message);

}