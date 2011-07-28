// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail.att;

import jodd.mail.EmailAttachment;

import javax.activation.DataSource;

/**
 * Byte array attachment.
 */
public class ByteArrayAttachment extends EmailAttachment {

	protected final byte[] content;
	protected final String contentType;

	public ByteArrayAttachment(byte[] content, String contentType, String name, String contentId) {
		super(name, contentId);
		this.content = content;
		this.contentType = contentType;
	}

	@Override
	public DataSource getDataSource() {
		return new StreamDataSource(content, contentType);
	}
}
