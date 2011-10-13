// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import java.io.IOException;

/**
 * Tag writer outputs content to destination.
 */
public class TagWriter implements TagVisitor {

	private final boolean build;
	private Appendable appendable;

	public TagWriter(Appendable appendable) {
		this.appendable = appendable;
		this.build = false;
	}

	public TagWriter(Appendable appendable, boolean build) {
		this.appendable = appendable;
		this.build = build;
	}

	public void setOutput(Appendable out) {
		this.appendable = out;
	}

	public Appendable getOutput() {
		return appendable;
	}

	// ---------------------------------------------------------------- visitor

	public void start() {
	}

	public void end() {
	}

	public void tag(Tag tag) {
		try {
			tag.writeTo(appendable, build);
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void xmp(Tag tag, CharSequence body) {
		try {
			tag.writeTo(appendable, build);
			if (body != null) {
				appendable.append(body);
			}
			appendable.append("</xmp>");
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void script(Tag tag, CharSequence body) {
		try {
			tag.writeTo(appendable, build);
			if (body != null) {
				appendable.append(body);
			}
			appendable.append("</script>");
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void comment(CharSequence comment) {
		try {
			appendable.append("<!--");
			appendable.append(comment);
			appendable.append("-->");
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void text(CharSequence text) {
		try {
			appendable.append(text);
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void cdata(CharSequence cdata) {
		try {
			appendable.append("<![CDATA[");
			appendable.append(cdata);
			appendable.append("]]>");
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void xml(Tag tag) {
		try {
			tag.writeTo(appendable, build);
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void directive(CharSequence directive) {
		try {
			appendable.append("<!");
			appendable.append(directive);
			appendable.append(">");
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void condCommentStart(CharSequence conditionalComment, boolean isDownlevelHidden) {
		try {
			if (isDownlevelHidden) {
				appendable.append("<!--[");
			} else {
				appendable.append("<![");
			}
			appendable.append(conditionalComment);
			appendable.append("]>");
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void condCommentEnd(CharSequence conditionalComment, boolean isDownlevelHidden) {
		try {
			appendable.append("<![");
			appendable.append(conditionalComment);
			if (isDownlevelHidden) {
				appendable.append("]-->");
			} else {
				appendable.append("]>");
			}
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void error(String message) {
	}
}