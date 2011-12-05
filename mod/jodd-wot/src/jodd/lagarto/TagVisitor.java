package jodd.lagarto;

/**
 * Handler that receives callbacks as content is parsed.
 */
public interface TagVisitor {

	// ---------------------------------------------------------------- start

	/**
	 * Invoked on very beginning of the visiting.
	 */
	void start();

	/**
	 * Invoked at the end, after all content is visited.
	 */
	void end();


	// ---------------------------------------------------------------- common

	/**
	 * Invoked on {@link Tag tag} (open, close or empty).
	 * <p>
	 * Warning: the passed tag instance <b>should not</b> be kept beyond
	 * this method as the parser reuse it!</p>
	 */
	void tag(Tag tag);

	/**
	 * Invoked on <b>xmp</b> tag.
	 */
	void xmp(Tag tag, CharSequence body);

	/**
	 * Invoked on <b>script</b> tag.
	 */
	void script(Tag tag, CharSequence body);

	/**
	 * Invoked on <b>style</b> tag.
	 */
	void style(Tag tag, CharSequence body);

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
	 * Invoked on DOCTYPE directive. If publicId is <code>null</code>, it is a SYSTEM
	 * directive, otherwise it is PUBLIC.
	 */
	void doctype(String name, String publicId, String baseUri);

	/**
	 * Invoked on IE conditional comments.
	 */
	void condComment(CharSequence conditionalComment, boolean isStartingTag, boolean isDownlevelHidden);

	// ---------------------------------------------------------------- errors

	/**
	 * Warn about parsing error. Usually, parser will try to continue.
	 * @param message parsing error message
	 */
	void error(String message);

}
