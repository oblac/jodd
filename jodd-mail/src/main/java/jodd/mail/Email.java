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

import javax.activation.DataSource;
import javax.mail.Address;
import java.util.Date;

import static jodd.mail.EmailAttachmentBuilder.DEPRECATED_MSG;

/**
 * E-mail holds all parts of an email and handle attachments.
 */
public class Email extends CommonEmail<Email> {

	@Override
	Email getThis() {
		return this;
	}

	/**
	 * Static constructor for fluent interface.
	 */
	public static Email create() {
		return new Email();
	}

	@Override
	public Email clone() {
		return create()

			// from / reply-to
			.setFrom(getFrom())
			.setReplyTo(getReplyTo())

			// recipients
			.setTo(getTo())
			.setCc(getCc())
			.setBcc(getBcc())

			// subject
			.setSubject(getSubject(), getSubjectEncoding())

			// dates
			.setSentDate(getSentDate())

			// headers - includes priority
			.setHeaders(getAllHeaders())

			// content / attachments
			.storeAttachments(getAttachments())
			.addMessages(getAllMessages());
	}

	// ---------------------------------------------------------------- date

	/**
	 * Sets current date as the sent date.
	 *
	 * @return this
	 * @see #setSentDate(Date)
	 */
	public Email setCurrentSentDate() {
		return setSentDate(new Date());
	}

	// ---------------------------------------------------------------- bcc

	/**
	 * BCC address.
	 */
	private EmailAddress[] bcc = EmailAddress.EMPTY_ARRAY;

	/**
	 * Appends BCC address.
	 *
	 * @param to {@link EmailAddress} to add.
	 * @return this
	 */
	public Email addBcc(final EmailAddress to) {
		this.bcc = ArraysUtil.append(this.bcc, to);
		return getThis();
	}

	/**
	 * Appends BCC address.
	 *
	 * @param bcc Address may be specified with personal name like this: {@code Jenny Doe <email@foo.com>}.
	 * @return this
	 * @see #addBcc(EmailAddress)
	 */
	public Email addBcc(final String bcc) {
		return addBcc(new EmailAddress(bcc));
	}

	/**
	 * Appends BCC address.
	 *
	 * @param personalName personal name.
	 * @param bcc          email address.
	 * @return this
	 * @see #addBcc(EmailAddress)
	 */
	public Email addBcc(final String personalName, final String bcc) {
		return addBcc(new EmailAddress(personalName, bcc));
	}

	/**
	 * Appends BCC address.
	 *
	 * @param bcc {@link Address} to add.
	 * @return this
	 * @see #addBcc(EmailAddress)
	 */
	public Email addBcc(final Address bcc) {
		return addBcc(new EmailAddress(bcc));
	}

	/**
	 * Sets BCC address.
	 *
	 * @param bccs array of {@link String}s to set.
	 * @return this
	 * @see #setBcc(EmailAddress...)
	 */
	public Email setBcc(final String[] bccs) {
		return setBcc(EmailAddress.createFrom(bccs));
	}

	/**
	 * Sets BCC address.
	 *
	 * @param bccs array of {@link Address}es to set.
	 * @return this
	 * @see #setBcc(EmailAddress...)
	 */
	public Email setBcc(final Address[] bccs) {
		return setBcc(EmailAddress.createFrom(bccs));
	}

	/**
	 * Sets one or more BCC addresses.
	 *
	 * @param bccs vararg of {@link EmailAddress}es to set.
	 * @return this
	 */
	public Email setBcc(final EmailAddress... bccs) {
		this.bcc = getValueOrEmptyArray(bccs);
		return getThis();
	}

	/**
	 * Returns BCC addresses.
	 */
	public EmailAddress[] getBcc() {
		return bcc;
	}

	// ---------------------------------------------------------------- deprecated

	// ---------------------------------------------------------------- from

	/**
	 * @deprecated Use {@link #setFrom(String)}
	 */
	@Deprecated
	public Email from(final String from) {
		return setFrom(from);
	}

	/**
	 * @deprecated Use {@link #setFrom(String, String)}
	 */
	@Deprecated
	public Email from(final String personal, final String from) {
		return setFrom(personal, from);
	}

	/**
	 * @deprecated Use {@link #setFrom(Address)}
	 */
	@Deprecated
	public Email from(final Address address) {
		return setFrom(address);
	}

	// ---------------------------------------------------------------- to

	/**
	 * @deprecated Use {@link #addTo(String)}
	 */
	@Deprecated
	public Email to(final String to) {
		return addTo(new EmailAddress(to));
	}

	/**
	 * @deprecated Use {@link #addTo(String, String)}
	 */
	@Deprecated
	public Email to(final String personalName, final String to) {
		return addTo(personalName, to);
	}

	/**
	 * @deprecated Use {@link #addTo(Address)}
	 */
	@Deprecated
	public Email to(final Address address) {
		return addTo(address);
	}

	/**
	 * @deprecated Use {@link #setTo(String[])}
	 */
	@Deprecated
	public Email to(final String[] tos) {
		return setTo(tos);
	}

	/**
	 * @deprecated Use {@link #setTo(Address[])}
	 */
	@Deprecated
	public Email to(final Address[] tos) {
		return setTo(tos);
	}

	// ---------------------------------------------------------------- reply to

	/**
	 * @deprecated Use {@link #addReplyTo(String)}
	 */
	@Deprecated
	public Email replyTo(final String replyTo) {
		return addReplyTo(replyTo);
	}

