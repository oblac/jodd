// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Performs simple authentication when the server requires it.
 */
public class SimpleAuthenticator extends Authenticator {

	protected final String username;
	protected final String password;

	public SimpleAuthenticator(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password);
	}
}

