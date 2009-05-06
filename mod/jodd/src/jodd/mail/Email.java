// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.util.StringPool;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.io.File;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

/**
 * E-mail holds all parts of an email and handle attachments.
 */
public class Email {

	public static final String X_PRIORITY = "X-Priority";
	public static final int PRIORITY_HIGHEST 	= 1;
	public static final int PRIORITY_HIGH 		= 2;
	public static final int PRIORITY_NORMAL 	= 3;
	public static final int PRIORITY_LOW 		= 4;
	public static final int PRIORITY_LOWEST		= 5;

	public static Email create() {
		return new Email();
	}

	// ---------------------------------------------------------------- from

	protected String from;

	/**
	 * Sets the FROM address.
	 */
	public void setFrom(String from) {
		this.from = from;
	}
	/**
	 * Returns FROM address.
	 */
	public String getFrom() {
		return from;
	}
	public Email from(String from) {
		setFrom(from);
		return this;
	}

	// ---------------------------------------------------------------- to

	protected LinkedList<String> toList;
	/**
	 * Adds TO address.
	 */
	public void setTo(String to) {
		if (toList == null) {
			toList = new LinkedList<String>();
		}
		toList.add(to.trim());
	}
	/**
	 * Adds several TO addresses at once.
	 */
	public void setTo(String... tos) {
		for (String to : tos) {
			setTo(to);
		}
	}
	/**
	 * Returns array of TO addresses.
	 */
	public String[] getTo() {
		if (toList == null) {
			return new String[0];
		}
		return toList.toArray(new String[toList.size()]);
	}
	public Email to(String to) {
		setTo(to);
		return this;
	}
	public Email to(String... tos) {
		setTo(tos);
		return this;
	}


	// ---------------------------------------------------------------- cc


	protected LinkedList<String> ccList;
	/**
	 * Adds CC address.
	 */
	public void setCc(String cc) {
		if (ccList == null) {
			ccList = new LinkedList<String>();
		}
		ccList.add(cc);
	}
	/**
	 * Adds multiple CC addresses.
	 */
	public void setCc(String... ccs) {
		for (String cc : ccs) {
			setCc(cc);
		}
	}
	/**
	 * Returns array of CC addresses.
	 */
	public String[] getCc() {
		if (ccList == null) {
			return new String[0];
		}
		return ccList.toArray(new String[ccList.size()]);
	}

	public Email cc(String cc) {
		setCc(cc);
		return this;
	}
	public Email cc(String... ccs) {
		setCc(ccs);
		return this;
	}


	// ---------------------------------------------------------------- bcc

	protected LinkedList<String> bccList;
	/**
	 * Adds BCC address.
	 */
	public void setBcc(String bcc) {
		if (bccList == null) {
			bccList = new LinkedList<String>();
		}
		bccList.add(bcc);
	}
	/**
	 * Adds multiple BCC addresses.
	 */
	public void setBcc(String... bccs) {
		for (String bcc : bccs) {
			setBcc(bcc);
		}
	}
	/**
	 * Returns array of BCC addresses.
	 */
	public String[] getBcc() {
		if (bccList == null) {
			return new String[0];
		}
		return bccList .toArray(new String[bccList.size()]);
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

	protected String subject;
	/**
	 * Sets message subject.
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * Returns message subject.
	 */
	public String getSubject() {
		return this.subject;
	}
	public Email subject(String subject) {
		setSubject(subject);
		return this;
	}

	// ---------------------------------------------------------------- message


	protected String text;
	protected String message;
	protected String encoding = StringPool.UTF_8;

	public String getEncoding() {
		return encoding;
	}
	/**
	 * Sets encoding.
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public Email encoding(String encoding) {
		setEncoding(encoding);
		return this;
	}

	/**
	 * Sets plain message text.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Returns plain message text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets HTML message.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Returns HTML message.
	 */
	public String getMessage() {
		return this.message;
	}

	public Email text(String text) {
		setText(text);
		return this;
	}
	public Email message(String message) {
		setMessage(message);
		return this;
	}


	// ---------------------------------------------------------------- attachments


	protected ArrayList<MimeBodyPart> attachments;

	/**
	 * Returns an array of attachments.
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
			attachments = new ArrayList<MimeBodyPart>();
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
	 * Adds a HTML text attachment.
	 * @param fileName attachment file name
	 * @param data     HTML data
	 */
	public void addHtmlAttachment(String fileName, String data) {
		addAttachment(fileName, new DataHandler(new ByteArrayDataSource(data, "text/html")));
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

	protected Map<String, String> headers;

	/**
	 * Returns all headers as a HashMap
	 */
	protected Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * Adds header.
	 */
	public void setHeader(String name, String value) {
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		headers.put(name, value);
	}

	public Email header(String name, String value) {
		setHeader(name, value);
		return this;
	}

	/**
	 * Sets email priority.
	 * Values of 1 through 5 are acceptable, with 1 being the highest priority, 3 = normal 
	 * and 5 = lowest priority.
	 */
	public void setPriority(int priority) {
		setHeader(X_PRIORITY, String.valueOf(priority));
	}
	public Email priority(int priority) {
		setPriority(priority);
		return this;
	}

	// ---------------------------------------------------------------- date

	protected Date sentDate;

	/**
	 * Sets e-mails sent date. If input parameter is <code>null</code> then date
	 * will be when email is physically sent.
	 */
	public void setSentDate(Date date) {
		sentDate = date;
	}
	/**
	 * Sets current date as e-mails sent date.
	 */
	public void setCurrentSentDate() {
		sentDate = new Date();
	}

	/**
	 * Returns e-mails sent date. If return value is <code>null</code> then date
	 * will be set during the process of sending.
	 *
	 * @return email's sent date or null if it will be set later.
	 */
	public Date getSentDate() {
		return sentDate;
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
