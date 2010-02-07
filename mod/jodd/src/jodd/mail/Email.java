// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.util.MimeTypes;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
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
	public void addText(String text) {
		messages.add(new EmailMessage(text));
	}
	public void addText(String text, String encoding) {
		messages.add(new EmailMessage(text, MimeTypes.MIME_TEXT_PLAIN, encoding));
	}
	public Email text(String text) {
		addText(text);
		return this;
	}


	/**
	 * Adds HTML message.
	 */
	public void addHtml(String message) {
		messages.add(new EmailMessage(message, MimeTypes.MIME_TEXT_HTML));
	}
	public void addHtml(String message, String encoding) {
		messages.add(new EmailMessage(message, MimeTypes.MIME_TEXT_HTML, encoding));
	}
	public Email html(String message) {
		addHtml(message);
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

	protected LinkedList<MimeBodyPart> attachments;

	/**
	 * Returns an array of attachments as body parts.
	 */
	public MimeBodyPart[] getAttachments() {
		if (attachments == null) {
			return null;
		}
		return attachments.toArray(new MimeBodyPart[attachments.size()]);
	}

	/**
	 * Adds a generic attachment.
	 * @param fileName file name of attachment
	 * @param dataHandler       DataHandler
	 */
	public void addAttachment(String fileName, DataHandler dataHandler) {
		if (attachments == null) {
			attachments = new LinkedList<MimeBodyPart>();
		}
		MimeBodyPart attBodyPart = new MimeBodyPart();
		try {
			attBodyPart.setFileName(fileName);
			attBodyPart.setDataHandler(dataHandler);
		} catch (MessagingException mex) {
			throw new EmailException("Unable to prepare attachment: '" + fileName + "'.");
		}
		attachments.add(attBodyPart);
	}
	public Email attach(String fileName, DataHandler dataHandler) {
		addAttachment(fileName, dataHandler);
		return this;
	}

	/**
	 * Adds a HTML text as an attachment.
	 * @param fileName attachment file name
	 * @param data     HTML data
	 */
	public void addHtmlAttachment(String fileName, String data) {
		addAttachment(fileName, new DataHandler(new ByteArrayDataSource(data, MimeTypes.MIME_TEXT_HTML)));
	}
	public Email attachHtml(String fileName, String data) {
		addHtmlAttachment(fileName, data);
		return this;
	}

	/**
	 * Adds an existing file as attachment.
	 */
	public void addFileAttachment(String fileName) {
		FileDataSource fileDataSource = new FileDataSource(fileName);
		addAttachment(fileDataSource.getName(), new DataHandler(fileDataSource));
	}
	public void addFileAttachment(File file) {
		FileDataSource fileDataSource = new FileDataSource(file);
		addAttachment(fileDataSource.getName(), new DataHandler(fileDataSource));
	}

	public Email attachFile(String fileName) {
		addFileAttachment(fileName);
		return this;
	}
	public Email attachFile(File file) {
		addFileAttachment(file);
		return this;
	}


	// ---------------------------------------------------------------- headers

	public Email header(String name, String value) {
		setHeader(name, value);
		return this;
	}

	public Email priority(int priority) {
		setPriority(priority);
		return this;
	}

	// ---------------------------------------------------------------- date

	/**
	 * Sets current date as e-mails sent date.
	 */
	public void setCurrentSentDate() {
		sentDate = new Date();
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
