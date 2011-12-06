// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.mail.att.ByteArrayAttachment;
import jodd.mail.att.FileAttachment;
import jodd.util.MimeTypes;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;

/**
 * E-mail holds all parts of an email and handle attachments.
 */
public class Email extends CommonEmail {

	public static Email create() {
		return new Email();
	}

	// ---------------------------------------------------------------- from, to, cc, bcc

	public Email from(String from) {
		setFrom(from);
		return this;
	}

	public Email to(String to) {
		setTo(to);
		return this;
	}
	public Email to(String... tos) {
		setTo(tos);
		return this;
	}

	public Email replyTo(String replyTo) {
		setReplyTo(replyTo);
		return this;
	}
	public Email replyTo(String... replyTos) {
		setTo(replyTos);
		return this;
	}

	public Email cc(String cc) {
		setCc(cc);
		return this;
	}
	public Email cc(String... ccs) {
		setCc(ccs);
		return this;
	}

	public Email bcc(String bcc) {
		setBcc(bcc);
		return this;
	}
	public Email bcc(String... bccs) {
		setBcc(bccs);
		return this;
	}

	// ---------------------------------------------------------------- subject

	public Email subject(String subject) {
		setSubject(subject);
		return this;
	}

	// ---------------------------------------------------------------- message

	/**
	 * Adds plain message text.
	 */
	public Email addText(String text) {
		messages.add(new EmailMessage(text));
		return this;
	}
	public Email addText(String text, String encoding) {
		messages.add(new EmailMessage(text, MimeTypes.MIME_TEXT_PLAIN, encoding));
		return this;
	}

	/**
	 * Adds HTML message.
	 */
	public Email addHtml(String message) {
		messages.add(new EmailMessage(message, MimeTypes.MIME_TEXT_HTML));
		return this;
	}
	public Email addHtml(String message, String encoding) {
		messages.add(new EmailMessage(message, MimeTypes.MIME_TEXT_HTML, encoding));
		return this;
	}


	public Email message(String text, String mimeType, String encoding) {
		addMessage(text, mimeType, encoding);
		return this;
	}
	public Email message(String text, String mimeType) {
		addMessage(text, mimeType);
		return this;
	}


	// ---------------------------------------------------------------- attachments

	protected LinkedList<EmailAttachment> attachments;

	/**
	 * Returns an array of attachments or <code>null</code> if no attachment enclosed with this email. 
	 */
	public LinkedList<EmailAttachment> getAttachments() {
		if (attachments == null) {
			return null;
		}
		return attachments;
	}

	/**
	 * Adds generic attachment.
	 */
	public Email attach(EmailAttachment emailAttachment) {
		if (attachments == null) {
			attachments = new LinkedList<EmailAttachment>();
		}
		attachments.add(emailAttachment);
		return this;
	}

	/**
	 * Attach bytes.
	 */
	public Email attachBytes(byte[] bytes, String contentType, String name) {
		attach(new ByteArrayAttachment(bytes, contentType, name));
		return this;
	}

	/**
	 * Adds an existing file as attachment.
	 */
	public Email attachFile(String fileName) {
		attach(new FileAttachment(new File(fileName)));
		return this;
	}
	public Email attachFile(File file) {
		attach(new FileAttachment(file));
		return this;
	}
	public Email embedFile(String fileName, String contentId) {
		File f = new File(fileName);
		attach(new FileAttachment(f, f.getName(), contentId));
		return this;
	}
	public Email embedFile(File file, String contentId) {
		attach(new FileAttachment(file, file.getName(), contentId));
		return this;
	}
	public Email embedFile(String fileName) {
		attach(new FileAttachment(new File(fileName), true));
		return this;
	}
	public Email embedFile(File file) {
		attach(new FileAttachment(file, true));
		return this;
	}


	// ---------------------------------------------------------------- headers

	public Email header(String name, String value) {
		setHeader(name, value);
		return this;
	}

	public Email priority(int priority) {
		super.setPriority(priority);
		return this;
	}

	// ---------------------------------------------------------------- date

	/**
	 * Sets current date as e-mails sent date.
	 */
	public Email setCurrentSentDate() {
		sentDate = new Date();
		return this;
	}
	
	public Email sentOn(Date date) {
		setSentDate(date);
		return this;
	}

	// ---------------------------------------------------------------- toString

	@Override
	public String toString() {
		return "Email{'" + from + "\', subject='" + subject + "\'}";
	}
}
