// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.util.HtmlEncoder;

import java.io.IOException;

/**
 * Tag writer outputs content to destination.
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

	public void start() {
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
			appendable.append(HtmlEncoder.text(text));
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

	public void xml(CharSequence version, CharSequence encoding, CharSequence standalone) {
		try {
			TagWriterUtil.writeXml(appendable, version, encoding, standalone);
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