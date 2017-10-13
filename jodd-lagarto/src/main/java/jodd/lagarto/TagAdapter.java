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

/**
 * Tag adapter.
 */
public class TagAdapter implements TagVisitor {

	protected final TagVisitor target;

	public TagAdapter(TagVisitor target) {
		this.target = target;
	}

	/**
	 * Returns target tag visitor. It may be another
	 * nested <code>TagAdapter</code> or <code>TagWriter</code>.
	 */
	public TagVisitor getTarget() {
		return target;
	}

	public void start() {
		target.start();
	}

	public void end() {
		target.end();
	}

	public void tag(Tag tag) {
		target.tag(tag);
	}

	public void script(Tag tag, CharSequence body) {
		target.script(tag, body);
	}

	public void comment(CharSequence comment) {
		target.comment(comment);
	}

	public void text(CharSequence text) {
		target.text(text);
	}

	public void cdata(CharSequence cdata) {
		target.cdata(cdata);
	}

	public void xml(CharSequence version, CharSequence encoding, CharSequence standalone) {
		target.xml(version, encoding, standalone);
	}

	public void doctype(Doctype doctype) {
		target.doctype(doctype);
	}

	public void condComment(CharSequence expression, boolean isStartingTag, boolean isHidden, boolean isHiddenEndTag) {
		target.condComment(expression, isStartingTag, isHidden, isHiddenEndTag);
	}

	public void error(String message) {
		target.error(message);
	}
}
