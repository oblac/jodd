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
 * Handler that receives callbacks as content is parsed.
 */
public interface TagVisitor {

	// ---------------------------------------------------------------- state

	/**
	 * Invoked on very beginning of the visiting.
	 */
	void start();

	/**
	 * Invoked at the end, after all content is visited.
	 */
	void end();

	// ---------------------------------------------------------------- html

	/**
	 * Invoked on DOCTYPE directive.
	 */
	void doctype(Doctype doctype);

	/**
	 * Invoked on {@link Tag tag} (open, close or empty).
	 * <p>
	 * Warning: the passed tag instance <b>should not</b> be kept beyond
	 * this method as the parser reuse it!</p>
	 */
	void tag(Tag tag);

	/**
	 * Invoked on <b>script</b> tag.
	 */
	void script(Tag tag, CharSequence body);

	/**
	 * Invoked on comment.
	 */
	void comment(CharSequence comment);

	/**
	 * Invoked on text i.e. anything other than a tag.
	 */
	void text(CharSequence text);

	/**
	 * Invoked on IE conditional comment. By default, the parser does <b>not</b>
	 * process the conditional comments, so you need to turn them on. Once conditional
	 * comments are enabled, this even will be fired.
	 * <p>
	 * The following conditional comments are recognized:
	 * {@code
	 * <!--[if IE 6]>one<![endif]-->
	 * <!--[if IE 6]><!-->two<!---<![endif]-->
	 * <!--[if IE 6]>three<!--xx<![endif]-->
	 * <![if IE 6]>four<![endif]>
	 * }
	 */
	void condComment(CharSequence expression, boolean isStartingTag, boolean isHidden, boolean isHiddenEndTag);

	// ---------------------------------------------------------------- xml

	/**
	 * Invoked on <b>xml</b> declaration.
	 */
	void xml(CharSequence version, CharSequence encoding, CharSequence standalone);

	/**
	 * Invoked on CDATA sequence.
	 */
	void cdata(CharSequence cdata);

	// ---------------------------------------------------------------- errors

	/**
	 * Warn about parsing error. Usually, parser will try to continue.
	 * @param message parsing error message
	 */
	void error(String message);

}