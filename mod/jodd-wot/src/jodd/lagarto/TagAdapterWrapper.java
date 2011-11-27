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

	public void condCommentStart(CharSequence conditionalComment, boolean isDownlevelHidden) {
		visitor.condCommentStart(conditionalComment, isDownlevelHidden);
		target.condCommentStart(conditionalComment, isDownlevelHidden);
	}

	public void condCommentEnd(CharSequence conditionalComment, boolean isDownlevelHidden) {
		visitor.condCommentEnd(conditionalComment, isDownlevelHidden);
		target.condCommentEnd(conditionalComment, isDownlevelHidden);
	}

	public void error(String message) {
		visitor.error(message);
		target.error(message);
	}
}
