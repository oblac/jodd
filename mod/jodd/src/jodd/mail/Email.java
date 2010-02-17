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
	 * @see #addAttachment(String, javax.activation.DataHandler, String) 
	 */
	public Email addAttachment(String fileName, DataHandler dataHandler) {
		addAttachment(fileName, dataHandler, null);
		return this;
	}

	public Email embedAttachment(String fileName, DataHandler dataHandler, String contentId) {
		addAttachment(fileName, dataHandler, contentId);
		return this;
	}

	/**
	 * Adds generic attachment.
	 * @param fileName 		file name of attachment
	 * @param dataHandler	DataHandler
	 * @param contentId		optional content id for embedded attachments (inline)
	 */
	public Email addAttachment(String fileName, DataHandler dataHandler, String contentId) {
		if (attachments == null) {
			attachments = new LinkedList<MimeBodyPart>();
		}
		MimeBodyPart attBodyPart = new MimeBodyPart();
		try {
			attBodyPart.setFileName(fileName);
			attBodyPart.setDataHandler(dataHandler);
			if (contentId != null) {
				attBodyPart.setContentID(contentId);
				attBodyPart.setDisposition("inline");
			}
		} catch (MessagingException mex) {
			throw new MailException("Unable to prepare attachment: '" + fileName + "'.");
		}
		attachments.add(attBodyPart);
		return this;
	}


	/**
	 * Adds a HTML text as an attachment.
	 * @param fileName attachment file name
	 * @param data     HTML data
	 */
	public Email attachHtml(String fileName, String data) {
		addAttachment(fileName, new DataHandler(new ByteArrayDataSource(data, MimeTypes.MIME_TEXT_HTML)));
		return this;
	}

	/**
	 * Adds an existing file as attachment.
	 */
	public Email attachFile(String fileName) {
		FileDataSource fileDataSource = new FileDataSource(fileName);
		addAttachment(fileDataSource.getName(), new DataHandler(fileDataSource));
		return this;
	}
	public Email attachFile(File file) {
		FileDataSource fileDataSource = new FileDataSource(file);
		addAttachment(fileDataSource.getName(), new DataHandler(fileDataSource));
		return this;
	}
	public Email embedFile(String fileName, String contentId) {
		FileDataSource fileDataSource = new FileDataSource(fileName);
		addAttachment(fileDataSource.getName(), new DataHandler(fileDataSource), contentId);
		return this;
	}
	public Email embedFile(File file, String contentId) {
		FileDataSource fileDataSource = new FileDataSource(file);
		addAttachment(fileDataSource.getName(), new DataHandler(fileDataSource), contentId);
		return this;
	}
	public Email embedFile(String fileName) {
		FileDataSource fileDataSource = new FileDataSource(fileName);
		String name = fileDataSource.getName();
		addAttachment(name, new DataHandler(fileDataSource), name);
		return this;
	}
	public Email embedFile(File file) {
		FileDataSource fileDataSource = new FileDataSource(file);
		String name = fileDataSource.getName();
		addAttachment(name, new DataHandler(fileDataSource), name);
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
