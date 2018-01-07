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

import jodd.util.ArraysUtil;
import jodd.util.net.MimeTypes;

import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Header;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common stuff for both {@link Email} and {@link ReceivedEmail}.
 */
public abstract class CommonEmail<T extends CommonEmail<T>> {

	abstract T getThis();

	public static final String X_PRIORITY = "X-Priority";

	public static final int PRIORITY_HIGHEST = 1;
	public static final int PRIORITY_HIGH = 2;
	public static final int PRIORITY_NORMAL = 3;
	public static final int PRIORITY_LOW = 4;
	public static final int PRIORITY_LOWEST = 5;

	/**
	 * Clones the object with all its necessary data.
	 *
	 * @return new object of type T
	 */
	@Override
	public abstract T clone();

	// ---------------------------------------------------------------- from

	/**
	 * FROM address.
	 */
	private EmailAddress from;

	/**
	 * Sets the FROM address.
	 *
	 * @param from {@link EmailAddress}.
	 * @return this
	 */
	public T setFrom(final EmailAddress from) {
		this.from = from;
		return getThis();
	}

	/**
	 * Sets the FROM address from {@link Address}.
	 *
	 * @param from {@link Address}
	 * @return this
	 * @see #setFrom(EmailAddress)
	 */
	public T setFrom(final Address from) {
		return setFrom(new EmailAddress(from));
	}

	/**
	 * Sets the FROM address.
	 *
	 * @param from Address may be specified with personal name like this: {@code Jenny Doe <email@foo.com>}.
	 * @return this
	 * @see #setFrom(EmailAddress)
	 */
	public T setFrom(final String from) {
		return setFrom(new EmailAddress(from));
	}

	/**
	 * Sets the FROM address by providing personal name and address.
	 *
	 * @param personalName personal name.
	 * @param from         email address.
	 * @return this
	 * @see #setFrom(EmailAddress)
	 */
	public T setFrom(final String personalName, final String from) {
		return setFrom(new EmailAddress(personalName, from));
	}

	/**
	 * Returns FROM {@link EmailAddress}.
	 */
	public EmailAddress getFrom() {
		return from;
	}

	// ---------------------------------------------------------------- to

	/**
	 * TO address.
	 */
	private EmailAddress[] to = EmailAddress.EMPTY_ARRAY;

	/**
	 * Appends TO address.
	 *
	 * @param to {@link EmailAddress} to add.
	 * @return this
	 */
	public T addTo(final EmailAddress to) {
		this.to = ArraysUtil.append(this.to, to);
		return getThis();
	}

	/**
	 * Appends TO address.
	 *
	 * @param to Address may be specified with personal name like this: {@code Jenny Doe <email@foo.com>}.
	 * @return this
	 * @see #addTo(EmailAddress)
	 */
	public T addTo(final String to) {
		return addTo(new EmailAddress(to));
	}

	/**
	 * Appends TO address by personal name and email address.
	 *
	 * @param personalName personal name.
	 * @param to           email address.
	 * @return this
	 * @see #addTo(EmailAddress)
	 */
	public T addTo(final String personalName, final String to) {
		return addTo(new EmailAddress(personalName, to));
	}

	/**
	 * Appends TO address from {@code Address}.
	 *
	 * @param to {@link Address} to add.
	 * @return this
	 * @see #addTo(EmailAddress)
	 */
	public T addTo(final Address to) {
		return addTo(new EmailAddress(to));
	}

	/**
	 * Sets one or more TO address.
	 *
	 * @param tos Address may be specified with personal name like this: {@code Jenny Doe <email@foo.com>}.
	 * @return this
	 * @see #setTo(EmailAddress...)
	 */
	public T setTo(final String[] tos) {
		return setTo(EmailAddress.createFrom(tos));
	}

	/**
	 * Sets one or more TO addresses.
	 *
	 * @param tos array of {@link Address}s to set.
	 * @return this
	 * @see #setTo(EmailAddress...)
	 */
	public T setTo(final Address[] tos) {
		return setTo(EmailAddress.createFrom(tos));
	}

