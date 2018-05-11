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

import jodd.core.JoddCore;
import jodd.util.StringUtil;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;

/**
 * Storage for personal name and email address.
 * Serves as an email address adapter between various formats.
 */
public class EmailAddress {

	public static final EmailAddress[] EMPTY_ARRAY = new EmailAddress[0];

	/**
	 * Email address.
	 */
	private final String email;

	/**
	 * Personal name.
	 */
	private final String personalName;

	/**
	 * Creates new address by specifying email and personal name.
	 *
	 * @param personalName personal name.
	 * @param email        email address.
	 */
	public EmailAddress(final String personalName, final String email) {
		this.email = email;
		this.personalName = personalName;
	}

	/**
	 * @see #EmailAddress(String, String)
	 */
	public static EmailAddress of(final String personalName, final String email) {
		return new EmailAddress(personalName, email);
	}

	/**
	 * Creates new address by specifying one of the following:
	 * <ul>
	 * <li>{@code "foo@bar.com" - only email address.}</li>
	 * <li>{@code "Jenny Doe &lt;foo@bar.com&gt;" - first part of the string is personal name,
	 * and the other part is email, surrounded with < and >.}</li>
	 * </ul>
	 *
	 * @param address {@link String} containing address to convert.
	 */
	public static EmailAddress of(String address) {
		address = address.trim();

		if (!StringUtil.endsWithChar(address, '>')) {
			return new EmailAddress(null, address);
		}

		final int ndx = address.lastIndexOf('<');
		if (ndx == -1) {
			return new EmailAddress(null, address);
		}

		String email = address.substring(ndx + 1, address.length() - 1);
		String personalName = address.substring(0, ndx).trim();
		return new EmailAddress(personalName, email);
	}

	/**
	 * Creates new email address from {@link InternetAddress}.
	 *
	 * @param internetAddress {@link InternetAddress} to convert
	 */
	public static EmailAddress of(final InternetAddress internetAddress) {
		return new EmailAddress(internetAddress.getPersonal(), internetAddress.getAddress());
	}

	/**
	 * Creates new email address from {@link InternetAddress}.
	 *
	 * @param address {@link Address} to convert.
	 */
	public static EmailAddress of(final Address address) {
		return of(address.toString());
	}

	// ---------------------------------------------------------------- getters

	/**
	 * Returns email address.
	 *
	 * @return email address.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Returns personal name.
	 *
	 * @return personal name. Value may be {@code null}.
	 */
	public String getPersonalName() {
		return personalName;
	}

	/**
	 * Returns string representation of this.
	 *
	 * @return String representation of this.
	 */
	@Override
	public String toString() {
		if (this.personalName == null) {
			return this.email;
		}
		return this.personalName + " <" + this.email + '>';
	}

	// ---------------------------------------------------------------- convert

	/**
	 * Creates new {@link InternetAddress} from current data.
	 *
	 * @return {@link InternetAddress} from current data.
	 */
	public InternetAddress toInternetAddress() throws AddressException {
		try {
			return new InternetAddress(email, personalName, JoddCore.encoding);
		} catch (final UnsupportedEncodingException ueex) {
			throw new AddressException(ueex.toString());
		}
	}

	// ---------------------------------------------------------------- arrays

	/**
	 * Converts array of {@link Address} to {@link EmailAddress}.
	 *
	 * @param addresses array of {@link Address}es to convert.
	 * @return an array of {@link EmailAddress}.
	 */
	public static EmailAddress[] of(final Address... addresses) {
		if (addresses == null) {
			return EmailAddress.EMPTY_ARRAY;
		}
		if (addresses.length == 0) {
			return EmailAddress.EMPTY_ARRAY;
		}

		final EmailAddress[] res = new EmailAddress[addresses.length];

		for (int i = 0; i < addresses.length; i++) {
			res[i] = EmailAddress.of(addresses[i]);
		}

		return res;
	}

	/**
	 * Converts array of {@link String} to {@link EmailAddress}.
	 *
	 * @param addresses array of {@link String}s to convert.
	 * @return an array of {@link EmailAddress}.
	 */
	public static EmailAddress[] of(final String... addresses) {
		if (addresses == null) {
			return EmailAddress.EMPTY_ARRAY;
		}
		if (addresses.length == 0) {
			return EmailAddress.EMPTY_ARRAY;
		}

		final EmailAddress[] res = new EmailAddress[addresses.length];

		for (int i = 0; i < addresses.length; i++) {
			res[i] = EmailAddress.of(addresses[i]);
		}

		return res;
	}

	/**
	 * Convert from array of {@link EmailAddress} to array of {@link InternetAddress}.
	 *
	 * @param addresses {@link EmailMessage}
	 * @return array of {@link InternetAddress}. Returns empty array if addresses was {@code null}.
	 * @throws MessagingException if there are failures
	 */
	public static InternetAddress[] convert(final EmailAddress[] addresses) throws MessagingException {
		if (addresses == null) {
			return new InternetAddress[0];
		}

		final int numRecipients = addresses.length;
		final InternetAddress[] address = new InternetAddress[numRecipients];

		for (int i = 0; i < numRecipients; i++) {
			address[i] = addresses[i].toInternetAddress();
		}
		return address;
	}
}