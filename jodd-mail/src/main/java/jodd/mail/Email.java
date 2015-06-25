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

import jodd.util.MimeTypes;

import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * E-mail holds all parts of an email and handle attachments.
 */
public class Email extends CommonEmail {

	/**
	 * Static constructor for fluent interface.
	 */
	public static Email create() {
		return new Email();
	}

	// ---------------------------------------------------------------- from

	/**
	 * Sets the FROM address. Address may be specified with personal name
	 * like this: <code>"Jenny Doe &lt;email@foo.com&gt;</code>.
	 */
	public Email from(String from) {
		setFrom(new MailAddress(from));
		return this;
	}
	/**
	 * Sets the FROM address by providing personal name and address.
	 */
	public Email from(String personal, String from) {
		setFrom(new MailAddress(personal, from));
		return this;
	}
	/**
	 * Sets the FROM address from {@link jodd.mail.EmailAddress}.
	 */
	public Email from(EmailAddress emailAddress) {
		setFrom(new MailAddress(emailAddress));
		return this;
	}
	/**
	 * Sets the FROM address from {@link javax.mail.internet.InternetAddress}.
	 */
	public Email from(InternetAddress internetAddress) {
		setFrom(new MailAddress(internetAddress));
		return this;
	}

	// ---------------------------------------------------------------- to

	/**
	 * Appends TO address. Address may be specified with personal name
	 * like this: <code>"Jenny Doe &lt;email@foo.com&gt;</code>.
	 */
	public Email to(String to) {
		addTo(new MailAddress(to));
		return this;
	}

	/**
	 * Appends TO address by personal name and email address.
	 */
	public Email to(String personalName, String to) {
		addTo(new MailAddress(personalName, to));
		return this;
	}
	/**
	 * Appends TO address from {@link jodd.mail.EmailAddress}.
	 */
	public Email to(EmailAddress emailAddress) {
		addTo(new MailAddress(emailAddress));
		return this;
	}
	/**
	 * Appends TO address from <code>InternetAddress</code>.
	 */
	public Email to(InternetAddress internetAddress) {
		addTo(new MailAddress(internetAddress));
		return this;
	}

	/**
	 * Sets one or more TO address. Address may be specified with personal name
	 * like this: <code>"Jenny Doe &lt;email@foo.com&gt;</code>.
	 */
	public Email to(String[] tos) {
		setTo(MailAddress.createFrom(tos));
		return this;
	}
	/**
	 * Sets one or more TO addresses.
	 */
	public Email to(EmailAddress[] tos) {
		setTo(MailAddress.createFrom(tos));
		return this;
	}
	/**
	 * Sets one or more TO addresses.
	 */
	public Email to(InternetAddress[] tos) {
		setTo(MailAddress.createFrom(tos));
		return this;
	}

	// ---------------------------------------------------------------- reply to

	/**
	 * Appends REPLY-TO address. Address may be specified with personal name
	 * like this: <code>"Jenny Doe &lt;email@foo.com&gt;</code>.
	 */
	public Email replyTo(String replyTo) {
		addReplyTo(new MailAddress(replyTo));
		return this;
	}
	/**
	 * Appends REPLY-TO address.
	 */
	public Email replyTo(String personalName, String replyTo) {
		addReplyTo(new MailAddress(personalName, replyTo));
		return this;
	}
	/**
	 * Appends REPLY-TO address.
	 */
	public Email replyTo(EmailAddress emailAddress) {
		addReplyTo(new MailAddress(emailAddress));
		return this;
	}
	/**
	 * Appends REPLY-TO address.
	 */
	public Email replyTo(InternetAddress internetAddress) {
		addReplyTo(new MailAddress(internetAddress));
		return this;
	}

	/**
	 * Sets one or more REPLY-TO address. Address may be specified with personal name
	 * like this: <code>"Jenny Doe &lt;email@foo.com&gt;</code>.
	 */
	public Email replyTo(String[] replyTos) {
		setReplyTo(MailAddress.createFrom(replyTos));
		return this;
	}
	/**
	 * Sets one or more REPLY-TO address.
	 */
	public Email replyTo(EmailAddress[] replyTos) {
		setReplyTo(MailAddress.createFrom(replyTos));
		return this;
	}
	/**
	 * Sets one or more REPLY-TO address.
	 */
	public Email replyTo(InternetAddress[] replyTos) {
		setReplyTo(MailAddress.createFrom(replyTos));
		return this;
	}

	// ---------------------------------------------------------------- cc

