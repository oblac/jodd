// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

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

	public void start(LagartoParserContext parserContext) {
		visitor.start(parserContext);
		target.start(parserContext);
	}

	public void end() {
		visitor.end();
		target.end();
	}

	public void tag(Tag tag) {
		visitor.tag(tag);
		target.tag(tag);
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

	public void doctype(Doctype doctype) {
		visitor.doctype(doctype);
		target.doctype(doctype);
	}

	public void condComment(CharSequence expression, boolean isStartingTag, boolean isHidden, CharSequence comment) {
		visitor.condComment(expression, isStartingTag, isHidden, comment);
		target.condComment(expression, isStartingTag, isHidden, comment);
	}

	public void error(String message) {
		visitor.error(message);
		target.error(message);
	}
}
