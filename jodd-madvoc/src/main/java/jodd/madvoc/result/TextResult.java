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

package jodd.madvoc.result;

import jodd.madvoc.meta.RenderWith;
import jodd.net.HttpStatus;
import jodd.net.MimeTypes;

import java.util.Objects;

/**
 * Text result.
 */
@RenderWith(TextActionResult.class)
public class TextResult {

	private final String value;
	private int status = 200;
	private String mimeType = MimeTypes.MIME_TEXT_PLAIN;

	public static TextResult of(final String value) {
		return new TextResult(value);
	}

	public TextResult(final String value) {
		this.value = value;
	}

	/**
	 * Sets content type to HTML.
	 */
	public TextResult asHtml() {
		mimeType = MimeTypes.MIME_TEXT_HTML;
		return this;
	}

	/**
	 * Defines custom content type.
	 */
	public TextResult as(final String contentType) {
		Objects.requireNonNull(contentType);
		mimeType = contentType;
		return this;
	}

	public TextResult status(final int status) {
		this.status = status;
		return this;
	}

	public TextResult status(final HttpStatus httpStatus) {
		this.status = httpStatus.status();
		return this;
	}

	/**
	 * Returns text content.
	 */
	public String value() {
		return value;
	}

	/**
	 * Returns status.
	 */
	public int status() {
		return status;
	}

	public String contentType() {
		return mimeType;
	}
}
