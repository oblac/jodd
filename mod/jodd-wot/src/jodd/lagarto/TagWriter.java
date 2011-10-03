// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import java.io.IOException;

/**
 * Tag writter outputs content to destination.
 */
public class TagWriter implements TagVisitor {

	private final Appendable out;
	private final boolean build;

	public TagWriter(Appendable out) {
		this.out = out;
		this.build = false;
	}

	public TagWriter(Appendable out, boolean build) {
		this.out = out;
		this.build = build;
	}

	public void start() {
	}

	public void end() {
	}

	public void tag(Tag tag) {
		try {
			tag.writeTo(out, build);
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void xmp(Tag tag, CharSequence body) {
		try {
			tag.writeTo(out, build);
			if (body != null) {
				out.append(body);
			}
			out.append("</xmp>");
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void script(Tag tag, CharSequence body) {
		try {
			tag.writeTo(out, build);
			if (body != null) {
				out.append(body);
			}
			out.append("</script>");
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void comment(CharSequence comment) {
		try {
			out.append("<!--");
			out.append(comment);
			out.append("-->");
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void text(CharSequence text) {
		try {
			out.append(text);
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void cdata(CharSequence cdata) {
		try {
			out.append("<![CDATA[");
			out.append(cdata);
			out.append("]]>");
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void xml(Tag tag) {
		try {
			tag.writeTo(out, build);
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void directive(CharSequence directive) {
		try {
			out.append("<!");
			out.append(directive);
			out.append(">");
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void condCommentStart(CharSequence conditionalComment, boolean isDownlevelHidden) {
		try {
			if (isDownlevelHidden) {
				out.append("<!--[");
			} else {
				out.append("<![");
			}
			out.append(conditionalComment);
			out.append("]>");
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void condCommentEnd(CharSequence conditionalComment, boolean isDownlevelHidden) {
		try {
			out.append("<![");
			out.append(conditionalComment);
			if (isDownlevelHidden) {
				out.append("]-->");
			} else {
				out.append("]>");
			}
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void error(String message) {
	}
}