	/**
	 * Sets TO addresses.
	 *
	 * @param tos vararg of {@link EmailAddress}es to set.
	 * @return this
	 */
	public T setTo(final EmailAddress... tos) {
		this.to = getValueOrEmptyArray(tos);
		return getThis();
	}

	/**
	 * Returns TO addresses.
	 */
	public EmailAddress[] getTo() {
		return to;
	}

	// ---------------------------------------------------------------- reply-to

	private EmailAddress[] replyTo = EmailAddress.EMPTY_ARRAY;

	/**
	 * Appends REPLY-TO address.
	 *
	 * @param to {@link EmailAddress} to add.
	 * @return this
	 */
	public T addReplyTo(final EmailAddress to) {
		this.replyTo = ArraysUtil.append(this.replyTo, to);
		return getThis();
	}

	/**
	 * Appends REPLY-TO address.
	 *
	 * @param replyTo Address may be specified with personal name like this: {@code Jenny Doe <email@foo.com>}.
	 * @return this
	 * @see #addReplyTo(EmailAddress)
	 */
	public T addReplyTo(final String replyTo) {
		return addReplyTo(new EmailAddress(replyTo));
	}

	/**
	 * Appends REPLY-TO address.
	 *
	 * @param personalName personal name.
	 * @param replyTo      email address.
	 * @return this
	 * @see #addReplyTo(EmailAddress)
	 */
	public T addReplyTo(final String personalName, final String replyTo) {
		return addReplyTo(new EmailAddress(personalName, replyTo));
	}

	/**
	 * Appends REPLY-TO address.
	 *
	 * @param replyTo {@link Address} to add.
	 * @return this
	 * @see #addReplyTo(EmailAddress)
	 */
	public T addReplyTo(final Address replyTo) {
		return addReplyTo(new EmailAddress(replyTo));
	}

	/**
	 * Sets one or more REPLY-TO address.
	 *
	 * @param replyTos array of {@link EmailAddress}es to set.
	 * @return this
	 * @see #setReplyTo(EmailAddress...)
	 */
	public T setReplyTo(final String[] replyTos) {
		return setReplyTo(EmailAddress.createFrom(replyTos));
	}

	/**
	 * Sets one or more REPLY-TO address.
	 *
	 * @param replyTos array of {@link Address}es to set.
	 * @return this
	 * @see #setReplyTo(EmailAddress...)
	 */
	public T setReplyTo(final Address[] replyTos) {
		return setReplyTo(EmailAddress.createFrom(replyTos));
	}

	/**
	 * Sets REPLY-TO addresses.
	 *
	 * @param replyTo vararg of {@link EmailAddress}es to set.
	 * @return this
	 */
	public T setReplyTo(final EmailAddress... replyTo) {
		this.replyTo = getValueOrEmptyArray(replyTo);
		return getThis();
	}

	/**
	 * Returns REPLY-TO addresses.
	 */
	public EmailAddress[] getReplyTo() {
		return replyTo;
	}

	// ---------------------------------------------------------------- cc

	/**
	 * CC address.
	 */
	private EmailAddress[] cc = EmailAddress.EMPTY_ARRAY;

	/**
	 * Appends CC address.
	 *
	 * @param to {@link EmailAddress} to add.
	 * @return this
	 */
	public T addCc(final EmailAddress to) {
		this.cc = ArraysUtil.append(this.cc, to);
		return getThis();
	}

	/**
	 * Appends CC address.
	 *
	 * @param cc Address may be specified with personal name like this: {@code Jenny Doe <email@foo.com>}.
	 * @return this
	 * @see #addCc(EmailAddress)
	 */
	public T addCc(final String cc) {
		return addCc(new EmailAddress(cc));
	}

	/**
	 * Appends CC address.
	 *
	 * @param personalName personal name.
	 * @param cc           email address.
	 * @return this
	 * @see #addCc(EmailAddress)
	 */
	public T addCc(final String personalName, final String cc) {
		return addCc(new EmailAddress(personalName, cc));
	}

