// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.mail.att.ByteArrayAttachment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Received email.
 */
public class ReceivedEmail extends CommonEmail {

	public static final int ANSWERED 	= 1;
	public static final int DELETED 	= 2;
	public static final int DRAFT 		= 4;
	public static final int FLAGGED 	= 8;
	public static final int RECENT 		= 16;
	public static final int SEEN 		= 32;
	public static final int USER 		= 0x80000000;


	// ---------------------------------------------------------------- number and flag

	protected int messageNumber;

	public int getMessageNumber() {
		return messageNumber;
	}

	public void setMessageNumber(int messageNumber) {
		this.messageNumber = messageNumber;
	}

	protected int flags;

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public void addFlags(int flag) {
		this.flags |= flag;
	}

	public void removeFlags(int flag) {
		this.flags &= ~flag;
	}

	public boolean hasFlags(int flags) {
		return (this.flags & flags) != 0;
	}

	// ---------------------------------------------------------------- date

	protected Date recvDate;

	/**
	 * Sets e-mails receive date.
	 */
	public void setReceiveDate(Date date) {
		recvDate = date;
	}

	/**
	 * Returns emails received date.
	 */
	public Date getReceiveDate() {
		return recvDate;
	}

	// ---------------------------------------------------------------- attachments

	protected List<EmailAttachment> attachments;

	/**
	 * Adds received attachment.
	 */
	public void addAttachment(String filename, String mimeType, String contentId, byte[] content) {
		if (attachments == null) {
			attachments = new ArrayList<EmailAttachment>();
		}
		EmailAttachment emailAttachment = new ByteArrayAttachment(content, mimeType, filename, contentId);
		emailAttachment.setSize(content.length);
		attachments.add(emailAttachment);
	}

	/**
	 * Returns the list of all attachments.
	 * If no attachment is available, returns <code>null</code>.
	 */
	public List<EmailAttachment> getAttachments() {
		return attachments;
	}
}
