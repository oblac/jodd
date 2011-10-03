// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

/**
 * Sets of {@link TagVisitor}.
 */
public class TagVisitorSet implements TagVisitor {

	protected final TagVisitor[] visitors;

	public TagVisitorSet(TagVisitor... visitors) {
		this.visitors = visitors;
	}

	public void start() {
		for (TagVisitor visitor : visitors) {
			visitor.start();
		}
	}

	public void end() {
		for (TagVisitor visitor : visitors) {
			visitor.end();
		}
	}

	public void tag(Tag tag) {
		for (TagVisitor visitor : visitors) {
			visitor.tag(tag);
		}
	}

	public void xmp(Tag tag, CharSequence body) {
		for (TagVisitor visitor : visitors) {
			visitor.xmp(tag, body);
		}
	}

	public void script(Tag tag, CharSequence body) {
		for (TagVisitor visitor : visitors) {
			visitor.script(tag, body);
		}
	}

	public void comment(CharSequence comment) {
		for (TagVisitor visitor : visitors) {
			visitor.comment(comment);
		}
	}

	public void text(CharSequence text) {
		for (TagVisitor visitor : visitors) {
			visitor.text(text);
		}
	}

	public void cdata(CharSequence cdata) {
		for (TagVisitor visitor : visitors) {
			visitor.cdata(cdata);
		}
	}

	public void xml(Tag tag) {
		for (TagVisitor visitor : visitors) {
			visitor.xml(tag);
		}
	}

	public void directive(CharSequence directive) {
		for (TagVisitor visitor : visitors) {
			visitor.directive(directive);
		}
	}

	public void condCommentStart(CharSequence conditionalComment, boolean isDownlevelHidden) {
		for (TagVisitor visitor : visitors) {
			visitor.condCommentStart(conditionalComment, isDownlevelHidden);
		}
	}

	public void condCommentEnd(CharSequence conditionalComment, boolean isDownlevelHidden) {
		for (TagVisitor visitor : visitors) {
			visitor.condCommentEnd(conditionalComment, isDownlevelHidden);
		}
	}

	public void error(String message) {
		for (TagVisitor visitor : visitors) {
			visitor.error(message);
		}
	}
}
