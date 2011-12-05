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

	public void doctype(String name, String publicId, String baseUri) {
	}

	public void condComment(CharSequence conditionalComment, boolean isStartingTag, boolean isDownlevelHidden) {
	}

	public void error(String message) {
	}
}
