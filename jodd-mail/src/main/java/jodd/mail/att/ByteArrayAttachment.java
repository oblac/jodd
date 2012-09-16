// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail.att;

import jodd.mail.EmailAttachment;
import jodd.mail.MailException;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;

/**
 * Byte array and input stream attachments.
 */
public class ByteArrayAttachment extends EmailAttachment {

	protected final byte[] content;
	protected final InputStream inputStream;
	protected final String contentType;

	public ByteArrayAttachment(byte[] content, String contentType, String name, String contentId) {
		super(name, contentId);
		this.content = content;
		this.inputStream = null;
		this.contentType = contentType;
	}

	public ByteArrayAttachment(byte[] content, String contentType, String name) {
		super(name, null);
		this.content = content;
		this.inputStream = null;
		this.contentType = contentType;
	}

	public ByteArrayAttachment(InputStream inputStream, String contentType, String name, String contentId) {
		super(name, contentId);
		this.content = null;
		this.inputStream = inputStream;
		this.contentType = contentType;
	}

	public ByteArrayAttachment(InputStream inputStream, String contentType, String name) {
		super(name, null);
		this.content = null;
		this.inputStream = inputStream;
		this.contentType = contentType;
	}

	@Override
	public DataSource getDataSource() {
		if (inputStream != null) {
			try {
				return new ByteArrayDataSource(inputStream, contentType);
			} catch (IOException ioex) {
				throw new MailException(ioex);
			}
		}
		if (content != null) {
			return new ByteArrayDataSource(content, contentType);
		}
		throw new MailException("No data source");
	}
}
