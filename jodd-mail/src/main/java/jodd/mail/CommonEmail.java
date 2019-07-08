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
import jodd.net.MimeTypes;

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

	@SuppressWarnings("unchecked")
	protected T _this() {
		return (T) this;
	}

	public static final String X_PRIORITY = "X-Priority";

	public static final int PRIORITY_HIGHEST = 1;
	public static final int PRIORITY_HIGH = 2;
	public static final int PRIORITY_NORMAL = 3;
	public static final int PRIORITY_LOW = 4;
	public static final int PRIORITY_LOWEST = 5;

	/**
	 * Clones the email with all its necessary data.
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
	public T from(final EmailAddress from) {
		this.from = from;
		return _this();
	}

	/**
	 * Sets the FROM address from {@link Address}.
	 *
	 * @param from {@link Address}
	 * @return this
	 * @see #from(EmailAddress)
	 */
	public T from(final Address from) {
		return from(EmailAddress.of(from));
	}

	/**
	 * Sets the FROM address.
	 *
	 * @param from Address may be specified with personal name like this: {@code Jenny Doe <email@foo.com>}.
	 * @return this
	 * @see #from(EmailAddress)
	 */
	public T from(final String from) {
		return from(EmailAddress.of(from));
	}

	/**
	 * Sets the FROM address by providing personal name and address.
	 *
	 * @param personalName personal name.
	 * @param from         email address.
	 * @return this
	 * @see #from(EmailAddress)
	 */
	public T from(final String personalName, final String from) {
		return from(new EmailAddress(personalName, from));
	}

	/**
	 * Returns FROM {@link EmailAddress}.
	 */
	public EmailAddress from() {
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
	public T to(final EmailAddress to) {
		this.to = ArraysUtil.append(this.to, to);
		return _this();
	}

	/**
	 * Appends TO address.
	 *
	 * @param to Address may be specified with personal name like this: {@code Jenny Doe <email@foo.com>}.
	 * @return this
	 * @see #to(EmailAddress)
	 */
	public T to(final String to) {
		return to(EmailAddress.of(to));
	}

	/**
	 * Appends TO address by personal name and email address.
	 *
	 * @param personalName personal name.
	 * @param to           email address.
	 * @return this
	 * @see #to(EmailAddress)
	 */
	public T to(final String personalName, final String to) {
		return to(new EmailAddress(personalName, to));
	}

	/**
	 * Appends TO address from {@code Address}.
	 *
	 * @param to {@link Address} to add.
	 * @return this
	 * @see #to(EmailAddress)
	 */
	public T to(final Address to) {
		return to(EmailAddress.of(to));
	}

	/**
	 * Appends one or more TO address.
	 *
	 * @param tos Address may be specified with personal name like this: {@code Jenny Doe <email@foo.com>}.
	 * @return this
	 * @see #to(EmailAddress...)
	 */
	public T to(final String... tos) {
		return to(EmailAddress.of(tos));
	}

	/**
	 * Appends one or more TO addresses.
	 *
	 * @param tos array of {@link Address}s to set.
	 * @return this
	 * @see #to(EmailAddress...)
	 */
	public T to(final Address... tos) {
		return to(EmailAddress.of(tos));
	}

	/**
	 * Appends TO addresses.
	 *
	 * @param tos vararg of {@link EmailAddress}es to set.
	 * @return this
	 */
	public T to(final EmailAddress... tos) {
		this.to = valueOrEmptyArray(tos);
		return _this();
	}

	/**
	 * Returns TO addresses.
	 */
	public EmailAddress[] to() {
		return to;
	}

	/**
	 * Resets TO addresses.
	 */
	public T resetTo() {
		this.to = EmailAddress.EMPTY_ARRAY;
		return _this();
	}

	// ---------------------------------------------------------------- reply-to

	private EmailAddress[] replyTo = EmailAddress.EMPTY_ARRAY;

	/**
	 * Appends REPLY-TO address.
	 *
	 * @param replyTo {@link EmailAddress} to add.
	 * @return this
	 */
	public T replyTo(final EmailAddress replyTo) {
		this.replyTo = ArraysUtil.append(this.replyTo, replyTo);
		return _this();
	}

	/**
	 * Appends REPLY-TO address.
	 *
	 * @param replyTo Address may be specified with personal name like this: {@code Jenny Doe <email@foo.com>}.
	 * @return this
	 * @see #replyTo(EmailAddress)
	 */
	public T replyTo(final String replyTo) {
		return replyTo(EmailAddress.of(replyTo));
	}

	/**
	 * Appends REPLY-TO address.
	 *
	 * @param personalName personal name.
	 * @param replyTo      email address.
	 * @return this
	 * @see #replyTo(EmailAddress)
	 */
	public T replyTo(final String personalName, final String replyTo) {
		return replyTo(new EmailAddress(personalName, replyTo));
	}

	/**
	 * Appends REPLY-TO address.
	 *
	 * @param replyTo {@link Address} to add.
	 * @return this
	 * @see #replyTo(EmailAddress)
	 */
	public T replyTo(final Address replyTo) {
		return replyTo(EmailAddress.of(replyTo));
	}

	/**
	 * Appends one or more REPLY-TO address.
	 *
	 * @param replyTos array of {@link EmailAddress}es to set.
	 * @return this
	 * @see #replyTo(EmailAddress...)
	 */
	public T replyTo(final String... replyTos) {
		return replyTo(EmailAddress.of(replyTos));
	}

	/**
	 * Appeds one or more REPLY-TO address.
	 *
	 * @param replyTos array of {@link Address}es to set.
	 * @return this
	 * @see #replyTo(EmailAddress...)
	 */
	public T replyTo(final Address... replyTos) {
		return replyTo(EmailAddress.of(replyTos));
	}

	/**
	 * Appends REPLY-TO addresses.
	 *
	 * @param replyTo vararg of {@link EmailAddress}es to set.
	 * @return this
	 */
	public T replyTo(final EmailAddress... replyTo) {
		this.replyTo = ArraysUtil.join(this.replyTo, valueOrEmptyArray(replyTo));
		return _this();
	}

	/**
	 * Returns REPLY-TO addresses.
	 */
	public EmailAddress[] replyTo() {
		return replyTo;
	}

	/**
	 * Resets all REPLY-To addresses.
	 */
	public T resetReplyTo() {
		this.replyTo = EmailAddress.EMPTY_ARRAY;
		return _this();
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
	public T cc(final EmailAddress to) {
		this.cc = ArraysUtil.append(this.cc, to);
		return _this();
	}

	/**
	 * Appends CC address.
	 *
	 * @param cc Address may be specified with personal name like this: {@code Jenny Doe <email@foo.com>}.
	 * @return this
	 * @see #cc(EmailAddress)
	 */
	public T cc(final String cc) {
		return cc(EmailAddress.of(cc));
	}

	/**
	 * Appends CC address.
	 *
	 * @param personalName personal name.
	 * @param cc           email address.
	 * @return this
	 * @see #cc(EmailAddress)
	 */
	public T cc(final String personalName, final String cc) {
		return cc(new EmailAddress(personalName, cc));
	}

	/**
	 * Appends CC address.
	 *
	 * @param cc {@link Address} to add.
	 * @return this
	 * @see #cc(EmailAddress)
	 */
	public T cc(final Address cc) {
		return cc(EmailAddress.of(cc));
	}

	/**
	 * Sets one or more CC address.
	 *
	 * @param ccs array of {@link String}s to set.
	 * @return this
	 * @see #cc(EmailAddress...)
	 */
	public T cc(final String... ccs) {
		return cc(EmailAddress.of(ccs));
	}

	/**
	 * Sets one or more CC address.
	 *
	 * @param ccs array of {@link Address}s to set.
	 * @return this
	 * @see #cc(EmailAddress...)
	 */
	public T cc(final Address... ccs) {
		return cc(EmailAddress.of(ccs));
	}

	/**
	 * Appends CC addresses.
	 *
	 * @param ccs vararg of {@link EmailAddress}es to set.
	 * @return this
	 */
	public T cc(final EmailAddress... ccs) {
		this.cc = ArraysUtil.join(this.cc, valueOrEmptyArray(ccs));
		return _this();
	}

	/**
	 * Returns CC addresses.
	 */
	public EmailAddress[] cc() {
		return cc;
	}

	/**
	 * Resets all CC addresses.
	 */
	public T resetCc() {
		this.cc = EmailAddress.EMPTY_ARRAY;
		return _this();
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
	public T subject(final String subject) {
		this.subject = subject;
		return _this();
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
	public T subject(final String subject, final String encoding) {
		subject(subject);
		this.subjectEncoding = encoding;
		return _this();
	}

	/**
	 * Returns message subject.
	 *
	 * @return message subject.
	 */
	public String subject() {
		return this.subject;
	}

	/**
	 * Returns the message subject encoding.
	 *
	 * @return the message subject encoding.
	 */
	public String subjectEncoding() {
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
	public List<EmailMessage> messages() {
		return messages;
	}

	/**
	 * Adds multiple messages.
	 *
	 * @param msgsToAdd {@link List} of {@link EmailMessage}s to add.
	 * @return this
	 */
	public T message(final List<EmailMessage> msgsToAdd) {
		messages.addAll(msgsToAdd);
		return _this();
	}

	/**
	 * Adds an {@link EmailMessage}.
	 *
	 * @param msgToAdd {@link EmailMessage} to add.
	 * @return this
	 */
	public T message(final EmailMessage msgToAdd) {
		messages.add(msgToAdd);
		return _this();
	}

	/**
	 * Adds a {@link EmailMessage}.
	 *
	 * @param text     The text to add as a {@link String}.
	 * @param mimeType The MIME type as a {@link String}.
	 * @param encoding The encoding as a {@link String}.
	 * @return this
	 * @see #message(EmailMessage)
	 */
	public T message(final String text, final String mimeType, final String encoding) {
		return message(new EmailMessage(text, mimeType, encoding));
	}

	/**
	 * Adds a {@link EmailMessage}.
	 *
	 * @param text     The text to add as a {@link String}.
	 * @param mimeType The MIME type as a {@link String}.
	 * @return this
	 * @see #message(EmailMessage)
	 */
	public T message(final String text, final String mimeType) {
		return message(new EmailMessage(text, mimeType));
	}

	/**
	 * Adds plain message text.
	 *
	 * @param text The text to add as a {@link String}.
	 * @return this
	 * @see #message(String, String)
	 */
	public T textMessage(final String text) {
		return message(text, MimeTypes.MIME_TEXT_PLAIN);
	}

	/**
	 * Adds plain message text.
	 *
	 * @param text     The text to add as a {@link String}.
	 * @param encoding The encoding as a {@link String}.
	 * @return this
	 * @see #message(EmailMessage)
	 */
	public T textMessage(final String text, final String encoding) {
		return message(new EmailMessage(text, MimeTypes.MIME_TEXT_PLAIN, encoding));
	}

	/**
	 * Adds HTML message.
	 *
	 * @param html The HTML to add as a {@link String}.
	 * @return this
	 * @see #message(EmailMessage)
	 */
	public T htmlMessage(final String html) {
		return message(new EmailMessage(html, MimeTypes.MIME_TEXT_HTML));
	}

	/**
	 * Adds HTML message.
	 *
	 * @param html     The HTML to add as a {@link String}.
	 * @param encoding The encoding as a {@link String}.
	 * @return this
	 * @see #message(EmailMessage)
	 */
	public T htmlMessage(final String html, final String encoding) {
		return message(new EmailMessage(html, MimeTypes.MIME_TEXT_HTML, encoding));
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
	protected Map<String, String> headers() {
		return headers;
	}

	/**
	 * Sets header value.
	 *
	 * @param name  The name of the header.
	 * @param value The value of the header.
	 * @return this
	 */
	public T header(final String name, final String value) {
		headers.put(name, value);
		return _this();
	}

	/**
	 * Sets headers.
	 *
	 * @param headersToSet Headers to set.
	 * @return this
	 */
	public T headers(final Map<String, String> headersToSet) {
		headers.putAll(headersToSet);
		return _this();
	}


	/**
	 * Sets headers.
	 *
	 * @param headersToSet Headers to set.
	 * @return this
	 * @see #header(String, String)
	 */
	public T headers(final Enumeration<Header> headersToSet) {
		while (headersToSet.hasMoreElements()) {
			final Header header = headersToSet.nextElement();
			header(header.getName(), header.getValue());
		}
		return _this();
	}

	/**
	 * Returns the value of a header.
	 *
	 * @param name The name of the header.
	 * @return The value of the header.
	 */
	public String header(final String name) {
		return headers.get(name);
	}

	/**
	 * Sets email priority.
	 *
	 * @param priority - Values of 1 through 5 are acceptable, with 1 being the highest priority, 3 = normal
	 *                 and 5 = lowest priority.
	 */
	public T priority(final int priority) {
		header(X_PRIORITY, String.valueOf(priority));
		return _this();
	}

	/**
	 * Returns emails priority (1 - 5) or <code>-1</code> if priority not available.
	 *
	 * @see #priority(int)
	 */
	public int priority() {
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
	public List<EmailAttachment<? extends DataSource>> attachments() {
		return attachments;
	}

	/**
	 * Adds {@link EmailAttachment}s.
	 *
	 * @param attachments {@link List} of {@link EmailAttachment}s to add.
	 * @return this
	 */
	protected T storeAttachments(final List<EmailAttachment<? extends DataSource>> attachments) {
		this.attachments.addAll(attachments);
		return _this();
	}

	/**
	 * Adds {@link EmailAttachment}.
	 *
	 * @param attachment {@link EmailAttachment} to add.
	 * @return this
	 */
	protected T storeAttachment(final EmailAttachment<? extends DataSource> attachment) {
		this.attachments.add(attachment);
		return _this();
	}

	/**
	 * Adds {@link EmailAttachment}s.
	 *
	 * @param attachments {@link List} of {@link EmailAttachment}s to add.
	 * @return this
	 */
	public T attachments(final List<EmailAttachment<? extends DataSource>> attachments) {
		for (final EmailAttachment<?> attachment : attachments) {
			attachment(attachment);
		}
		return _this();
	}

	/**
	 * Adds {@link EmailAttachment}. Content ID will be set to {@code null}.
	 *
	 * @param attachment {@link EmailAttachment} to add.
	 * @return this
	 */
	public T attachment(final EmailAttachment<? extends DataSource> attachment) {
		attachment.setContentId(null);
		return storeAttachment(attachment);
	}

	/**
	 * @see #attachment(EmailAttachment)
	 */
	public T attachment(final EmailAttachmentBuilder builder) {
		return attachment(builder.buildByteArrayDataSource());
	}

	/**
	 * Attaches the embedded attachment: Content ID will be set if missing from attachment's file name.
	 *
	 * @param builder {@link EmailAttachmentBuilder}
	 * @return this
	 * @see #embeddedAttachment(EmailAttachment)
	 */
	public T embeddedAttachment(final EmailAttachmentBuilder builder) {
		builder.setContentIdFromNameIfMissing();

		// https://github.com/oblac/jodd/issues/546
		// https://github.com/oblac/jodd/issues/404#issuecomment-297011351
		// content disposition will be set to "inline"
		builder.inline(true);

		return embeddedAttachment(builder.buildByteArrayDataSource());
	}

	/**
	 * Embed {@link EmailAttachment} to last message. No header is changed.
	 *
	 * @param attachment {@link EmailAttachment}
	 * @return this
	 * @see #storeAttachment(EmailAttachment)
	 */
	public T embeddedAttachment(final EmailAttachment<? extends DataSource> attachment) {
		storeAttachment(attachment);

		final List<EmailMessage> messages = messages();
		final int size = messages.size();
		if (size > 1) {
			// Add to last message
			final int lastMessagePos = size - 1;
			final EmailMessage lastMessage = messages.get(lastMessagePos);
			attachment.setEmbeddedMessage(lastMessage);
		}

		return _this();
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
	public T sentDate(final Date date) {
		sentDate = date;
		return _this();
	}


	/**
	 * Returns email's sent date. If return value is {@code null}, then date
	 * will be set during the process of sending.
	 *
	 * @return email's sent date or {@code null} if it will be set later.
	 */
	public Date sentDate() {
		return sentDate;
	}

	// ---------------------------------------------------------------- toString

	@Override
	public String toString() {
		return "Email{'" + from() + "\', subject='" + subject() + "\'}";
	}

	// ---------------------------------------------------------------- helper

	protected EmailAddress[] valueOrEmptyArray(EmailAddress[] arr) {
		if (arr == null) {
			arr = EmailAddress.EMPTY_ARRAY;
		}
		return arr;
	}
}
