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

import javax.mail.Address;
import java.util.Date;

/**
 * E-mail holds all parts of an email and handle attachments.
 */
public class Email extends CommonEmail<Email> {

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
			.from(from())
			.replyTo(replyTo())

			// recipients
			.to(to())
			.cc(cc())
			.bcc(bcc())

			// subject
			.subject(subject(), subjectEncoding())

			// dates
			.sentDate(sentDate())

			// headers - includes priority
			.headers(headers())

			// content / attachments
			.storeAttachments(attachments())
			.message(messages());
	}

	// ---------------------------------------------------------------- date

	/**
	 * Sets current date as the sent date.
	 *
	 * @return this
	 * @see #sentDate(Date)
	 */
	public Email currentSentDate() {
		return sentDate(new Date());
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
	public Email bcc(final EmailAddress to) {
		this.bcc = ArraysUtil.append(this.bcc, to);
		return _this();
	}

	/**
	 * Appends BCC address.
	 *
	 * @param bcc Address may be specified with personal name like this: {@code Jenny Doe <email@foo.com>}.
	 * @return this
	 * @see #bcc(EmailAddress)
	 */
	public Email bcc(final String bcc) {
		return bcc(EmailAddress.of(bcc));
	}

	/**
	 * Appends BCC address.
	 *
	 * @param personalName personal name.
	 * @param bcc          email address.
	 * @return this
	 * @see #bcc(EmailAddress)
	 */
	public Email bcc(final String personalName, final String bcc) {
		return bcc(new EmailAddress(personalName, bcc));
	}

	/**
	 * Appends BCC address.
	 *
	 * @param bcc {@link Address} to add.
	 * @return this
	 * @see #bcc(EmailAddress)
	 */
	public Email bcc(final Address bcc) {
		return bcc(EmailAddress.of(bcc));
	}

	/**
	 * Appends BCC address.
	 *
	 * @param bccs array of {@link String}s to set.
	 * @return this
	 * @see #bcc(EmailAddress...)
	 */
	public Email bcc(final String... bccs) {
		return bcc(EmailAddress.of(bccs));
	}

	/**
	 * Appends BCC address.
	 *
	 * @param bccs array of {@link Address}es to set.
	 * @return this
	 * @see #bcc(EmailAddress...)
	 */
	public Email bcc(final Address... bccs) {
		return bcc(EmailAddress.of(bccs));
	}

	/**
	 * Appends one or more BCC addresses.
	 *
	 * @param bccs vararg of {@link EmailAddress}es to set.
	 * @return this
	 */
	public Email bcc(final EmailAddress... bccs) {
		this.bcc = ArraysUtil.join(this.bcc, valueOrEmptyArray(bccs));
		return _this();
	}

	/**
	 * Returns BCC addresses.
	 */
	public EmailAddress[] bcc() {
		return bcc;
	}

	/**
	 * Resets BCC addresses.
	 */
	public Email resetBcc() {
		this.bcc = EmailAddress.EMPTY_ARRAY;
		return _this();
	}


}
