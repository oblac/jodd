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

	public TagAdapter(final TagVisitor target) {
		this.target = target;
	}

	/**
	 * Returns target tag visitor. It may be another
	 * nested <code>TagAdapter</code> or <code>TagWriter</code>.
	 */
	public TagVisitor getTarget() {
		return target;
	}

	@Override
	public void start() {
		target.start();
	}

	@Override
	public void end() {
		target.end();
	}

	@Override
	public void tag(final Tag tag) {
		target.tag(tag);
	}

	@Override
	public void script(final Tag tag, final CharSequence body) {
		target.script(tag, body);
	}

	@Override
	public void comment(final CharSequence comment) {
		target.comment(comment);
	}

	@Override
	public void text(final CharSequence text) {
		target.text(text);
	}

	@Override
	public void cdata(final CharSequence cdata) {
		target.cdata(cdata);
	}

	@Override
	public void xml(final CharSequence version, final CharSequence encoding, final CharSequence standalone) {
		target.xml(version, encoding, standalone);
	}

	@Override
	public void doctype(final Doctype doctype) {
		target.doctype(doctype);
	}

	@Override
	public void condComment(final CharSequence expression, final boolean isStartingTag, final boolean isHidden, final boolean isHiddenEndTag) {
		target.condComment(expression, isStartingTag, isHidden, isHiddenEndTag);
	}

	@Override
	public void error(final String message) {
		target.error(message);
	}
}
