// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import java.io.IOException;

/**
 * Tag writer outputs content to destination.
 * As writer is usually called at the end of visitor chain,
 * it will not handle or warn about any errors.
 */
public class TagWriter implements TagVisitor {

	protected final boolean forceBuild;
	protected Appendable appendable;

	public TagWriter(Appendable appendable) {
		this.appendable = appendable;
		this.forceBuild = false;
	}

	public TagWriter(Appendable appendable, boolean forceBuild) {
		this.appendable = appendable;
		this.forceBuild = forceBuild;
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
			tag.writeTo(appendable, forceBuild);
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void xmp(Tag tag, CharSequence body) {
		try {
			tag.writeTo(appendable, forceBuild);
			if (body != null) {
				appendable.append(body);
			}
			appendable.append("</xmp>");
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void style(Tag tag, CharSequence body) {
		try {
			tag.writeTo(appendable, forceBuild);
			if (body != null) {
				appendable.append(body);
			}
			appendable.append("</style>");
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void script(Tag tag, CharSequence body) {
		try {
			tag.writeTo(appendable, forceBuild);
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
			TagWriterUtil.writeComment(appendable, comment);
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
			TagWriterUtil.writeCData(appendable, cdata);
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void xml(Tag tag) {
		try {
			tag.writeTo(appendable, forceBuild);
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void doctype(String name, String publicId, String baseUri) {
		try {
			TagWriterUtil.writeDoctype(appendable, name, publicId, baseUri);
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void condComment(CharSequence conditionalComment, boolean isStartingTag, boolean isDownlevelHidden) {
		try {
			TagWriterUtil.writeConditionalComment(appendable, conditionalComment, isStartingTag, isDownlevelHidden);
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void error(String message) {
	}
}