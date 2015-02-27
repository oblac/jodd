// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

/**
 * Create {@link ReceiveMailSession email receiving sessions}.
 */
public interface ReceiveMailSessionProvider {

	/**
	 * Creates new receiving mail session.
	 */
	ReceiveMailSession createSession();
}