	/**
	 * Appends CC address.
	 *
	 * @param cc {@link Address} to add.
	 * @return this
	 * @see #addCc(EmailAddress)
	 */
	public T addCc(final Address cc) {
		return addCc(new EmailAddress(cc));
	}

	/**
	 * Sets one or more CC address.
	 *
	 * @param ccs array of {@link String}s to set.
	 * @return this
	 * @see #setCc(EmailAddress...)
	 */
	public T setCc(final String... ccs) {
		return setCc(EmailAddress.createFrom(ccs));
	}

	/**
	 * Sets one or more CC address.
	 *
	 * @param ccs array of {@link Address}s to set.
	 * @return this
	 * @see #setCc(EmailAddress...)
	 */
	public T setCc(final Address... ccs) {
		return setCc(EmailAddress.createFrom(ccs));
	}

	/**
	 * Sets CC addresses.
	 *
	 * @param ccs vararg of {@link EmailAddress}es to set.
	 * @return this
	 */
	public T setCc(final EmailAddress... ccs) {
		this.cc = getValueOrEmptyArray(ccs);
		return getThis();
	}

	/**
	 * Returns CC addresses.
	 */
	public EmailAddress[] getCc() {
		return cc;
	}

	// ---------------------------------------------------------------- subject

	/**
	 * Message subject.
	 */
	private String subject;

	/**
	 * Message subject encoding.
	 */
	private String subjectEncoding;

	/**
	 * Sets message subject.
	 *
	 * @param subject The message subject to set.
	 * @return this
	 */
	public T setSubject(final String subject) {
		this.subject = subject;
		return getThis();
	}

	/**
	 * Sets message subject with specified encoding to override default platform encoding.
	 * If the subject contains non US-ASCII characters, it will be encoded using the specified charset.
	 * If the subject contains only US-ASCII characters, no encoding is done and it is used as-is.
	 * The application must ensure that the subject does not contain any line breaks.
	 * See {@link javax.mail.internet.MimeMessage#setSubject(String, String)}.
	 *
	 * @param subject  The message subject
	 * @param encoding The encoding for the message subject.
	 * @return this
	 */
	public T setSubject(final String subject, final String encoding) {
		setSubject(subject);
		this.subjectEncoding = encoding;
		return getThis();
	}

	/**
	 * Returns message subject.
	 *
	 * @return message subject.
	 */
	public String getSubject() {
		return this.subject;
	}

	/**
	 * Returns the message subject encoding.
	 *
	 * @return the message subject encoding.
	 */
	public String getSubjectEncoding() {
		return this.subjectEncoding;
	}

	// ---------------------------------------------------------------- message

	/**
	 * All messages.
	 */
	private final List<EmailMessage> messages = new ArrayList<>();

	/**
	 * Returns all messages.
	 */
	public List<EmailMessage> getAllMessages() {
		return messages;
	}

	/**
	 * Adds multiple messages.
	 *
	 * @param msgsToAdd {@link List} of {@link EmailMessage}s to add.
	 * @return this
	 */
	public T addMessages(final List<EmailMessage> msgsToAdd) {
		messages.addAll(msgsToAdd);
		return getThis();
	}

	/**
	 * Adds an {@link EmailMessage}.
	 *
	 * @param msgToAdd {@link EmailMessage} to add.
	 * @return this
	 */
	public T addMessage(final EmailMessage msgToAdd) {
		messages.add(msgToAdd);
		return getThis();
	}

	/**
	 * Adds a {@link EmailMessage}.
	 *
	 * @param text     The text to add as a {@link String}.
	 * @param mimeType The MIME type as a {@link String}.
	 * @param encoding The encoding as a {@link String}.
	 * @return this
	 * @see #addMessage(EmailMessage)
	 */
	public T addMessage(final String text, final String mimeType, final String encoding) {
		return addMessage(new EmailMessage(text, mimeType, encoding));
	}

