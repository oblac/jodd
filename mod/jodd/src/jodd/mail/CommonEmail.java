// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Common stuff for both {@link Email} and {@link jodd.mail.ReceivedEmail}
 */
public abstract class CommonEmail {

	public static final String X_PRIORITY = "X-Priority";

	public static final int PRIORITY_HIGHEST 	= 1;
	public static final int PRIORITY_HIGH 		= 2;
	public static final int PRIORITY_NORMAL 	= 3;
	public static final int PRIORITY_LOW 		= 4;
	public static final int PRIORITY_LOWEST		= 5;

	protected static final String[] NONE = new String[0];

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

	// ---------------------------------------------------------------- to

	protected String[] to = NONE;

	/**
	 * Sets TO addresses.
	 */
	public void setTo(String... tos) {
		if (tos == null) {
			tos = NONE;
		}
		to = tos;
	}

	/**
	 * Returns TO addresses.
	 */
	public String[] getTo() {
		return to;
	}

	// ---------------------------------------------------------------- reply-to

	protected String[] replyTo = NONE;

	/**
	 * Sets REPLY-TO addresses.
	 */
	public void setReplyTo(String... replyTo) {
		if (replyTo == null) {
			replyTo = NONE;
		}
		this.replyTo = replyTo;
	}

	/**
	 * Returns REPLY-TO addresses.
	 */
	public String[] getReplyTo() {
		return replyTo;
	}

	// ---------------------------------------------------------------- cc

	protected String[] cc = NONE;

	/**
	 * Sets CC addresses.
	 */
	public void setCc(String... ccs) {
		if (ccs == null) {
			ccs = NONE;
		}
		cc = ccs;
	}

	/**
	 * Returns CC addresses.
	 */
	public String[] getCc() {
		return cc;
	}

	// ---------------------------------------------------------------- bcc

	protected String[] bcc = NONE;

	/**
	 * Sets BCC addresses.
	 */
	public void setBcc(String... bccs) {
		if (bccs == null) {
			bccs = NONE;
		}
		bcc = bccs;
	}

	/**
	 * Returns BCC addresses.
	 */
	public String[] getBcc() {
		return bcc;
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

	// ---------------------------------------------------------------- message

	protected LinkedList<EmailMessage> messages = new LinkedList<EmailMessage>();

	/**
	 * Returns all messages.
	 */
	public LinkedList<EmailMessage> getAllMessages() {
		return messages;
	}

	public void addMessage(EmailMessage emailMessage) {
		messages.add(emailMessage);
	}
	public void addMessage(String text, String mimeType, String encoding) {
		messages.add(new EmailMessage(text, mimeType, encoding));
	}
	public void addMessage(String text, String mimeType) {
		messages.add(new EmailMessage(text, mimeType));
	}

	// ---------------------------------------------------------------- headers

	protected Map<String, String> headers;

	/**
	 * Returns all headers as a <code>HashMap</code>.
	 */
	protected Map<String, String> getAllHeaders() {
		return headers;
	}

	/**
	 * Sets a new header value.
	 */
	public void setHeader(String name, String value) {
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		headers.put(name, value);
	}

	public String getHeader(String name) {
		if (headers == null) {
			return null;
		}
		return headers.get(name);
	}

	
	/**
	 * Sets email priority.
	 * Values of 1 through 5 are acceptable, with 1 being the highest priority, 3 = normal
	 * and 5 = lowest priority.
	 */
	public void setPriority(int priority) {
		setHeader(X_PRIORITY, String.valueOf(priority));
	}

	/**
	 * Returns emails priority (1 - 5) or <code>-1</code> if priority not available.
	 * @see #setPriority(int)
	 */
	public int getPriority() {
		if (headers == null) {
			return -1;
		}
		try {
			return Integer.parseInt(headers.get(X_PRIORITY));
		} catch (NumberFormatException ignore) {
			return -1;
		}
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
	 * Returns e-mails sent date. If return value is <code>null</code> then date
	 * will be set during the process of sending.
	 *
	 * @return email's sent date or null if it will be set later.
	 */
	public Date getSentDate() {
		return sentDate;
	}

}
