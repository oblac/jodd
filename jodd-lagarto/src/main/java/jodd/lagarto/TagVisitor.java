// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

/**
 * Handler that receives callbacks as content is parsed.
 */
public interface TagVisitor {

	// ---------------------------------------------------------------- state

	/**
	 * Invoked on very beginning of the visiting.
	 */
	void start();

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
	void xml(CharSequence version, CharSequence encoding, CharSequence standalone);

	/**
	 * Invoked on DOCTYPE directive.
	 */
	void doctype(Doctype doctype);

	/**
	 * Invoked on IE conditional comment. By default, the parser does <b>not</b>
	 * process the conditional comments, so you need to turn them on. Once conditional
	 * comments are enabled, this even will be fired.
	 * <p>
	 * The following conditional comments are recognized:
	 * {@code
	 * <!--[if IE 6]>one<![endif]-->
	 * <!--[if IE 6]><!-->two<!---<![endif]-->
	 * <!--[if IE 6]>three<!--xx<![endif]-->
	 * <![if IE 6]>four<![endif]>
	 * }
	 */
	void condComment(CharSequence expression, boolean isStartingTag, boolean isHidden, boolean isHiddenEndTag);

	// ---------------------------------------------------------------- errors

	/**
	 * Warn about parsing error. Usually, parser will try to continue.
	 * @param message parsing error message
	 */
	void error(String message);

}