// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.mail.att.ByteArrayAttachment;

import javax.mail.Flags;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Received email.
 */
public class ReceivedEmail extends CommonEmail {

	protected int messageNumber;
	protected Flags flags;

	public int getMessageNumber() {
		return messageNumber;
	}

	public void setMessageNumber(int messageNumber) {
		this.messageNumber = messageNumber;
	}

	public Flags getFlags() {
		return flags;
	}

	public void setFlags(Flags flags) {
		this.flags = flags;
	}

	/**
	 * Returns <code>true</code> if message is answered.
	 */
	public boolean isAnswered() {
		return flags.contains(Flags.Flag.ANSWERED);
	}

	/**
	 * Returns <code>true</code> if message is deleted.
	 */
	public boolean isDeleted() {
		return flags.contains(Flags.Flag.DELETED);
	}

	/**
	 * Returns <code>true</code> if message is draft.
	 */
	public boolean isDraf() {
		return flags.contains(Flags.Flag.DRAFT);
	}

	/**
	 * Returns <code>true</code> is message is flagged.
	 */
	public boolean isFlagged() {
		return flags.contains(Flags.Flag.FLAGGED);
	}

	/**
	 * Returns <code>true</code> if message is recent.
	 */
	public boolean isRecent() {
		return flags.contains(Flags.Flag.RECENT);
	}

	/**
	 * Returns <code>true</code> if message is seen.
	 */
	public boolean isSeen() {
		return flags.contains(Flags.Flag.SEEN);
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
