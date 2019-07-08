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

import jodd.io.FastByteArrayOutputStream;
import jodd.io.StreamUtil;

import javax.activation.DataSource;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Email attachment.
 */
public class EmailAttachment<T extends DataSource> {

	/**
	 * {@link String} with file name.
	 */
	private final String name;

	/**
	 * Content ID of attachment.
	 */
	private String contentId;

	/**
	 * Whether the attachment is inline.
	 */
	private boolean isInline;

	/**
	 * {@link DataSource} of the attachment.
	 */
	private final T dataSource;

	/**
	 * Target {@link EmailMessage}.
	 */
	private EmailMessage targetMessage;

	// ---------------------------------------------------------------- constructor

	/**
	 * Returns new/empty {@link EmailAttachmentBuilder}.
	 *
	 * @return {@link EmailAttachmentBuilder}.
	 */
	public static EmailAttachmentBuilder with() {
		return new EmailAttachmentBuilder();
	}

	/**
	 * Creates new attachment with given name and content id for inline attachments.
	 *
	 * @param contentId Value may be {@code null} if attachment is not embedded.
	 * @param isInline  {@code true} if the attachment is inline.
	 * @param name      Email name may be {@code null} as well.
	 * @see MimeUtility#decodeText(String)
	 */
	protected EmailAttachment(final String name, final String contentId, final boolean isInline, final T dataSource) {
		if (name != null) {
			try {
				this.name = MimeUtility.decodeText(name);
			} catch (final UnsupportedEncodingException useexc) {
				throw new MailException(useexc);
			}
		} else {
			this.name = null;
		}
		this.contentId = contentId;
		this.isInline = isInline;
		this.dataSource = dataSource;
	}

	// ---------------------------------------------------------------- properties

	/**
	 * Returns attachment name.
	 *
	 * @return attachment name. Value may be {@code null}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns encoded attachment name.
	 *
	 * @return encoded attachment name. Value may be {@code null}.
	 */
	public String getEncodedName() {
		if (name == null) {
			return null;
		}
		try {
			return MimeUtility.encodeText(name);
		} catch (final UnsupportedEncodingException ueex) {
			throw new MailException(ueex);
		}
	}

	/**
	 * Returns content id for inline attachments.
	 * <p>
	 * Value is {@code null} when attachment is not embedded.
	 *
	 * @return content id for inline attachments
	 * @see #isEmbedded()
	 */
	public String getContentId() {
		return contentId;
	}

	/**
	 * Returns {@code true} if the attachment is embedded.
	 * <p>
	 * Embedded attachment is one when {@link #getContentId()} is not {@code null}.
	 *
	 * @return {@code true} if the attachment is embedded.
	 */
	public boolean isEmbedded() {
		return contentId != null;
	}

	/**
	 * Returns {@code true} if it is an inline attachment.
	 *
	 * @return {@code true} if it is an inline attachment.
	 */
	public boolean isInline() {
		return isInline;
	}

	/**
	 * Sets whether attachment is inline.
	 *
	 * @param isInline {@code true} for inline.
	 * @return this
	 */
	protected EmailAttachment<T> setInline(final boolean isInline) {
		this.isInline = isInline;
		return this;
	}

	/**
	 * Sets content ID.
	 *
	 * @param contentId content ID of {@link EmailAttachment}.
	 * @return this
	 */
	protected EmailAttachment<T> setContentId(final String contentId) {
		this.contentId = contentId;
		return this;
	}

	/**
	 * Sets target message for embedded attachments.
	 *
	 * @param emailMessage target {@link EmailMessage}.
	 */
	public EmailAttachment<T> setEmbeddedMessage(final EmailMessage emailMessage) {
		targetMessage = emailMessage;
		return this;
	}

	/**
	 * Returns {@code true} if attachment is embedded into provided message.
	 *
	 * @param emailMessage target {@link EmailMessage}.
	 * @return {@code true} if attachment is embedded into provided message.
	 */
	public boolean isEmbeddedInto(final EmailMessage emailMessage) {
		return targetMessage == emailMessage;
	}

	// ---------------------------------------------------------------- data source

	/**
	 * Returns {@link DataSource} implementation, depending on attachment source.
	 */
	public T getDataSource() {
		return dataSource;
	}

	/**
	 * Returns content type of {@link DataSource}.
	 *
	 * @return content type of {@link DataSource}.
	 */
	public String getContentType() {
		return dataSource.getContentType();
	}

	// ---------------------------------------------------------------- size

	/**
	 * Size of attachment. Defaults to -1.
	 */
	private int size = -1;

	/**
	 * Returns size of attachment.
	 *
	 * @return size of attachment or -1 if not yet calculated from {@link DataSource}.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets size of attachment.
	 *
	 * @param size the size of the attachment.
	 * @return this
	 */
	protected EmailAttachment<T> setSize(final int size) {
		this.size = size;
		return this;
	}

	// ---------------------------------------------------------------- content methods

	/**
	 * Returns byte content of the attachment.
	 *
	 * @return byte array with content of the attachment.
	 */
	public byte[] toByteArray() {
		final FastByteArrayOutputStream out;
		if (size != -1) {
			out = new FastByteArrayOutputStream(size);
		} else {
			out = new FastByteArrayOutputStream();
		}
		writeToStream(out);
		return out.toByteArray();
	}

	/**
	 * Saves attachment to a file.
	 *
	 * @param destination The destination file to be written.
	 */
	public void writeToFile(final File destination) {
		InputStream input = null;
		final OutputStream output;
		try {
			input = getDataSource().getInputStream();
			output = new FileOutputStream(destination);

			StreamUtil.copy(input, output);
		}
		catch (final IOException ioex) {
			throw new MailException(ioex);
		}
		finally {
			StreamUtil.close(input);
		}
	}

	/**
	 * Saves attachment to the output stream.
	 *
	 * @param out OutputStream where attachment should be copied to.
	 */
	public void writeToStream(final OutputStream out) {
		InputStream input = null;
		try {
			input = getDataSource().getInputStream();

			StreamUtil.copy(input, out);
		}
		catch (final IOException ioex) {
			throw new MailException(ioex);
		}
		finally {
			StreamUtil.close(input);
		}
	}

}
