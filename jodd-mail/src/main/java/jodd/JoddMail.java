// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import jodd.mail.MailSystem;

/**
 * Jodd MAIL module.
 */
public class JoddMail {

	static {
		Jodd.module();
	}

	/**
	 * Mail system properties for fine-tuning the java Mail behavior.
	 */
	public static MailSystem mailSystem = new MailSystem();

}