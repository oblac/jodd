// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import javax.activation.DataSource;

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
}
