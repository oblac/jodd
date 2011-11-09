// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

/**
 * Tag adapter.
 */
public class TagAdapter implements TagVisitor {

	protected TagVisitor target;

	public TagAdapter(TagVisitor target) {
		this.target = target;
	}

	/**
	 * Returns target tag visitor. It may be another
	 * nested <code>TagAdapter</code> or <code>TagWriter</code>.
	 */
	public TagVisitor getTarget() {
		return target;
	}

	public void start() {
		target.start();
	}

	public void end() {
		target.end();
	}

	public void tag(Tag tag) {
		target.tag(tag);
	}

	public void xmp(Tag tag, CharSequence body) {
		target.xmp(tag, body);
	}

	public void script(Tag tag, CharSequence body) {
		target.script(tag, body);
	}

	public void comment(CharSequence comment) {
		target.comment(comment);
	}

	public void text(CharSequence text) {
		target.text(text);
	}

	public void cdata(CharSequence cdata) {
		target.cdata(cdata);
	}

	public void xml(Tag tag) {
		target.xml(tag);
	}

	public void directive(CharSequence directive) {
		target.directive(directive);
	}

	public void condCommentStart(CharSequence conditionalComment, boolean isDownlevelHidden) {
		target.condCommentStart(conditionalComment, isDownlevelHidden);
	}

	public void condCommentEnd(CharSequence conditionalComment, boolean isDownlevelHidden) {
		target.condCommentEnd(conditionalComment, isDownlevelHidden);
	}

	public void error(String message) {
		target.error(message);
	}
}
