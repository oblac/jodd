// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

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
public class MailAddress {

	public static final MailAddress[] EMPTY_ARRAY = new MailAddress[0];

	private final String email;
	private final String personalName;

	/**
	 * Creates new address by specifying email and personal name.
	 */
	public MailAddress(String personalName, String email) {
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
	public MailAddress(String address) {
		address = address.trim();

		if (StringUtil.endsWithChar(address, '>') == false) {
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
		this.personalName = address.substring(0, ndx - 1).trim();
	}

	/**
	 * Creates new email address from {@link jodd.mail.EmailAddress}.
	 */
	public MailAddress(EmailAddress emailAddress) {
		this.personalName = emailAddress.getPersonalName();
		this.email = emailAddress.getLocalPart() + '@' + emailAddress.getDomain();
	}

	/**
	 * Creates new email address from <code>InternetAddress</code>.
	 */
	public MailAddress(InternetAddress internetAddress) {
		this.personalName = internetAddress.getPersonal();
		this.email = internetAddress.getAddress();
	}

	/**
	 * Creates new email address from <code>Address</code>.
	 */
	public MailAddress(Address address) {
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
	 * Creates new {@link jodd.mail.EmailAddress}.
	 */
	public EmailAddress toEmailAddress() {
		return new EmailAddress(toString());
	}

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
	 * Converts array of <code>Address</code> to {@link MailAddress}.
	 */
	public static MailAddress[] createFrom(Address[] addresses) {
		if (addresses == null) {
			return null;
		}
		if (addresses.length == 0) {
			return null;
		}

		MailAddress[] res = new MailAddress[addresses.length];

		for (int i = 0; i < addresses.length; i++) {
			res[i] = new MailAddress(addresses[i]);
		}

		return res;
	}

	/**
	 * Converts array of <code>String</code> to {@link MailAddress}.
	 */
	public static MailAddress[] createFrom(String[] addresses) {
		if (addresses == null) {
			return null;
		}
		if (addresses.length == 0) {
			return null;
		}

		MailAddress[] res = new MailAddress[addresses.length];

		for (int i = 0; i < addresses.length; i++) {
			res[i] = new MailAddress(addresses[i]);
		}

		return res;
	}

	/**
	 * Converts array of {@link jodd.mail.EmailAddress} to {@link MailAddress}.
	 */
	public static MailAddress[] createFrom(EmailAddress[] addresses) {
		if (addresses == null) {
			return null;
		}
		if (addresses.length == 0) {
			return null;
		}

		MailAddress[] res = new MailAddress[addresses.length];

		for (int i = 0; i < addresses.length; i++) {
			res[i] = new MailAddress(addresses[i]);
		}

		return res;
	}

}