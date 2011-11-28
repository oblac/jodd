// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.io.FastByteArrayOutputStream;
import jodd.io.FileUtil;
import jodd.io.StreamUtil;

import javax.activation.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Email attachment.
 */
public abstract class EmailAttachment {

	protected final String name;
	protected final String contentId;

	protected EmailAttachment(String name, String contentId) {
		this.name = name;
		this.contentId = contentId;
	}

	/**
	 * Returns attachment name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns content id for inline attachments, may be <code>null</code>.
	 */
	public String getContentId() {
		return contentId;
	}

	/**
	 * Returns <code>true</code> if it is inline attachment.
	 */
	public boolean isInline() {
		return contentId != null;
	}

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
