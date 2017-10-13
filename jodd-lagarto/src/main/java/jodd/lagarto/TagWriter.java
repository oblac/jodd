// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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