	/**
	 * Adds a {@link EmailMessage}.
	 *
	 * @param text     The text to add as a {@link String}.
	 * @param mimeType The MIME type as a {@link String}.
	 * @return this
	 * @see #addMessage(EmailMessage)
	 */
	public T addMessage(final String text, final String mimeType) {
		return addMessage(new EmailMessage(text, mimeType));
	}

	/**
	 * Adds plain message text.
	 *
	 * @param text The text to add as a {@link String}.
	 * @return this
	 * @see #addMessage(String, String)
	 */
	public T addText(final String text) {
		return addMessage(text, MimeTypes.MIME_TEXT_PLAIN);
	}

	/**
	 * Adds plain message text.
	 *
	 * @param text     The text to add as a {@link String}.
	 * @param encoding The encoding as a {@link String}.
	 * @return this
	 * @see #addMessage(EmailMessage)
	 */
	public T addText(final String text, final String encoding) {
		return addMessage(new EmailMessage(text, MimeTypes.MIME_TEXT_PLAIN, encoding));
	}

	/**
	 * Adds HTML message.
	 *
	 * @param html The HTML to add as a {@link String}.
	 * @return this
	 * @see #addMessage(EmailMessage)
	 */
	public T addHtml(final String html) {
		return addMessage(new EmailMessage(html, MimeTypes.MIME_TEXT_HTML));
	}

	/**
	 * Adds HTML message.
	 *
	 * @param html     The HTML to add as a {@link String}.
	 * @param encoding The encoding as a {@link String}.
	 * @return this
	 * @see #addMessage(EmailMessage)
	 */
	public T addHtml(final String html, final String encoding) {
		return addMessage(new EmailMessage(html, MimeTypes.MIME_TEXT_HTML, encoding));
	}

	// ---------------------------------------------------------------- headers

	/**
	 * All headers.
	 */
	private final Map<String, String> headers = new HashMap<>();

	/**
	 * Returns all headers as a {@link HashMap}.
	 *
	 * @return all headers in a {@link HashMap}
	 */
	protected Map<String, String> getAllHeaders() {
		return headers;
	}

	/**
	 * Sets header value.
	 *
	 * @param name  The name of the header.
	 * @param value The value of the header.
	 * @return this
	 */
	public T setHeader(final String name, final String value) {
		headers.put(name, value);
		return getThis();
	}

	/**
	 * Sets headers.
	 *
	 * @param headersToSet Headers to set.
	 * @return this
	 */
	public T setHeaders(final Map<String, String> headersToSet) {
		headers.putAll(headersToSet);
		return getThis();
	}


	/**
	 * Sets headers.
	 *
	 * @param headersToSet Headers to set.
	 * @return this
	 * @see #setHeader(String, String)
	 */
	public T setHeaders(final Enumeration<Header> headersToSet) {
		while (headersToSet.hasMoreElements()) {
			final Header header = headersToSet.nextElement();
			setHeader(header.getName(), header.getValue());
		}
		return getThis();
	}

	/**
	 * Returns the value of a header.
	 *
	 * @param name The name of the header.
	 * @return The value of the header.
	 */
	public String getHeader(final String name) {
		return headers.get(name);
	}

	/**
	 * Sets email priority.
	 *
	 * @param priority - Values of 1 through 5 are acceptable, with 1 being the highest priority, 3 = normal
	 *                 and 5 = lowest priority.
	 */
	public T setPriority(final int priority) {
		setHeader(X_PRIORITY, String.valueOf(priority));
		return getThis();
	}

	/**
	 * Returns emails priority (1 - 5) or <code>-1</code> if priority not available.
	 *
	 * @see #setPriority(int)
	 */
	public int getPriority() {
		try {
			return Integer.parseInt(headers.get(X_PRIORITY));
		} catch (final NumberFormatException ignore) {
			return -1;
		}
	}

	// ---------------------------------------------------------------- attachments

	private final List<EmailAttachment<? extends DataSource>> attachments = new ArrayList<>();

	/**
	 * Returns the list of all {@link EmailAttachment}s.
	 *
	 * @return List of {@link EmailAttachment}s. Returns empty list if no attachment is available.
	 */
	public List<EmailAttachment<? extends DataSource>> getAttachments() {
		return attachments;
	}