	/**
	 * @deprecated Use {@link #addReplyTo(String, String)}
	 */
	@Deprecated
	public Email replyTo(final String personalName, final String replyTo) {
		return addReplyTo(personalName, replyTo);
	}

	/**
	 * @deprecated Use {@link #addReplyTo(Address)}
	 */
	@Deprecated
	public Email replyTo(final Address address) {
		return addReplyTo(address);
	}

	/**
	 * @deprecated Use {@link #setReplyTo(String[])}
	 */
	@Deprecated
	public Email replyTo(final String[] replyTos) {
		return setReplyTo(replyTos);
	}

	/**
	 * @deprecated Use {@link #setReplyTo(Address[])}
	 */
	@Deprecated
	public Email replyTo(final Address[] replyTos) {
		return setReplyTo(replyTos);
	}

	// ---------------------------------------------------------------- cc

	/**
	 * @deprecated Use {@link #addCc(String)}
	 */
	@Deprecated
	public Email cc(final String cc) {
		return addCc(cc);
	}

	/**
	 * @deprecated Use {@link #addCc(String, String)}
	 */
	@Deprecated
	public Email cc(final String personalName, final String cc) {
		return addCc(personalName, cc);
	}

	/**
	 * @deprecated Use {@link #addCc(Address)}
	 */
	@Deprecated
	public Email cc(final Address address) {
		return addCc(address);
	}

	/**
	 * @deprecated Use {@link #setCc(String[])}
	 */
	@Deprecated
	public Email cc(final String[] ccs) {
		return setCc(ccs);
	}

	/**
	 * @deprecated Use {@link #setCc(Address[])}
	 */
	@Deprecated
	public Email cc(final Address[] ccs) {
		return setCc(EmailAddress.createFrom(ccs));
	}

	// ---------------------------------------------------------------- bcc

	/**
	 * @deprecated Use {@link #addBcc(String)}
	 */
	@Deprecated
	public Email bcc(final String bcc) {
		return addBcc(bcc);
	}

	/**
	 * @deprecated Use {@link #addBcc(String, String)}
	 */
	@Deprecated
	public Email bcc(final String personal, final String bcc) {
		return addBcc(personal, bcc);
	}

	/**
	 * @deprecated Use {@link #addBcc(Address)}
	 */
	@Deprecated
	public Email bcc(final Address address) {
		return addBcc(address);
	}

	/**
	 * @deprecated Use {@link #setBcc(String[])}
	 */
	@Deprecated
	public Email bcc(final String[] bccs) {
		return setBcc(EmailAddress.createFrom(bccs));
	}

	/**
	 * @deprecated Use {@link #setBcc(Address[])}
	 */
	@Deprecated
	public Email bcc(final Address[] bccs) {
		return setBcc(bccs);
	}

	// ---------------------------------------------------------------- subject

	/**
	 * @deprecated Use {@link #setSubject(String)}
	 */
	@Deprecated
	public Email subject(final String subject) {
		return setSubject(subject);
	}

	/**
	 * @deprecated Use {@link #setSubject(String, String)}
	 */
	@Deprecated
	public Email subject(final String subject, final String subjectEncoding) {
		return setSubject(subject, subjectEncoding);
	}

	// ---------------------------------------------------------------- message

	/**
	 * @deprecated Use {@link #addMessage(String, String, String)}
	 */
	@Deprecated
	public Email message(final String text, final String mimeType, final String encoding) {
		return addMessage(text, mimeType, encoding);
	}

	/**
	 * @deprecated Use {@link #addMessage(String, String)}
	 */
	@Deprecated
	public Email message(final String text, final String mimeType) {
		return addMessage(text, mimeType);
	}

	// ---------------------------------------------------------------- attachments

	/**
	 * @deprecated Use {@link #addAttachment(EmailAttachmentBuilder)}
	 */
	@Deprecated
	public Email attach(final EmailAttachment<DataSource> emailAttachment) {
		throw new UnsupportedOperationException(String.format(DEPRECATED_MSG, "#addAttachment(EmailAttachmentBuilder)"));

	}

	/**
	 * @deprecated Use {@link #addAttachment(EmailAttachmentBuilder)}
	 */
	@Deprecated
	public Email attach(final EmailAttachmentBuilder emailAttachmentBuilder) {
		return addAttachment(emailAttachmentBuilder);
	}

	/**
	 * @deprecated Use {@link #embedAttachment(EmailAttachmentBuilder)}
	 */
	@Deprecated
	public Email embed(final EmailAttachment<DataSource> emailAttachment) {
		throw new UnsupportedOperationException(String.format(DEPRECATED_MSG, "#embedAttachment(EmailAttachmentBuilder)"));
	}

	/**
	 * @deprecated Use {@link #embedAttachment(EmailAttachmentBuilder)}
	 */
	@Deprecated
	public Email embed(final EmailAttachmentBuilder emailAttachmentBuilder) {
		return embedAttachment(emailAttachmentBuilder);
	}

	// ---------------------------------------------------------------- headers

	/**
	 * @deprecated Use {@link #setHeader(String, String)}
	 */
	@Deprecated
	public Email header(final String name, final String value) {
		return setHeader(name, value);
	}

	/**
	 * @deprecated Use {@link #setPriority(int)}
	 */
	@Deprecated
	public Email priority(final int priority) {
		return setPriority(priority);
	}

	// ---------------------------------------------------------------- date

	/**
	 * @deprecated Use {@link #setSentDate(Date)}
	 */
	@Deprecated
	public Email sentOn(final Date date) {
		return setSentDate(date);
	}
}
