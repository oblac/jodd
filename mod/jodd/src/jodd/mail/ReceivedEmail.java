// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import java.util.Date;
import java.util.HashMap;

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

	protected HashMap<String, byte[]> attachments;

	public void addAttachment(String filename, byte[] content) {
		if (attachments == null) {
			attachments = new HashMap<String,byte[]>();
		}
		attachments.put(filename, content);
	}

	public HashMap<String, byte[]> getAttachments() {
		return attachments;
	}
}
