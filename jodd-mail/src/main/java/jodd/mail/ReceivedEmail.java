// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.mail;

import jodd.io.FastByteArrayOutputStream;
import jodd.io.StreamUtil;
import jodd.mail.att.ByteArrayAttachment;
import jodd.util.StringPool;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * Received email.
 */
public class ReceivedEmail extends CommonEmail {

	public static final ReceivedEmail[] EMPTY_ARRAY = new ReceivedEmail[0];

	public ReceivedEmail(Message message) {
		try {
			parseMessage(message);
		} catch (Exception ex) {
			throw new MailException("Message parsing failed", ex);
		}
	}

	/**
	 * Parse java <code>Message</code> and extracts all data for the received message.
	 */
	@SuppressWarnings("unchecked")
	protected void parseMessage(Message msg) throws MessagingException, IOException {
		// flags
		setFlags(msg.getFlags());

		// msg no
		setMessageNumber(msg.getMessageNumber());

		// single from
		Address[] addresses = msg.getFrom();

		if (addresses != null && addresses.length > 0) {
			setFrom(new EmailAddress(addresses[0]));
		}

		// common field
		setTo(EmailAddress.createFrom(msg.getRecipients(Message.RecipientType.TO)));
		setCc(EmailAddress.createFrom(msg.getRecipients(Message.RecipientType.CC)));
		setBcc(EmailAddress.createFrom(msg.getRecipients(Message.RecipientType.BCC)));

		// reply to
		setReplyTo(EmailAddress.createFrom(msg.getReplyTo()));

		setSubject(msg.getSubject());

		setReceiveDate(parseReceiveDate(msg));
		setSentDate(parseSendDate(msg));

		// copy headers
		Enumeration<Header> headers = msg.getAllHeaders();
		while (headers.hasMoreElements()) {
			Header header = headers.nextElement();
			setHeader(header.getName(), header.getValue());
		}

		// content
		processPart(this, msg);
	}

	/**
	 * Process single part of received message. All parts are simple added to the message, i.e. hierarchy is not saved.
	 */
	protected void processPart(ReceivedEmail email, Part part) throws IOException, MessagingException {
		Object content = part.getContent();

		if (content instanceof String) {
			String stringContent = (String) content;

			String disposition = part.getDisposition();
			if (disposition != null && disposition.equalsIgnoreCase(Part.ATTACHMENT)) {
				String contentType = part.getContentType();

				String mimeType = EmailUtil.extractMimeType(contentType);
				String encoding = EmailUtil.extractEncoding(contentType);
				String fileName = part.getFileName();
				String contentId = parseContentId(part);
				boolean inline = parseInline(part);

				if (encoding == null) {
					encoding = StringPool.US_ASCII;
				}

				email.addAttachment(fileName, mimeType, contentId, inline, stringContent.getBytes(encoding));
			} else {
				String contentType = part.getContentType();
				String encoding = EmailUtil.extractEncoding(contentType);
				String mimeType = EmailUtil.extractMimeType(contentType);

				if (encoding == null) {
					encoding = StringPool.US_ASCII;
				}

				email.addMessage(stringContent, mimeType, encoding);
			}
		}
		else if (content instanceof Multipart) {
			Multipart mp = (Multipart) content;
			int count = mp.getCount();
			for (int i = 0; i < count; i++) {
				Part innerPart = mp.getBodyPart(i);
				processPart(email, innerPart);
			}
		}
		else if (content instanceof InputStream) {
			String fileName = EmailUtil.resolveFileName(part);
			String contentId = parseContentId(part);
			boolean inline = parseInline(part);
			String mimeType = EmailUtil.extractMimeType(part.getContentType());

			InputStream is = (InputStream) content;
			FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream();
			StreamUtil.copy(is, fbaos);

			email.addAttachment(fileName, mimeType, contentId, inline, fbaos.toByteArray());
		}
		else if (content instanceof MimeMessage) {
			MimeMessage mimeMessage = (MimeMessage) content;

			addAttachmentMessage(new ReceivedEmail(mimeMessage));
		}
		else {
			String fileName = part.getFileName();
			String contentId = parseContentId(part);
			boolean inline = parseInline(part);
			String mimeType = EmailUtil.extractMimeType(part.getContentType());

			InputStream is = part.getInputStream();
			FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream();
			StreamUtil.copy(is, fbaos);
			StreamUtil.close(is);

			email.addAttachment(fileName, mimeType, contentId, inline, fbaos.toByteArray());
		}
	}

	protected String parseContentId(Part part) throws MessagingException {
		return (part instanceof MimePart) ? ((MimePart) part).getContentID() : null;
	}

	protected boolean parseInline(Part part) throws MessagingException {
		if (part instanceof MimePart) {
			String dispositionId = part.getDisposition();
			if (dispositionId != null && dispositionId.equalsIgnoreCase("inline")) {
				return true;
			}
		}
		return false;
	}

	protected Date parseReceiveDate(Message msg) throws MessagingException {
		return msg.getReceivedDate();
	}

	protected Date parseSendDate(Message msg) throws MessagingException {
		return msg.getSentDate();
	}

	// ---------------------------------------------------------------- flags

	protected Flags flags;

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

	// ---------------------------------------------------------------- additional properties

	protected int messageNumber;
	protected Date recvDate;

	/**
	 * Returns message number.
	 */
	public int getMessageNumber() {
		return messageNumber;
	}

	/**
	 * Sets message number.
	 */
	public void setMessageNumber(int messageNumber) {
		this.messageNumber = messageNumber;
	}

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
	public void addAttachment(String filename, String mimeType, String contentId, boolean inline, byte[] content) {
		if (attachments == null) {
			attachments = new ArrayList<>();
		}
		EmailAttachment emailAttachment = new ByteArrayAttachment(content, mimeType, filename, contentId, inline);
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


	// ---------------------------------------------------------------- inner messages

	protected List<ReceivedEmail> attachedMessages;

	/**
	 * Adds attached messages.
	 */
	public void addAttachmentMessage(ReceivedEmail receivedEmail) {
		if (attachedMessages == null) {
			attachedMessages = new ArrayList<>();
		}
		attachedMessages.add(receivedEmail);
	}

	/**
	 * Returns the list of attached messages.
	 * If not attached message is available, returns <code>null</code>.
	 */
	public List<ReceivedEmail> getAttachedMessages() {
		return attachedMessages;
	}

}