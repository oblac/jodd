// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

/**
 * Empty tag visitor.
 */
public class EmptyTagVisitor implements TagVisitor {

	public void start(LagartoParserContext parserContext) {
	}

	public void end() {
	}

	public void tag(Tag tag) {
	}

	public void script(Tag tag, CharSequence body) {
	}

	public void style(Tag tag, CharSequence body) {
	}

	public void comment(CharSequence comment) {
	}

	public void text(CharSequence text) {
	}

	public void cdata(CharSequence cdata) {
	}

	public void xml(Tag tag) {
	}

	public void doctype(Doctype doctype) {
	}

	public void condComment(CharSequence expression, boolean isStartingTag, boolean isHidden, CharSequence comment) {
	}

	public void error(String message) {
	}
}
