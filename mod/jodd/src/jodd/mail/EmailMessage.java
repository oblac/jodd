// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.JoddDefault;
import jodd.util.MimeTypes;

/**
 * Represents e-mail message: string with mime type and encoding.
 */
public class EmailMessage {

	private final String content;
	private final String mimeType;
	private final String encoding;

	/**
	 * Defines email content.
	 */
	public EmailMessage(String content, String mimeType, String encoding) {
		this.content = content;
		this.mimeType = mimeType;
		this.encoding = encoding;
	}

	/**
	 * Defines UTF 8 email content.
	 */
	public EmailMessage(String content, String mimeType) {
		this.content = content;
		this.mimeType = mimeType;
		this.encoding = JoddDefault.encoding;
	}

	/**
	 * Defines text/plain UTF 8 email content.
	 */
	public EmailMessage(String content) {
		this.content = content;
		this.mimeType = MimeTypes.MIME_TEXT_PLAIN;
		this.encoding = JoddDefault.encoding;
	}

	// ---------------------------------------------------------------- getters

	/**
	 * Returns message content.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Returns message mime type.
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Returns message encoding.
	 */
	public String getEncoding() {
		return encoding;
	}
}
