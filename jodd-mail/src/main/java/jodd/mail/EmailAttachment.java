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
import jodd.io.FileUtil;
import jodd.io.StreamUtil;

import javax.activation.DataSource;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Email attachment.
 */
public abstract class EmailAttachment {

	protected final String name;
	protected final String contentId;
	protected final boolean inline;
	protected EmailMessage targetMessage;

	/**
	 * Creates new attachment with given name and content id for inline attachments.
	 * Content id may be <code>null</code> if attachment is not embedded.
	 * Email name may be <code>null</code> as well.
	 */
	protected EmailAttachment(String name, String contentId, boolean inline) {
		if (name != null) {
			try {
				this.name = MimeUtility.decodeText(name);
			} catch (UnsupportedEncodingException ueex) {
				throw new MailException(ueex);
			}
		} else {
			this.name = null;
		}
		this.contentId = contentId;
		this.inline = inline;
	}

	/**
	 * Creates {@link EmailAttachmentBuilder builder} for convenient
	 * building of the email attachments.
	 */
	public static EmailAttachmentBuilder attachment() {
		return new EmailAttachmentBuilder();
	}

	/**
	 * Returns attachment name. May be <code>null</code>.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns encoded attachment name. May be <code>null</code>.
	 */
	public String getEncodedName() {
		if (name == null) {
			return null;
		}
		try {
			return MimeUtility.encodeText(name);
		} catch (UnsupportedEncodingException ueex) {
			throw new MailException(ueex);
		}
	}

	/**
	 * Returns content id for inline attachments.
	 * Equals to <code>null</code> when attachment is not embedded.
	 * @see #isEmbedded()
	 */
	public String getContentId() {
		return contentId;
	}

	/**
	 * Returns {@code true} if attachment is embedded.
	 * Embedded attachment is one when {@link #getContentId() contentId} is not
	 * {@code null}.
	 */
	public boolean isEmbedded() {
		return contentId != null;
	}

	/**
	 * Returns <code>true</code> if it is inline attachment.
	 */
	public boolean isInline() {
		return inline;
	}

	/**
	 * Sets target message for embedded attachments.
	 */
	public void setEmbeddedMessage(EmailMessage emailMessage) {
		targetMessage = emailMessage;
	}

	/**
	 * Returns <code>true</code> if attachment is embedded into provided message.
	 */
	public boolean isEmbeddedInto(EmailMessage emailMessage) {
		return targetMessage == emailMessage;
	}

	// ---------------------------------------------------------------- data source

	/**
	 * Returns <code>DataSource</code> implementation, depending of attachment source.
	 */
	public abstract DataSource getDataSource();

	// ---------------------------------------------------------------- size

	protected int size = -1;

	/**
	 * Returns size of <b>received</b> attachment,
	 */
	public int getSize() {
		return size;
	}

	protected void setSize(int size) {
		this.size = size;
	}

	// ---------------------------------------------------------------- content methods

	/**
	 * Returns byte content of the attachment.
	 */
	public byte[] toByteArray() {
		FastByteArrayOutputStream out = size != -1 ?
				new FastByteArrayOutputStream(size) :
				new FastByteArrayOutputStream();

		writeToStream(out);
		return out.toByteArray();
	}

	/**
	 * Saves attachment to a file.
	 */
	public void writeToFile(File destination) {
		InputStream in = null;
		try {
			in = getDataSource().getInputStream();
			FileUtil.writeStream(destination, in);
		} catch (IOException ioex) {
			throw new MailException(ioex);
		} finally {
			StreamUtil.close(in);
		}
	}

	/**
	 * Saves attachment to output stream.
	 */
	public void writeToStream(OutputStream out) {
		InputStream in = null;
		try {
			in = getDataSource().getInputStream();
			StreamUtil.copy(in, out);
		} catch (IOException ioex) {
			throw new MailException(ioex);
		} finally {
			StreamUtil.close(in);
		}
	}
}
