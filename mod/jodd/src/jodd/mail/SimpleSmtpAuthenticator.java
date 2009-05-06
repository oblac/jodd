// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Performs simple authentication when the SMTP server requires it.
 */
public class SimpleSmtpAuthenticator extends Authenticator {

	protected final String username;
	protected final String password;

	public SimpleSmtpAuthenticator(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password);
	}
}

