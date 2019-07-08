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
import jodd.io.StreamUtil;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Developer-friendly class for parsing EML files.
 */
public class EMLParser extends EMLProperties<EMLParser> {

	public static EMLParser create() {
		return new EMLParser();
	}

	/**
	 * Parses EML with provided EML content.
	 *
	 * @param emlContent {@link String} with EML content.
	 * @param charset    String with charset.
	 * @return {@link ReceivedEmail}.
	 * @throws UnsupportedEncodingException if the named charset is not supported.
	 * @throws MessagingException           if {@link MimeMessage} cannot be created.
	 * @see #parse(byte[])
	 */
	public ReceivedEmail parse(final String emlContent, final String charset) throws
			UnsupportedEncodingException, MessagingException {

		final byte[] bytes = emlContent.getBytes(charset);
		return parse(bytes);
	}

	/**
	 * Parses EML with provided EML content.
	 *
	 * @param emlContent {@link String} with EML content.
	 * @return {@link ReceivedEmail}.
	 * @throws MessagingException if {@link MimeMessage} cannot be created.
	 * @see #parse(String, String)
	 */
	public ReceivedEmail parse(final String emlContent) throws MessagingException {
		try {
			return parse(emlContent, JoddCore.encoding);
		} catch (final UnsupportedEncodingException ignore) {
			return null;
		}
	}

	/**
	 * Parses EML with provided EML content.
	 *
	 * @param content byte[] with EML content.
	 * @return {@link ReceivedEmail}.
	 * @throws MessagingException if {@link MimeMessage} cannot be created.
	 * @see #parse(InputStream)
	 */
	public ReceivedEmail parse(final byte[] content) throws MessagingException {
		return parse(new ByteArrayInputStream(content));
	}

	/**
	 * Starts EML parsing with provided EML {@link File}.
	 *
	 * @param emlFile {@link File} with EML content.
	 * @return {@link ReceivedEmail}.
	 * @throws FileNotFoundException if emlFile cannot be found
	 * @throws MessagingException    if {@link MimeMessage} cannot be created.
	 * @see #parse(InputStream)
	 */
	public ReceivedEmail parse(final File emlFile) throws FileNotFoundException, MessagingException {
		final FileInputStream fileInputStream = new FileInputStream(emlFile);
		try {
			return parse(fileInputStream);
		} finally {
			StreamUtil.close(fileInputStream);
		}
	}

	/**
	 * Parses the EML content. If {@link Session} is not created, default one will be used.
	 *
	 * @param emlContentInputStream {@link InputStream} containing the EML content.
	 * @return {@link ReceivedEmail}.
	 * @throws MessagingException if {@link MimeMessage} cannot be created.
	 */
	protected ReceivedEmail parse(final InputStream emlContentInputStream) throws MessagingException {
		if (getSession() == null) {
			createSession(getProperties());
		}

		try {
			final MimeMessage message = new MimeMessage(getSession(), emlContentInputStream);
			return new ReceivedEmail(message, false, null);
		} finally {
			StreamUtil.close(emlContentInputStream);
		}
	}
}