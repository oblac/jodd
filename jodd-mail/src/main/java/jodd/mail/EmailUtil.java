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
import jodd.core.JoddCoreDefaults;
import jodd.io.FastByteArrayOutputStream;
import jodd.io.StreamUtil;
import jodd.util.CharUtil;
import jodd.util.StringPool;

import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Email utilities.
 */
public class EmailUtil {

	protected static final String ATTR_CHARSET = "charset=";
	static final String NO_NAME = "no-name";

	/**
	 * Extracts MIME type from content type.
	 *
	 * @param contentType MIME type.
	 * @return MIME type for the given content type.
	 */
	//TODO: should this always return lowercase or always uppercase?
	public static String extractMimeType(final String contentType) {
		final int ndx = contentType.indexOf(';');
		final String mime;
		if (ndx != -1) {
			mime = contentType.substring(0, ndx);
		} else {
			mime = contentType;
		}
		return mime;
	}

	/**
	 * Extracts encoding from a given content type.
	 *
	 * @param contentType content type.
	 * @return Encoding from the content type. May return {@code null} if encoding is not specified in content type.
	 */
	//TODO: should this always return lowercase or always uppercase?
	public static String extractEncoding(final String contentType) {
		int ndx = contentType.indexOf(';');
		final String charset = ndx != -1 ? contentType.substring(ndx + 1) : StringPool.EMPTY;
		String encoding = null;

		ndx = charset.indexOf(ATTR_CHARSET);
		if (ndx != -1) {
			ndx += ATTR_CHARSET.length();
			final int len = charset.length();

			if (charset.charAt(ndx) == '"') {
				ndx++;
			}
			final int start = ndx;

			while (ndx < len) {
				final char c = charset.charAt(ndx);
				if ((c == '"') || (CharUtil.isWhitespace(c)) || (c == ';')) {
					break;
				}
				ndx++;
			}
			encoding = charset.substring(start, ndx);
		}
		return encoding;
	}

	/**
	 * Extracts encoding from a given content type.
	 *
	 * @param contentType     content type.
	 * @param defaultEncoding Default encoding to be used if extract returns {@code null}.
	 *                        If defaultEncoding is {@code null}, {@link JoddCoreDefaults#getEncoding()} will be used.
	 * @return Encoding from the content type.
	 * @see #extractEncoding(String)
	 * @see JoddCoreDefaults#getEncoding()
	 */
	public static String extractEncoding(final String contentType, String defaultEncoding) {
		String encoding = extractEncoding(contentType);

		if (encoding == null) {
			if (defaultEncoding == null) {
				defaultEncoding = JoddCore.get().defaults().getEncoding();
			}
			encoding = defaultEncoding;
		}
		return encoding;
	}

	/**
	 * Correctly resolves file name from the message part.
	 * Thanx to: Flavio Pompermaier
	 *
	 * @param part {@link Part} to decode file name from.
	 * @return String containing file name.
	 */
	public static String resolveFileName(final Part part) throws MessagingException {
		if (!(part instanceof MimeBodyPart)) {
			return part.getFileName();
		}

		final String contentType = part.getContentType();
		String ret;

		try {
			ret = MimeUtility.decodeText(part.getFileName());
		} catch (final Exception ex) {
			// String[] contentId = part.getHeader("Content-ID");
			// if (contentId != null && contentId.length > 0) {
			final String contentId = ((MimeBodyPart) part).getContentID();
			if (contentId != null) {
				ret = contentId + contentTypeForFileName(contentType);
			} else {
				ret = defaultFileName(contentType);
			}
		}

		return ret;
	}

	private static String contentTypeForFileName(final String contentType) {
		return StringPool.DOT + contentType.substring(contentType.lastIndexOf("/") + 1, contentType.length());
	}

	private static String defaultFileName(final String contentType) {
		return NO_NAME + contentTypeForFileName(contentType);
	}

	/**
	 * Setups the system email properties.
	 */
	public static void setupSystemMailProperties() {
		final JoddMailDefaults defaults = JoddMail.get().defaults();
		System.setProperty("mail.mime.encodefilename", Boolean.valueOf(defaults.isMailMimeEncodefilename()).toString());
		System.setProperty("mail.mime.decodefilename", Boolean.valueOf(defaults.isMailMimeDecodefilename()).toString());
	}

	/**
	 * @param protocol          Protocol such as {@link ImapServer#PROTOCOL_IMAP} or {@link Pop3Server#PROTOCOL_POP3}.
	 * @param sessionProperties Session properties to use.
	 * @param authenticator     Authenticator which contains necessary authentication for server.
	 * @return {@link ReceiveMailSession}.
	 */
	public static ReceiveMailSession createSession(final String protocol, final Properties sessionProperties, final Authenticator authenticator) {
		final Session session = Session.getInstance(sessionProperties, authenticator);
		final Store store;
		try {
			store = session.getStore(protocol);
		} catch (final NoSuchProviderException nspex) {
			final String errMsg = String.format("Failed to create %s session", protocol);
			throw new MailException(errMsg, nspex);
		}
		return new ReceiveMailSession(session, store);
	}

	/**
	 * Copies data from {@link InputStream} to {@link OutputStream} out, closes the {@link InputStream} and returns output.
	 *
	 * @param input  {@link InputStream} to copy from.
	 * @param output {@link OutputStream} to copy to.
	 * @return {@link OutputStream} out with data.
	 * @throws MailException in place of {@link IOException}.
	 * @see StreamUtil#copy(InputStream, OutputStream)
	 */
	public static OutputStream copyStream(final InputStream input, final OutputStream output) throws MailException {
		try {
			StreamUtil.copy(input, output);
		} catch (final IOException ioex) {
			throw new MailException(ioex);
		} finally {
			StreamUtil.close(input);
		}
		return output;
	}

	/**
	 * Copies data from {@link DataSource} to {@link OutputStream}, closes the {@link InputStream} and returns output.
	 *
	 * @param input  {@link DataSource} to copy from.
	 * @param output {@link OutputStream} to copy to.
	 * @return new {@link FastByteArrayOutputStream} with data from input.
	 * @see #copyStream(InputStream, OutputStream)
	 */
	public static OutputStream copyStream(final DataSource input, final OutputStream output) throws MailException {
		try {
			copyStream(input.getInputStream(), output);
			return output;
		} catch (final IOException ioex) {
			throw new MailException(ioex);
		}
	}

	/**
	 * Copies data from {@link DataSource} to {@link File}, closes the {@link DataSource} and returns output.
	 *
	 * @param input  {@link DataSource} to copy from.
	 * @param output {@link OutputStream} to copy to.
	 * @return new {@link FastByteArrayOutputStream} with data from input.
	 * @see #copyStream(InputStream, OutputStream)
	 */
	public static File copyStream(final DataSource input, final File output) throws MailException {
		try {
			copyStream(input.getInputStream(), new FileOutputStream(output));
			return output;
		} catch (final IOException ioex) {
			throw new MailException(ioex);
		}
	}
}