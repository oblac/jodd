// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail.att;

import jodd.mail.EmailAttachment;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

/**
 * Byte array {@link EmailAttachment email attachment}.
 */
public class ByteArrayAttachment extends EmailAttachment {

	protected final byte[] content;
	protected final String contentType;

	public ByteArrayAttachment(byte[] content, String contentType, String name, String contentId) {
		super(name, contentId);
		this.content = content;
		this.contentType = contentType;
	}

	public ByteArrayAttachment(byte[] content, String contentType, String name) {
		super(name, null);
		this.content = content;
		this.contentType = contentType;
	}

	@Override
	public DataSource getDataSource() {
		return new ByteArrayDataSource(content, contentType);
	}

	/**
	 * Returns content type.
	 */
	public String getContentType() {
		return contentType;
	}
}
