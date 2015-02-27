// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

/**
 * Tag adapter.
 */
public class TagAdapter implements TagVisitor {

	protected final TagVisitor target;

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

	public void xml(CharSequence version, CharSequence encoding, CharSequence standalone) {
		target.xml(version, encoding, standalone);
	}

	public void doctype(Doctype doctype) {
		target.doctype(doctype);
	}

	public void condComment(CharSequence expression, boolean isStartingTag, boolean isHidden, boolean isHiddenEndTag) {
		target.condComment(expression, isStartingTag, isHidden, isHiddenEndTag);
	}

	public void error(String message) {
		target.error(message);
	}
}
