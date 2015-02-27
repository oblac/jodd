// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

/**
 * Visitor over several target visitors at once.
 */
public class TagVisitorChain implements TagVisitor {

	protected final TagVisitor[] targets;

	public TagVisitorChain(TagVisitor... targets) {
		this.targets = targets;
	}

	public void start() {
		for (TagVisitor target : targets) {
			target.start();
		}
	}

	public void end() {
		for (TagVisitor target : targets) {
			target.end();
		}
	}

	public void tag(Tag tag) {
		for (TagVisitor target : targets) {
			target.tag(tag);
		}
	}

	public void script(Tag tag, CharSequence body) {
		for (TagVisitor target : targets) {
			target.script(tag, body);
		}
	}

	public void comment(CharSequence comment) {
		for (TagVisitor target : targets) {
			target.comment(comment);
		}
	}

	public void text(CharSequence text) {
		for (TagVisitor target : targets) {
			target.text(text);
		}
	}

	public void cdata(CharSequence cdata) {
		for (TagVisitor target : targets) {
			target.cdata(cdata);
		}
	}

	public void xml(CharSequence version, CharSequence encoding, CharSequence standalone) {
		for (TagVisitor target : targets) {
			target.xml(version, encoding, standalone);
		}
	}

	public void doctype(Doctype doctype) {
		for (TagVisitor target : targets) {
			target.doctype(doctype);
		}
	}

	public void condComment(CharSequence expression, boolean isStartingTag, boolean isHidden, boolean isHiddenEndTag) {
		for (TagVisitor target : targets) {
			target.condComment(expression, isStartingTag, isHidden, isHiddenEndTag);
		}
	}

	public void error(String message) {
		for (TagVisitor target : targets) {
			target.error(message);
		}
	}

}