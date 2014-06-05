// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import java.io.IOException;

/**
 * Tag writer outputs content to destination.
 * As writer is usually called at the end of visitor chain,
 * it will not handle or warn about any errors.
 */
public class TagWriter implements TagVisitor {

	protected Appendable appendable;

	public TagWriter(Appendable appendable) {
		this.appendable = appendable;
	}

	public void setOutput(Appendable out) {
		this.appendable = out;
	}

	public Appendable getOutput() {
		return appendable;
	}

	// ---------------------------------------------------------------- visitor

	public void start(LagartoParserContext parserContext) {
	}

	public void end() {
	}

	public void tag(Tag tag) {
		try {
			tag.writeTo(appendable);
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void script(Tag tag, CharSequence body) {
		try {
			tag.writeTo(appendable);
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
			tag.writeTo(appendable);
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void doctype(Doctype doctype) {
		try {
			TagWriterUtil.writeDoctype(
					appendable,
					doctype.getName(),
					doctype.getPublicIdentifier(),
					doctype.getSystemIdentifier());
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void condComment(CharSequence expression, boolean isStartingTag, boolean isHidden, boolean isHiddenEndTag) {
		try {
			TagWriterUtil.writeConditionalComment(appendable, expression, isStartingTag, isHidden, isHiddenEndTag);
		} catch (IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	public void error(String message) {
	}
}