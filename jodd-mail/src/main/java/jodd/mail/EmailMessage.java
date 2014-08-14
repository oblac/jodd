// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.JoddCore;

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
	 * Defines UTF-8 email content.
	 */
	public EmailMessage(String content, String mimeType) {
		this.content = content;
		this.mimeType = mimeType;
		this.encoding = JoddCore.encoding;
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
