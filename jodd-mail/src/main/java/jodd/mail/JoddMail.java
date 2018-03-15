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

import javax.mail.internet.MimeUtility;

/**
 * Jodd MAIL module.
 */
public class JoddMail {

	private static final JoddMail instance = new JoddMail();

	public static JoddMail defaults() {
		return instance;
	}

	// ---------------------------------------------------------------- instance

	private boolean mailMimeEncodefilename = true;
	private boolean mailMimeDecodefilename = true;

	/**
	 * @return {@code true} if mailMimeEncodefilename is {@code true}.
	 * @see #setMailMimeEncodefilename(boolean)
	 */
	public boolean isMailMimeEncodefilename() {
		return mailMimeEncodefilename;
	}

	/**
	 * Sets mailMimeEncodefilename variable.
	 *
	 * @param mailMimeEncodefilename If set to {@code true}, the setFileName method uses the
	 *                               {@link MimeUtility#encodeText(String)} to encode any non-ASCII characters in the filename.
	 *                               Note that this encoding violates the MIME specification, but is useful for interoperating
	 *                               with some mail clients that use this convention. The default is {@code true}.
	 */
	public void setMailMimeEncodefilename(final boolean mailMimeEncodefilename) {
		this.mailMimeEncodefilename = mailMimeEncodefilename;
	}

	/**
	 * @see #setMailMimeDecodefilename(boolean)
	 */
	public boolean isMailMimeDecodefilename() {
		return mailMimeDecodefilename;
	}

	/**
	 * Sets mailMimeDecodefilename variable.
	 *
	 * @param mailMimeDecodefilename If set to {@code true}, the getFileName method uses the
	 *                               {@link MimeUtility#decodeText(String)} to decode any non-ASCII characters in the filename.
	 *                               Note that this decoding violates the MIME specification, but is useful for interoperating
	 *                               with some mail clients that use this convention. The default is true.
	 */
	public void setMailMimeDecodefilename(final boolean mailMimeDecodefilename) {
		this.mailMimeDecodefilename = mailMimeDecodefilename;
	}

}