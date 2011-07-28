// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail.att;

import jodd.JoddDefault;
import jodd.mail.EmailAttachment;
import jodd.util.MimeTypes;

import javax.activation.DataSource;

/**
 * String attachment, never inline.
 */
public class StringAttachment extends EmailAttachment {

	protected final String content;
	protected final String mime;
	protected final String encoding;

	public StringAttachment(String content, String mime, String name) {
		this(content, mime, JoddDefault.encoding, name);
	}

	public StringAttachment(String content, String mime, String encoding, String name) {
		super(name, null);
		this.content = content;
		this.mime = mime;
		this.encoding = encoding;
	}

	/**
	 * Returns string content.
	 */
	public String getContent() {
		return content;
	}

	@Override
	public DataSource getDataSource() {
		return new StreamDataSource(content, MimeTypes.MIME_TEXT_HTML, encoding);
	}
}
