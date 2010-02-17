// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.util.MimeTypes;

import javax.activation.DataSource;

/**
 * String attachment, never inline.
 */
public class StringAttachment extends EmailAttachment {

	protected final String content;
	protected final String mime;

	public StringAttachment(String content, String mime, String name) {
		super(name, null);
		this.content = content;
		this.mime = mime;
	}

	/**
	 * Returns string content.
	 */
	public String getContent() {
		return content;
	}

	@Override
	public DataSource getDataSource() {
		return new ByteArrayDataSource(content, MimeTypes.MIME_TEXT_HTML);
	}
}
