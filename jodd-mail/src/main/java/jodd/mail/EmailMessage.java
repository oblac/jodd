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

package jodd.mail;

import jodd.core.JoddCore;

/**
 * Represents email message including the mime type and encoding.
 */
public class EmailMessage {

	/**
	 * The content as a {@link String}.
	 */
	private final String content;

	/**
	 * The MIME type as a as a {@link String}.
	 */
	private final String mimeType;

	/**
	 * The encoding as a {@link String}.
	 */
	private final String encoding;

	/**
	 * Defines email content.
	 *
	 * @param content  The content as a {@link String}.
	 * @param mimeType The MIME type as a as a {@link String}.
	 * @param encoding The encoding as a {@link String}.
	 */
	public EmailMessage(final String content, final String mimeType, final String encoding) {
		this.content = content;
		this.mimeType = mimeType;
		this.encoding = encoding;
	}

	/**
	 * Uses UTF-8 email content by default (as per {@link JoddCore#encoding}.
	 *
	 * @param content  The content as a {@link String}.
	 * @param mimeType The MIME type as a as a {@link String}.
	 */
	public EmailMessage(final String content, final String mimeType) {
		this(content, mimeType, JoddCore.encoding);
	}

	// ---------------------------------------------------------------- getters

	/**
	 * Returns message content.
	 *
	 * @return {@link String} containing the message content.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Returns message mime type.
	 *
	 * @return {@link String} containing the message mime type.
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Returns message encoding.
	 *
	 * @return {@link String} containing the message encoding.
	 */
	public String getEncoding() {
		return encoding;
	}
}
