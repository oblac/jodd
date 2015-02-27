// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

/**
 * Create {@link jodd.mail.SendMailSession email seding sessions}.
 */
public interface SendMailSessionProvider {

	/**
	 * Creates new sending mail session.
	 */
	SendMailSession createSession();
}
