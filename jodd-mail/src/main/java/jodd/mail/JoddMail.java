// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail;

import jodd.Jodd;
import jodd.mail.MailSystem;

/**
 * Jodd MAIL module.
 */
public class JoddMail {

	static {
		init();
	}

	public static void init() {
		Jodd.init(JoddMail.class);
	}

	/**
	 * Mail system properties for fine-tuning the java Mail behavior.
	 */
	public static MailSystem mailSystem = new MailSystem();

}