// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

/**
 * Empty tag visitor.
 */
public class EmptyTagVisitor implements TagVisitor {

	public void start() {
	}

	public void end() {
	}

	public void tag(Tag tag) {
	}

	public void xmp(Tag tag, CharSequence body) {
	}

	public void script(Tag tag, CharSequence body) {
	}

	public void comment(CharSequence comment) {
	}

	public void text(CharSequence text) {
	}

	public void cdata(CharSequence cdata) {
	}

	public void xml(Tag tag) {
	}

	public void directive(CharSequence directive) {
	}

	public void condCommentStart(CharSequence conditionalComment, boolean isDownlevelHidden) {
	}

	public void condCommentEnd(CharSequence conditionalComment, boolean isDownlevelHidden) {
	}

	public void error(String message) {
	}
}
