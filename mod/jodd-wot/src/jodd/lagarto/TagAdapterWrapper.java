// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

/**
 * Adapter wrapper over a visitor that calls target after the execution.
 */
public class TagAdapterWrapper implements TagVisitor {

	protected final TagVisitor target;
	protected final TagVisitor visitor;

	public TagAdapterWrapper(TagVisitor visitor, TagVisitor target) {
		this.visitor = visitor;
		this.target = target;
	}

	public void start() {
		visitor.start();
		target.start();
	}

	public void end() {
		visitor.start();
		target.end();
	}

	public void tag(Tag tag) {
		visitor.tag(tag);
		target.tag(tag);
	}

	public void xmp(Tag tag, CharSequence body) {
		visitor.xmp(tag, body);
		target.xmp(tag, body);
	}

	public void style(Tag tag, CharSequence body) {
		visitor.style(tag, body);
		target.style(tag, body);
	}

	public void script(Tag tag, CharSequence body) {
		visitor.script(tag, body);
		target.script(tag, body);
	}

	public void comment(CharSequence comment) {
		visitor.comment(comment);
		target.comment(comment);
	}

	public void text(CharSequence text) {
		visitor.text(text);
		target.text(text);
	}

	public void cdata(CharSequence cdata) {
		visitor.cdata(cdata);
		target.cdata(cdata);
	}

	public void xml(Tag tag) {
		visitor.xml(tag);
		target.xml(tag);
	}

	public void directive(CharSequence directive) {
		visitor.directive(directive);
		target.directive(directive);
	}

	public void condComment(CharSequence conditionalComment, boolean isStartingTag, boolean isDownlevelHidden) {
		visitor.condComment(conditionalComment, isStartingTag, isDownlevelHidden);
		target.condComment(conditionalComment, isStartingTag, isDownlevelHidden);
	}

	public void error(String message) {
		visitor.error(message);
		target.error(message);
	}
}
