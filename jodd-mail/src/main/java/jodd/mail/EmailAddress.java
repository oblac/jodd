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
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;

/**
 * Storage for personal name and email address.
 * Serves as a email address adapter between various formats.
 */
public class EmailAddress {

	public static final EmailAddress[] EMPTY_ARRAY = new EmailAddress[0];

	private final String email;
	private final String personalName;

	/**
	 * Creates new address by specifying email and personal name.
	 */
	public EmailAddress(String personalName, String email) {
		this.email = email;
		this.personalName = personalName;
	}

	/**
	 * Creates new address by specifying one of the following:
	 * <ul>
	 *     <li>"foo@bar.com" - only email address.</li>
	 *     <li>"Jenny Doe &lt;foo@bar.com&gt;" - first part of the string is personal name,
	 *     and the other part is email, surrounded with &lt; and &gt;.</li>
	 * </ul>
	 */
	public EmailAddress(String address) {
		address = address.trim();

		if (!StringUtil.endsWithChar(address, '>')) {
			this.email = address;
			this.personalName = null;
			return;
		}

		int ndx = address.lastIndexOf('<');
		if (ndx == -1) {
			this.email = address;
			this.personalName = null;
			return;
		}

		this.email = address.substring(ndx + 1, address.length() - 1);
		this.personalName = address.substring(0, ndx).trim();
	}

	/**
	 * Creates new email address from <code>InternetAddress</code>.
	 */
	public EmailAddress(InternetAddress internetAddress) {
		this.personalName = internetAddress.getPersonal();
		this.email = internetAddress.getAddress();
	}

	/**
	 * Creates new email address from <code>Address</code>.
	 */
	public EmailAddress(Address address) {
		this(address.toString());
	}

	// ---------------------------------------------------------------- getters

	/**
	 * Returns email address.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Returns personal name, may be <code>null</code>.
	 */
	public String getPersonalName() {
		return personalName;
	}


	// ---------------------------------------------------------------- convert

	/**
	 * Creates new <code>InternetAddress</code> from current data.
	 */
	public InternetAddress toInternetAddress() throws AddressException {
		try {
			return new InternetAddress(email, personalName, JoddCore.encoding);
		} catch (UnsupportedEncodingException ueex) {
			throw new AddressException(ueex.toString());
		}
	}

	/**
	 * Returns string representation of this email.
	 */
	public String toString() {
		if (this.personalName == null) {
			return this.email;
		}
		return this.personalName + " <" + this.email + '>';
	}

	// ---------------------------------------------------------------- arrays

	/**
	 * Converts array of <code>Address</code> to {@link EmailAddress}.
	 */
	public static EmailAddress[] createFrom(Address... addresses) {
		if (addresses == null) {
			return EmailAddress.EMPTY_ARRAY;
		}
		if (addresses.length == 0) {
			return EmailAddress.EMPTY_ARRAY;
		}

		EmailAddress[] res = new EmailAddress[addresses.length];

		for (int i = 0; i < addresses.length; i++) {
			res[i] = new EmailAddress(addresses[i]);
		}

		return res;
	}

	/**
	 * Converts array of <code>String</code> to {@link EmailAddress}.
	 */
	public static EmailAddress[] createFrom(String... addresses) {
		if (addresses == null) {
			return EmailAddress.EMPTY_ARRAY;
		}
		if (addresses.length == 0) {
			return EmailAddress.EMPTY_ARRAY;
		}

		EmailAddress[] res = new EmailAddress[addresses.length];

		for (int i = 0; i < addresses.length; i++) {
			res[i] = new EmailAddress(addresses[i]);
		}

		return res;
	}
}