	/**
	 * Adds {@link EmailAttachment}s.
	 *
	 * @param attachments {@link List} of {@link EmailAttachment}s to add.
	 * @return this
	 */
	T storeAttachments(final List<EmailAttachment<? extends DataSource>> attachments) {
		this.attachments.addAll(attachments);
		return getThis();
	}

	/**
	 * Adds {@link EmailAttachment}.
	 *
	 * @param attachment {@link EmailAttachment} to add.
	 * @return this
	 */
	T storeAttachment(final EmailAttachment<? extends DataSource> attachment) {
		this.attachments.add(attachment);
		return getThis();
	}

	/**
	 * Adds {@link EmailAttachment}s.
	 *
	 * @param attachments {@link List} of {@link EmailAttachment}s to add.
	 * @return this
	 */
	public T addAttachments(final List<EmailAttachment<? extends DataSource>> attachments) {
		for (final EmailAttachment<?> attachment : attachments) {
			addAttachment(attachment);
		}
		return getThis();
	}

	/**
	 * Adds {@link EmailAttachment}. Content ID will be set to {@code null}.
	 *
	 * @param attachment {@link EmailAttachment} to add.
	 * @return this
	 */
	public T addAttachment(final EmailAttachment<? extends DataSource> attachment) {
		attachment.setContentId(null);
		return storeAttachment(attachment);
	}

	/**
	 * @see #addAttachment(EmailAttachment)
	 */
	public T addAttachment(final EmailAttachmentBuilder builder) {
		return addAttachment(builder.buildByteArrayDataSource());
	}

	/**
	 * Attaches the embedded attachment: Content ID will be set if missing from attachment's file name.
	 *
	 * @param builder {@link EmailAttachmentBuilder}
	 * @return this
	 * @see #embedAttachment(EmailAttachment)
	 */
	public T embedAttachment(final EmailAttachmentBuilder builder) {
		builder.setContentIdFromNameIfMissing();

		//TODO: is this really supposed to always be inline?
		// https://github.com/oblac/jodd/issues/546
		//content disposition will be set to {@code inline}
		//builder.setInline(true);

		return embedAttachment(builder.buildByteArrayDataSource());
	}

	/**
	 * Embed {@link EmailAttachment} to last message. No header is changed.
	 *
	 * @param attachment {@link EmailAttachment}
	 * @return this
	 * @see #storeAttachment(EmailAttachment)
	 */
	public T embedAttachment(final EmailAttachment<? extends DataSource> attachment) {
		storeAttachment(attachment);

		final List<EmailMessage> messages = getAllMessages();
		final int size = messages.size();
		if (size > 1) {
			// Add to last message
			final int lastMessagePos = size - 1;
			final EmailMessage lastMessage = messages.get(lastMessagePos);
			attachment.setEmbeddedMessage(lastMessage);
		}

		return getThis();
	}

	// ---------------------------------------------------------------- date

	/**
	 * Email's sent date.
	 */
	private Date sentDate;

	/**
	 * Sets email's sent date.
	 *
	 * @param date - Email's sent date. If {@code null}, then date will be set during the process of sending.
	 * @return this
	 */
	public T setSentDate(final Date date) {
		sentDate = date;
		return getThis();
	}


	/**
	 * Returns email's sent date. If return value is {@code null}, then date
	 * will be set during the process of sending.
	 *
	 * @return email's sent date or {@code null} if it will be set later.
	 */
	public Date getSentDate() {
		return sentDate;
	}

	// ---------------------------------------------------------------- toString

	@Override
	public String toString() {
		return "Email{'" + getFrom() + "\', subject='" + getSubject() + "\'}";
	}

	// ---------------------------------------------------------------- helper

	EmailAddress[] getValueOrEmptyArray(EmailAddress[] arr) {
		if (arr == null) {
			arr = EmailAddress.EMPTY_ARRAY;
		}
		return arr;
	}
}