	/**
	 * Appends CC address. Address may be specified with personal name
	 * like this: <code>"Jenny Doe &lt;email@foo.com&gt;</code>.
	 */
	public Email cc(String cc) {
		addCc(new MailAddress(cc));
		return this;
	}
	/**
	 * Appends CC address.
	 */
	public Email cc(String personalName, String cc) {
		addCc(new MailAddress(personalName, cc));
		return this;
	}
	/**
	 * Appends CC address.
	 */
	public Email cc(EmailAddress emailAddress) {
		addCc(new MailAddress(emailAddress));
		return this;
	}
	/**
	 * Appends CC address.
	 */
	public Email cc(InternetAddress internetAddress) {
		addCc(new MailAddress(internetAddress));
		return this;
	}

	/**
	 * Sets one or more CC address. Address may be specified with personal name
	 * like this: <code>"Jenny Doe &lt;email@foo.com&gt;</code>.
	 */
	public Email cc(String[] ccs) {
		setCc(MailAddress.createFrom(ccs));
		return this;
	}
	/**
	 * Sets one or more CC address.
	 */
	public Email cc(EmailAddress[] ccs) {
		setCc(MailAddress.createFrom(ccs));
		return this;
	}
	/**
	 * Sets one or more CC address.
	 */
	public Email cc(InternetAddress[] ccs) {
		setCc(MailAddress.createFrom(ccs));
		return this;
	}

	// ---------------------------------------------------------------- bcc

	/**
	 * Appends BCC address. Address may be specified with personal name
	 * like this: <code>"Jenny Doe &lt;email@foo.com&gt;</code>.
	 */
	public Email bcc(String bcc) {
		addBcc(new MailAddress(bcc));
		return this;
	}
	/**
	 * Appends BCC address.
	 */
	public Email bcc(String personal, String bcc) {
		addBcc(new MailAddress(personal, bcc));
		return this;
	}
	/**
	 * Appends BCC address.
	 */
	public Email bcc(EmailAddress emailAddress) {
		addBcc(new MailAddress(emailAddress));
		return this;
	}
	/**
	 * Appends BCC address.
	 */
	public Email bcc(InternetAddress internetAddress) {
		addBcc(new MailAddress(internetAddress));
		return this;
	}

	/**
	 * Sets one or more BCC addresses.
	 */
	public Email bcc(String[] bccs) {
		setBcc(MailAddress.createFrom(bccs));
		return this;
	}
	/**
	 * Sets one or more BCC addresses.
	 */
	public Email bcc(EmailAddress[] bccs) {
		setBcc(MailAddress.createFrom(bccs));
		return this;
	}
	/**
	 * Sets one or more BCC addresses.
	 */
	public Email bcc(InternetAddress[] bccs) {
		setBcc(MailAddress.createFrom(bccs));
		return this;
	}

	// ---------------------------------------------------------------- subject

	public Email subject(String subject) {
		setSubject(subject);
		return this;
	}

	public Email subject(String subject, String subjectEncoding) {
		setSubject(subject, subjectEncoding);
		return this;
	}

	// ---------------------------------------------------------------- message

	public Email message(String text, String mimeType, String encoding) {
		addMessage(text, mimeType, encoding);
		return this;
	}
	public Email message(String text, String mimeType) {
		addMessage(text, mimeType);
		return this;
	}

	/**
	 * Adds plain message text.
	 */
	public Email addText(String text) {
		messages.add(new EmailMessage(text, MimeTypes.MIME_TEXT_PLAIN));
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

	// ---------------------------------------------------------------- attachments

	protected ArrayList<EmailAttachment> attachments;

	/**
	 * Returns an array of attachments or <code>null</code> if no attachment enclosed with this email. 
	 */
	public List<EmailAttachment> getAttachments() {
		return attachments;
	}

	/**
	 * Adds attachment.
	 */
	public Email attach(EmailAttachment emailAttachment) {
		if (attachments == null) {
			attachments = new ArrayList<>();
		}
		attachments.add(emailAttachment);
		return this;
	}

	/**
	 * Embed attachment to last message.
	 */
	public Email embed(EmailAttachment emailAttachment) {
		attach(emailAttachment);

		if (emailAttachment.isInline()) {
			int size = messages.size();
			if (size > 0) {
				emailAttachment.setEmbeddedMessage(messages.get(size - 1));		// get last message
			}
		}
		return this;
	}

	public Email attach(EmailAttachmentBuilder emailAttachmentBuilder) {
		emailAttachmentBuilder.setInline(false);
		attach(emailAttachmentBuilder.create());
		return this;
	}

	public Email embed(EmailAttachmentBuilder emailAttachmentBuilder) {
		emailAttachmentBuilder.setInline(true);
		embed(emailAttachmentBuilder.create());
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
