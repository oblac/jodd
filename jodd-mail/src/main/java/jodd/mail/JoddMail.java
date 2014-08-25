// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.Jodd;

/**
 * Jodd MAIL module.
 */
public class JoddMail {

	/**
	 * Mail system properties for fine-tuning the java Mail behavior.
	 */
	public static MailSystem mailSystem = new MailSystem();

	// ---------------------------------------------------------------- module

	static {
		init();
	}

	public static void init() {
		Jodd.init(JoddMail.class);
	}

}